package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StrategyTmpDAO;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.enums.FilterFildEnum;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyTmpServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.CommonDataService.*;


//todo  根据策略预测的时候也要加权处理
//todo  不同市值的公司的主升浪逻辑也不会一样， 应该区别计算
@Slf4j
@SpringBootTest
public class DFSTest {
    private static final int BATCH_SAVE_SIZE = 100;

    @Autowired
    private StrategyTmpServiceImpl strategyTmpService;

    @Autowired
    private StrategyTmpDAO strategyTmpDAO;

    @Autowired
    private StrategyServiceImpl strategyService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = GlobalThreadPool.singleThreadPool;

    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyTmp> tmpBatch = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger taskCnt = new AtomicInteger(0);
    public static List<StrategyL1> dfsStrategyL1s;

    /**
     * DFS 过滤策略
     */
    public static FilterFildEnum fildEnum = FilterFildEnum.RISE5_MAX_MIDDLE_025;

    @Test
    @DisplayName("DFS深度遍历")
    public void dfs() throws InterruptedException, ExecutionException {
        DfsMain();
        dfsAfterDetail();
    }


    @Test
    @DisplayName("重新填充dfs遍历后的数据，生成最终数据")
    public void dfsAfter() throws ExecutionException, InterruptedException {
        dfsAfterDetail();
    }

    public void DfsMain() throws InterruptedException {
        dfsInit();
        for (int i = 0; i < dfsStrategyL1s.size(); i++) {
            StrategyL1 strategyL1 = dfsStrategyL1s.get(i);
            int finalI = i;
            taskCnt.incrementAndGet();
            CompletableFuture.runAsync(() -> {
                try {
                    StrategyTmp strategyTmp = VoConvert.INSTANCE.convertTo(strategyL1);
                    strategyTmp.setMaxMiddle(strategyL1.getRise5MaxMiddle());
                    strategyTmp.setMinMiddle(strategyL1.getRise5MinMiddle());
                    strategyTmp.getStrategyCodeSet().add(strategyTmp.getStrategyCode());
                    if (Objects.nonNull(strategyL1.getType())) {
                        strategyTmp.getStrategyTypeSet().add(strategyL1.getType());
                    }
                    buildByLevel(strategyTmp, finalI);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    taskCnt.decrementAndGet();
                }
            }, cpuThreadPool);
        }
        while (taskCnt.get() != 0) {
            log.info("任务数 taskCnt:{}", taskCnt.get());
            Thread.sleep(10000);
        }
        flushWinBatch();
    }


    private void buildByLevel(StrategyTmp strategyTmp, Integer parentIdx) {
        int level = strategyTmp.getStrategyCodeSet().size() + 1;
        Map<Integer, StrategyTmp> idxToTmpMap = new ConcurrentHashMap<>(INIT_DATE_SIZE);
        for (int idx = parentIdx + 1; idx < dfsStrategyL1s.size(); idx++) {

            //已存在的策略  或者策略类型相同  则跳过
            StrategyL1 strategyL1 = dfsStrategyL1s.get(idx);
            if (strategyTmp.getStrategyCodeSet().contains(strategyL1.getStrategyCode())) {
                continue;
            }

            if (Objects.nonNull(strategyL1.getType()) && strategyTmp.getStrategyTypeSet().contains(strategyL1.getType())) {
                continue;
            }

            //l1层无符合数据 则跳过
            int[] l1DetailIdArr = strategyL1.getDetailIdArr();
            if (l1DetailIdArr == null) {
                continue;
            }

            //交集为0 则跳过
            int[] resDetailIdArr = retainAll(strategyTmp.getDetailIdArr(), l1DetailIdArr);
            if (resDetailIdArr.length < 50) {
                continue;
            }


            //计算并集中筛选字段的属性值
            StrategyTmp resStrategyTmp = new StrategyTmp(strategyL1, strategyTmp, resDetailIdArr);
            for (int detailId : resDetailIdArr) {
                resStrategyTmp.addToResult(detailArr[detailId]);
            }
            resStrategyTmp.fillFilterField(fildEnum);
            resStrategyTmp.getDetails().clear();

            //进行阈值过滤 和 数据保存
            if (!fildEnum.getIsConformity().apply(resStrategyTmp)) {
                continue;
            }

            idxToTmpMap.put(idx, resStrategyTmp);
        }
        //取阈值最高的30条策略继续进行递归
        List<Map.Entry<Integer, StrategyTmp>> tmpList = idxToTmpMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(StrategyTmp::getMaxMiddle).reversed()))
                .limit(30).toList();
        boolean isContinue = level + 1 <= fildEnum.getLevelLimit();
        idxToTmpMap.clear();
        tmpList.forEach(entry -> {
            StrategyTmp tmp = entry.getValue();
            Integer idx = entry.getKey();
            tmp.fillCode();
            addToTmpBatch(tmp);
            if(!isContinue) {
                return;
            }
            //递归 线程池有空余线程时用多线程处理
            if (level <= 5 && taskCnt.get() < cpuThreadPool.getCorePoolSize() * 1.5) {
                taskCnt.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        buildByLevel(tmp, idx);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        taskCnt.decrementAndGet();
                    }
                }, cpuThreadPool);
            } else {
                buildByLevel(tmp, idx);
            }
        });

    }

    private void dfsInit() {
        log.info("dfs 初始化");
        strategyTmpService.remove(new QueryWrapper<>());
        dfsStrategyL1s = CommonDataService.strategyL1s.stream()
                .filter(item -> moreThan(item.getRise5MaxMiddle(), 0.025))
                .filter(item -> item.getName().startsWith("T0")
                        || item.getName().startsWith("T1")
                        || item.getName().startsWith("T2"))
                .sorted(Comparator.comparingInt((StrategyL1 s) -> s.getDetailIdArray().size()))
                .toList();
        log.info("dfs 初始化结束");
    }

    private void dfsAfterDetail() throws ExecutionException, InterruptedException {
        strategyService.remove(new QueryWrapper<>());
        List<StrategyTmp> strategyTmps = strategyTmpDAO.getAfterTmp();
        List<Strategy> resList = Collections.synchronizedList(new ArrayList<>(5000));
        //统计每个策略符合的detail  对各种属性进行填充
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyTmp strategyTmp : strategyTmps) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                strategyTmp.setStrategyCodeSet(Arrays.stream(strategyTmp.getStrategyCode().split(" ")).filter(s -> !s.isEmpty()).collect(Collectors.toSet()));
                List<int[]> detailArrList = strategyTmp.getStrategyCodeSet().stream().map(code -> codeToL1Map.get(code).getDetailIdArr()).toList();
                int[] resDetailIds = retainAll(detailArrList);
                List<Detail> details = Arrays.stream(resDetailIds).mapToObj(detailId -> detailArr[detailId]).toList();
                Strategy strategy = VoConvert.INSTANCE.convertTo(strategyTmp);
                strategy.setDetails(details);
                strategy.fillOtherData();
                resList.add(strategy);
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        strategyService.saveBatch(resList);

        //当两个策略重复度高达95%时， 字段阈值高的有效， 低的则改成失效状态  从而避免策略重复
        futures = new ArrayList<>();
        for (Strategy strategy1 : resList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Strategy strategy2 : resList) {
                    if (Objects.equals(strategy1.getStrategyId(), strategy2.getStrategyId())
                            || !strategy2.getIsActive() || strategy1.getDetailCnt() < strategy2.getDetailCnt() * 0.94
                            || strategy2.getDetailCnt() < strategy1.getDetailCnt() * 0.94) {
                        continue;
                    }
                    double repeatPerc = getRepeatPerc(strategy1.getDetailIdArr(), strategy2.getDetailIdArr());
                    if (moreThan(repeatPerc, 0.95)) {
                        Double pert1 = strategy1.getRise5MaxMiddle();
                        Double pert2 = strategy2.getRise5MaxMiddle();
                        if (isEquals(pert1, pert2)) {
                            if (strategy1.getDateCnt() > strategy2.getDateCnt()) {
                                strategy2.setIsActive(false);
                            } else {
                                strategy1.setIsActive(false);
                            }
                        }
                        if (moreThan(pert1, pert2)) {
                            strategy2.setIsActive(false);
                        }
                        if (lessThan(pert1, pert2)) {
                            strategy1.setIsActive(false);
                        }
                    }
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        strategyService.saveOrUpdateBatch(resList);
    }


    private void addToTmpBatch(StrategyTmp tmpData) {
        synchronized (tmpBatch) {
            tmpBatch.add(tmpData);
            if (tmpBatch.size() >= BATCH_SAVE_SIZE) {
                flushWinBatch();
            }
        }
    }

    private void flushWinBatch() {
        List<StrategyTmp> toSave;
        synchronized (tmpBatch) {
            if (tmpBatch.isEmpty())
                return;
            toSave = new ArrayList<>(tmpBatch);
            strategyTmpService.saveBatch(toSave);
            tmpBatch.clear();
            for (StrategyTmp t : toSave) {
                t.getDetails().clear();
            }
        }
    }

}