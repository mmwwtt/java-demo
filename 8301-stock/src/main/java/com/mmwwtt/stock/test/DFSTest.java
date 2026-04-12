package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StrategyTmpDAO;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.enums.FilterFieldEnum;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyTmpServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.CommonDataService.*;


//todo  根据策略预测的时候也要加权处理
//todo  不同市值的公司的主升浪逻辑也不会一样， 应该区别计算
@Slf4j
@SpringBootTest
public class DFSTest {
    private static final int BATCH_SAVE_SIZE = 100;

    @Resource
    private StrategyTmpServiceImpl strategyTmpService;

    @Resource
    private StrategyTmpDAO strategyTmpDAO;

    @Resource
    private StrategyServiceImpl strategyService;

    @Resource
    private CommonDataService commonDataService;

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
    public static FilterFieldEnum fieldEnum = FilterFieldEnum.RISE3_MAX_MIDDLE_50_DAY;

    @Test
    @DisplayName("DFS深度遍历")
    public void dfs() throws InterruptedException, ExecutionException {
        commonDataService.init();
        DfsMain();
        dfsAfterDetail();
    }


    @Test
    @DisplayName("重新填充dfs遍历后的数据，生成最终策略数据")
    public void dfsAfter() throws ExecutionException, InterruptedException {
        commonDataService.init();
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
                    strategyTmp.setMiddle(fieldEnum.getL1MiddleGetter().apply(strategyL1));
                    strategyTmp.setMinMiddle(fieldEnum.getL1MinMiddleGetter().apply(strategyL1));
                    strategyTmp.getStrategyCodeSet().add(strategyTmp.getStrategyCode());
                    strategyTmp.setFieldEnumCode(fieldEnum.getCode());
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


            //计算并集中筛选字段的属性值
            StrategyTmp resStrategyTmp = new StrategyTmp(strategyL1, strategyTmp, resDetailIdArr);
            resStrategyTmp.setFieldEnumCode(fieldEnum.getCode());
            for (int detailId : resDetailIdArr) {
                resStrategyTmp.addToResult(detailArr[detailId]);
            }

            resStrategyTmp.fillDateSumMap();
            //进行天数过滤
            if (!fieldEnum.checkDateCnt(resStrategyTmp)) {
                continue;
            }
            resStrategyTmp.fillFilterField(fieldEnum);
            resStrategyTmp.clearCacheDate();

            //进行阈值过滤 和 数据保存
            if (!fieldEnum.checkLimit(resStrategyTmp)) {
                continue;
            }

            idxToTmpMap.put(idx, resStrategyTmp);
        }
        //取阈值最高的30条策略继续进行递归
        List<Map.Entry<Integer, StrategyTmp>> tmpList = idxToTmpMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(StrategyTmp::getMiddle).reversed()))
                .limit(fieldEnum.getTopLimit()).toList();
        boolean isContinue = level + 1 <= fieldEnum.getLevelLimit();
        idxToTmpMap.clear();
        tmpList.forEach(entry -> {
            StrategyTmp tmp = entry.getValue();
            Integer idx = entry.getKey();
            tmp.fillCode();
            addToTmpBatch(tmp);
            if (!isContinue) {
                return;
            }
            //递归 线程池有空余线程时用多线程处理
            if (level <= 5 && taskCnt.get() < cpuThreadPool.getCorePoolSize() - 1) {
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
        QueryWrapper<StrategyTmp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("field_enum_code", fieldEnum.getCode());
        strategyTmpService.remove(queryWrapper);
        dfsStrategyL1s = CommonDataService.strategyL1s.stream()
                .filter(item -> item.getName().startsWith("T0")
                        || item.getName().startsWith("T1")
                        || item.getName().startsWith("T2"))
                .sorted(Comparator.comparingInt((StrategyL1 s) -> s.getDetailIdArray().size()))
                .toList();
        log.info("dfs 初始化结束");
    }

    private void dfsAfterDetail() throws ExecutionException, InterruptedException {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("field_enum_code", fieldEnum.getCode());
        strategyService.remove(wrapper);
        List<StrategyTmp> strategyTmps = strategyTmpDAO.getAfterTmp(fieldEnum.getCode(), 2000);
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
                strategy.setStrategyId(null);
                strategy.setDetails(details);
                strategy.fillOtherData();
                resList.add(strategy);
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        //当两个策略重复度高达95%时， 字段阈值高的有效， 低的则改成失效状态  从而避免策略重复
        futures = new ArrayList<>();
        for (Strategy strategy1 : resList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Strategy strategy2 : resList) {
                    if (Objects.equals(strategy1.getStrategyCode(), strategy2.getStrategyCode())
                            || !strategy1.getIsActive() || !strategy2.getIsActive()
                            || strategy1.getDetailCnt() < strategy2.getDetailCnt() * 0.94
                            || strategy2.getDetailCnt() < strategy1.getDetailCnt() * 0.94) {
                        continue;
                    }
                    double repeatPerc = getRepeatPerc(strategy1.getDetailIdArr(), strategy2.getDetailIdArr());
                    if (moreThan(repeatPerc, 0.9)) {
                        Double pert1 = strategy1.getRise5MaxMiddle();
                        Double pert2 = strategy2.getRise5MaxMiddle();
                        if (moreThan(pert1, pert2)) {
                            strategy2.setIsActive(false);
                            continue;
                        }
                        if (lessThan(pert1, pert2)) {
                            strategy1.setIsActive(false);
                            continue;
                        }
                        if (isEquals(pert1, pert2)) {
                            if (strategy1.getDateCnt() > strategy2.getDateCnt()) {
                                strategy2.setIsActive(false);
                            } else {
                                strategy1.setIsActive(false);
                            }
                        }
                    }
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        List<Strategy> res = resList.stream().filter(Strategy::getIsActive).toList();

        List<List<Strategy>> parts = ListUtils.partition(res, 100);
        futures = new ArrayList<>();
        for (List<Strategy> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                part.forEach(this::verifyPredictRes);
                strategyService.saveBatch(part);
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
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


    public void verifyPredictRes(Strategy strategy) {
        strategy.getStrategyCodeSet().addAll(List.of(strategy.getStrategyCode().split(" ")));
        Map<String, List<Detail>> dataToDetailsMap = new ConcurrentHashMap<>();
        codeToDetailMap.forEach((key, value) -> {
            for (Detail detail : value) {
                if (detail.getDealDate().compareTo(calcEndDate) <= 0) {
                    break;
                }
                if (Objects.isNull(detail.getRise5Max())
                        || Objects.isNull(detail.getT10())
                        || Objects.isNull(detail.getT10().getSixtyDayLine())
                        || moreThan(detail.getRise0(), 0.097)) {
                    continue;
                }
                List<Function<Detail, Boolean>> filterFuncs = strategy.getStrategyCodeSet().stream()
                        .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
                if (CollectionUtils.isEmpty(filterFuncs)) {
                    continue;
                }
                boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                if (res) {
                    dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> new ArrayList<>()).add(detail);
                }
            }
        });

        double rise3DateAvgSum = 0;
        double rise3MaxDateAvgSum = 0;
        double rise5DateAvgSum = 0;
        double rise5MaxDateAvgSum = 0;
        int dateCnt = 0;
        for (String date : predictDateList) {
            List<Detail> details = dataToDetailsMap.getOrDefault(date, null);
            if (CollectionUtils.isEmpty(details)) {
                continue;
            }
            dateCnt++;
            double rise3Sum = 0;
            double rise3MaxSum = 0;
            double rise5Sum = 0;
            double rise5MaxSum = 0;
            for (Detail detail : details) {

                rise3Sum += detail.getRise3();
                rise3MaxSum += detail.getRise3Max();

                rise5Sum += detail.getRise5();
                rise5MaxSum += detail.getRise5Max();
            }
            int size = details.size();
            double rise3DateAvg = divide(rise3Sum, size);
            double rise3MaxDateAvg = divide(rise3MaxSum, size);
            double rise5DateAvg = divide(rise5Sum, size);
            double rise5MaxDateAvg = divide(rise5MaxSum, size);
            rise3DateAvgSum += rise3DateAvg;
            rise3MaxDateAvgSum += rise3MaxDateAvg;
            rise5DateAvgSum += rise5DateAvg;
            rise5MaxDateAvgSum += rise5MaxDateAvg;
        }
        if (isEquals(0d, rise3DateAvgSum)) {
            return;
        }
        strategy.setPredictDateCnt(dateCnt);
        strategy.setPredictRise3Avg(rise3DateAvgSum / dateCnt);
        strategy.setPredictRise3MaxAvg(rise3MaxDateAvgSum / dateCnt);
        strategy.setPredictRise5Avg(rise5DateAvgSum / dateCnt);
        strategy.setPredictRise5MaxAvg(rise5MaxDateAvgSum / dateCnt);
        dataToDetailsMap.clear();
    }
}