package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.enums.FilterFildEnum;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyL1ServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyTmpServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;


//todo 对结果进行再处理 找出符合结果的详情列表， 判断重复度>80%的如何处理
//todo 策略枚举不采用区间隔离，而是存在重叠当相同类型的枚举则跳过筛选
//todo 优化验证，当同一个数据被多个策略选中时，增加该数据的权重 再计算涨幅
//todo next5MaxPertRate 感觉这个字段不是很对
//todo 分成1层表    DFS中间表      策略结果表
@Slf4j
@SpringBootTest
public class DFSTest {
    private static final int BATCH_SAVE_SIZE = 1000;

    @Autowired
    private StrategyTmpServiceImpl strategyTmpService;

    @Autowired
    private StrategyL1ServiceImpl strategyL1Service;

    @Autowired
    private StrategyServiceImpl strategyService;

    @Resource
    private CommonService commonService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();


    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyTmp> tmpBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyL1> strategyL1s;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(4000000);
    private final AtomicInteger taskCnt = new AtomicInteger(0);
    private static int LEVEL_LIMIT;
    public static FilterFildEnum fildEnum;

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        buildStrateResultLevel1();
    }

    @Test
    @DisplayName("DFS深度遍历 - 五日最大涨幅的中位数")
    public void dfsFor5MaxMid() throws InterruptedException {
        fildEnum = FilterFildEnum.RISE5_MAX_MIDDLE;
        DfsMain(3);
    }

    @Test
    @DisplayName("填充DFS后策略的其他字段数据， 并对策略的详情列表做交集，判断是否高度重合，并做处理")
    public void dfsAfter() throws ExecutionException, InterruptedException {
        dfsAfterDetail();
    }

    public void DfsMain(int levelLimit) throws InterruptedException {
        dfsInit();
        LEVEL_LIMIT = levelLimit;
        for (int i = 0; i < strategyL1s.size(); i++) {
            StrategyL1 strategyL1 = strategyL1s.get(i);
            int[] detailIds = strategyL1.getDetailIdArr();
            int finalI = i;
            taskCnt.incrementAndGet();
            CompletableFuture.runAsync(() -> {
                try {
                    StrategyTmp strategyTmp = VoConvert.INSTANCE.convertTo(strategyL1);
                    buildByLevel(detailIds, strategyTmp, finalI);
                } finally {
                    taskCnt.decrementAndGet();
                }
            }, cpuThreadPool);
        }
        while (taskCnt.get() != 0) {
            Thread.sleep(10000);
            log.info("任务数 taskCnt:{}", taskCnt.get());
        }
        flushWinBatch();
        log.info("md5Map数量 ： {}", md5ToLevelMap.size());
    }


    private void buildByLevel(int[] detailIds, StrategyTmp strategyTmp, Integer curIdx) {
        int level = strategyTmp.getStrategyCodeSet().size() + 1;
        if (level > LEVEL_LIMIT) {
            return;
        }
        for (int i = curIdx + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (parentWin.getStrategyCodeSet().contains(strategy.getStrategyCode())) {
                continue;
            }
            int[] curDetailIds = strategyCodeToDetailsMap.get(strategy.getStrategyCode());
            if (curDetailIds == null) {
                continue;
            }
            int[] curRetainAllDetailIds = retainAll(parentDetailIds, curDetailIds);

            String md5Key = getMd5Key(curRetainAllDetailIds);
            Integer newLevel = md5ToLevelMap.merge(md5Key, level, Math::max);
            if (newLevel > level) {
                continue;
            }

            StrategyWin win = calcStrategyWin(parentWin.getStrategyCodeSet(),
                    fildEnum.getWinGetter().apply(parentWin),
                    strategy.getStrategyCode(), curRetainAllDetailIds, fildEnum);

            if (!fildEnum.getFunc().apply(win)) {
                continue;
            }
            win.fillLevelAndName();
            addToWinBatch(win);
            if (level < LEVEL_LIMIT && taskCnt.get() < cpuThreadPool.getCorePoolSize()) {
                int finalI = i;
                taskCnt.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        buildByLevel(curRetainAllDetailIds, win, finalI);
                    } finally {
                        taskCnt.decrementAndGet();
                    }
                }, cpuThreadPool);
            } else {
                buildByLevel(curRetainAllDetailIds, win, i);
            }
        }
    }

    private void dfsInit() {
        log.info("dfs 初始化");
        QueryWrapper<StrategyWin> wrapper = new QueryWrapper<>();
        wrapper.apply("level!=1");
        strategyWinService.remove(wrapper);
        tmpBatch.clear();
        md5ToLevelMap.clear();
        l1WinList = CommonService.strategyL1s.stream()
                .filter(item -> moreThan(item.getRise5MaxMiddle(), 0.025))
                .filter(item -> item.getStrategyName().startsWith("T0")
                        || item.getStrategyName().startsWith("T1")
                        || item.getStrategyName().startsWith("T2")
                        || item.getStrategyName().startsWith("T3")
                )
                .peek(item -> item.getStrategyCodeSet().add(item.getStrategyCode()))
                .sorted(Comparator.comparing(StrategyWin::getRise5MaxMiddle).reversed()).toList();
        log.info("dfs 初始化结束");
    }

    private void dfsAfterDetail() throws ExecutionException, InterruptedException {
        List<StrategyWin> strategyWins = strategyWinService.list(fildEnum.getWapper());

        Map<Long, List<Detail>> strategyIdToDetailIdsMap = new ConcurrentHashMap<>(strategyWins.size() * 2);

        strategyWins.forEach(strategyWin -> {
            strategyIdToDetailIdsMap.put(strategyWin.getStrategyWinId(), Collections.synchronizedList(new ArrayList<>(500)));
            strategyWin.setStrategyCodeSet(Arrays.stream(strategyWin.getStrategyCode().split(" ")).collect(Collectors.toSet()));
            strategyWin.getStrategyCodeSet().forEach(strategyCode ->
                    strategyWin.getFilterFuncs().add(StrategyEnum.codeToEnumMap.get(strategyCode).getFilterFunc()));
        });


        //统计每个策略符合的detail  对各种属性进行填充
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        strategyWins.forEach(strategyWin -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Detail> details = strategyIdToDetailIdsMap.get(strategyWin.getStrategyWinId());
                codeToDetailMap.values().forEach(detailList -> detailList.forEach(detail -> {
                    boolean isTrue = strategyWin.getFilterFuncs().stream().allMatch(func -> func.apply(detail));
                    if (isTrue) {
                        details.add(detail);
                    }
                }));
                strategyWin.setDetails(details);
                strategyWin.fillOtherData();
            }, cpuThreadPool);
            futures.add(future);
        });
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        //todo 对win进行重复度判断  如果detailIds重复度达到95%则抛弃胜率低的那条
    }


    private void addToWinBatch(StrategyWin win) {
        synchronized (tmpBatch) {
            tmpBatch.add(win);
            if (tmpBatch.size() >= BATCH_SAVE_SIZE) {
                flushWinBatch();
            }
        }
    }

    private void flushWinBatch() {
        List<StrategyWin> toSave;
        synchronized (tmpBatch) {
            if (tmpBatch.isEmpty())
                return;
            toSave = new ArrayList<>(tmpBatch);
            tmpBatch.clear();
        }
        strategyWinService.saveBatch(toSave);
    }

    private StrategyWin calcStrategyWin(Set<String> parentWinStrategyCodeSet, Double parentFieldValue,
                                        String curStrategyCode, int[] details, FilterFildEnum filterFildEnum) {
        StrategyWin win = new StrategyWin(curStrategyCode, parentWinStrategyCodeSet,
                parentFieldValue, details);
        for (int detail : details) {
            win.addToResult(idToDetailMap.get(detail));
        }
        win.fillFilterField(filterFildEnum);
        return win;
    }

    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        QueryWrapper<StrategyL1> winWrapper = new QueryWrapper<>();
        strategyL1Service.remove(winWrapper);

        QueryWrapper<StrategyTmp> resultWrapper = new QueryWrapper<>();
        strategyTmpService.remove(resultWrapper);

        List<StrategyEnum> values = StrategyEnum.dayForStrategyList;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyEnum strategy : values) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Detail> resDetails = new ArrayList<>();
                for (String stockCode : stockCodeList) {
                    List<Detail> details = codeToDetailMap.get(stockCode);
                    for (Detail detail : details) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getPricePert(), 0.097)
                                || detail.getDealDate().compareTo("202505") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategy.getFilterFunc().apply(detail)) {
                            resDetails.add(detail);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(resDetails) && resDetails.size() > 10) {
                    StrategyL1 strategyL1 = new StrategyL1(strategy.getCode(), resDetails);
                    strategyL1.fillOtherData();
                    strategyL1Service.save(strategyL1);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("策略层级 1 计算 - 结束");
    }
}