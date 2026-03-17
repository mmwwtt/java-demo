package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyResult;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyResultServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;


//todo 对结果进行再处理 找出符合结果的详情列表， 判断重复度>80%的如何处理
//todo 策略枚举不采用区间隔离，而是存在重叠当相同类型的枚举则跳过筛选
//todo 优化验证，当同一个数据被多个策略选中时，增加该数据的权重 再计算涨幅
//todo next5MaxPertRate 感觉这个字段不是很对
@Slf4j
@SpringBootTest
public class DFSTest {

    private static final int CNT_THRESHOLD = 80;
    private static final int BATCH_SAVE_SIZE = 1000;

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    @Autowired
    private StrategyResultServiceImpl strategyResultService;

    @Resource
    private CommonService commonService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();


    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
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
        DfsMain(7);
    }

    @Test
    @DisplayName("填充DFS后策略的其他字段数据， 并对策略的详情列表做交集，判断是否高度重合，并做处理")
    public void dfsAfter() {
        List<StrategyWin> list = strategyWinService.list(fildEnum.getWapper());
        list.forEach(win -> {
            List<Integer> detailIds = new ArrayList<>();
            detailIds.forEach(detailId -> win.addToResult(idToDetailMap.get(detailId)));
            win.fillOtherData();
        });
    }


    public void DfsMain(int levelLimit) throws InterruptedException {
        dfsInit();
        LEVEL_LIMIT = levelLimit;
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            int[] detailIds = strategyToDetailsMap.get(strategyWin.getStrategyCode());
            if (detailIds == null) {
                continue;
            }
            int finalI = i;
            taskCnt.incrementAndGet();
            CompletableFuture.runAsync(() -> {
                try {
                    buildByLevel(detailIds, strategyWin, finalI);
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


    private void buildByLevel(int[] parentDetailIds, StrategyWin parentWin, Integer curIdx) {
        int level = parentWin.getStrategyCodeSet().size() + 1;
        if (level > LEVEL_LIMIT) {
            return;
        }
        for (int i = curIdx + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (parentWin.getStrategyCodeSet().contains(strategy.getStrategyCode())) {
                continue;
            }
            int[] curDetailIds = strategyToDetailsMap.get(strategy.getStrategyCode());
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

            if (fildEnum.getFunc().apply(win)) {
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
        winBatch.clear();
        md5ToLevelMap.clear();
        l1WinList = l1StrategyList.stream()
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

    @AllArgsConstructor
    @Getter
    public enum FilterFildEnum implements BaseEnum {
        RISE5_MAX_MIDDLE("rise5MaxMiddle", "最大五日涨幅中位数",
                StockDetail::getNext5MaxPricePert,
                StrategyWin::setRise5MaxMiddle,
                StrategyWin::getRise5MaxMiddle,
                new QueryWrapper<StrategyWin>().apply("rise5_max_middle>0.14  and rise5_max_middle<0.15"),
                (StrategyWin win) -> {
                    if (win.getDateCnt() < CNT_THRESHOLD || lessThan(win.getRise5MaxMiddle(), 0.025)) {
                        return true;
                    }
                    int level = win.getStrategyCodeSet().size();
                    return lessThan(win.getRise5MaxMiddle(), multiply(win.getParentLowLimit(), 1.02))
                            || (level == 2 && lessThan(win.getRise5MaxMiddle(), 0.075))
                            || (level == 3 && lessThan(win.getRise5MaxMiddle(), 0.085))
                            || (level == 4 && lessThan(win.getRise5MaxMiddle(), 0.095))
                            || (level == 5 && lessThan(win.getRise5MaxMiddle(), 0.105))
                            || (level == 6 && lessThan(win.getRise5MaxMiddle(), 0.11))
                            || (level == 7 && lessThan(win.getRise5MaxMiddle(), 0.12));
                }),
        RISE5_MAX_AVG("rise5MaxAvg", "最大五日涨幅平均数",
                StockDetail::getNext5MaxPricePert,
                StrategyWin::setRise5MaxAvg,
                StrategyWin::getRise5MaxMiddle,
                new QueryWrapper<StrategyWin>().apply("rise5_max_avg>0.14  and rise5_max_avg<0.15"),
                (StrategyWin win) -> {
                    if (win.getDateCnt() < CNT_THRESHOLD || lessThan(win.getRise5MaxAvg(), 0.05)) {
                        return true;
                    }
                    int level = win.getStrategyCodeSet().size();
                    return lessThan(win.getRise5MaxAvg(), multiply(win.getParentLowLimit(), 1.01))
                            || (level == 2 && lessThan(win.getRise5MaxAvg(), 0.08))
                            || (level == 3 && lessThan(win.getRise5MaxAvg(), 0.09))
                            || (level == 4 && lessThan(win.getRise5MaxAvg(), 0.10))
                            || (level == 5 && lessThan(win.getRise5MaxAvg(), 0.11))
                            || (level == 6 && lessThan(win.getRise5MaxAvg(), 0.115))
                            || (level == 7 && lessThan(win.getRise5MaxAvg(), 0.12));
                }),

        ;
        private final String code;
        private final String desc;

        /**
         * getter setter方法
         */
        private final Function<StockDetail, Double> detailGetter;
        private final BiConsumer<StrategyWin, Double> winSetter;
        private final Function<StrategyWin, Double> winGetter;

        /**
         * 需要填充策略数据的查询sql
         */
        private final QueryWrapper<StrategyWin> wapper;

        /**
         * 策略过滤方法
         */
        private final Function<StrategyWin, Boolean> func;
    }


    private void addToWinBatch(StrategyWin win) {
        synchronized (winBatch) {
            winBatch.add(win);
            if (winBatch.size() >= BATCH_SAVE_SIZE) {
                flushWinBatch();
            }
        }
    }

    private void flushWinBatch() {
        List<StrategyWin> toSave;
        synchronized (winBatch) {
            if (winBatch.isEmpty())
                return;
            toSave = new ArrayList<>(winBatch);
            winBatch.clear();
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
        QueryWrapper<StrategyWin> winWrapper = new QueryWrapper<>();
        strategyWinService.remove(winWrapper);

        QueryWrapper<StrategyResult> resultWrapper = new QueryWrapper<>();
        strategyResultService.remove(resultWrapper);

        List<StrategyEnum> values = StrategyEnum.dayForStrategyList;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyEnum strategy : values) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Integer> dateList = new ArrayList<>();
                for (String stockCode : stockCodeList) {
                    List<StockDetail> stockDetails = codeToDetailMap.get(stockCode);
                    for (StockDetail detail : stockDetails) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getPricePert(), 0.097)
                                || detail.getDealDate().compareTo("202505") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategy.getRunFunc().apply(detail)) {
                            dateList.add(detail.getStockDetailId());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(dateList) && dateList.size() > 10) {
                    StrategyResult strategyResult = new StrategyResult(1, strategy.getCode(), dateList);
                    strategyResultService.save(strategyResult);
                }

            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("策略层级 1 计算 - 结束");

        //计算第一层的策略的win结果
        List<StrategyResult> results = strategyResultService.getStrategyResult(
                StrategyResult.builder().level(1).build());
        futures = new ArrayList<>();
        for (StrategyResult result : results) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                StrategyWin strategyWin = new StrategyWin(result.getStrategyCode());
                result.getStockDetailIdList().stream()
                        .map(item -> (Integer) item)
                        .forEach(item -> strategyWin.addToResult(idToDetailMap.get(item)));
                strategyWin.fillFilterField(fildEnum);
                strategyWin.fillLevelAndName();
                strategyWinService.save(strategyWin);
            }, ioThreadPool);
            futures.add(future);
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算第一层的策略的win结果 结束");
    }
}