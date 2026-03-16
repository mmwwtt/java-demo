package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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


//todo 将判断字段如五日平均涨幅  作为枚举  便于跑结果
//todo 对结果进行再处理 找出符合结果的详情列表， 判断重复度>80%的如何处理
//todo 策略枚举不采用区间隔离，而是存在重叠当相同类型的枚举则跳过筛选
//todo 优化验证，当同一个数据被多个策略选中时，增加该数据的权重 再计算涨幅
@Slf4j
@SpringBootTest
public class DFSTest {

    private static final int CNT_THRESHOLD = 80;
    private static final int BATCH_SAVE_SIZE = 1000;

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    @Resource
    private CommonService commonService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();


    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(4000000);
    private final AtomicInteger taskCnt = new AtomicInteger(0);
    private static int LEVEL_LIMIT;

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        commonService.buildStrateResultLevel1();
    }

    @Test
    @DisplayName("DFS深度遍历 - 五日最大涨幅的平均值")
    public void DFS1() throws InterruptedException {
        DfsMain(FilterFildEnum.RISE5_MAX_AVG, StrategyWin::getRise5MaxAvg, 4);
    }

    @Test
    @DisplayName("DFS深度遍历 - 五日最大涨幅的中位数")
    public void DFS2() throws InterruptedException {
        DfsMain(FilterFildEnum.RISE5_MAX_MIDDLE, StrategyWin::getRise5MaxMiddle, 7);
    }


    public void DfsMain(FilterFildEnum fildEnum, Function<StrategyWin, Double> getter,
                        int levelLimit) throws InterruptedException {
        //调用getter方法
        //Double apply = getter.apply(new StrategyWin());

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
                    buildByLevel(detailIds, strategyWin, finalI, fildEnum);
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


    private void buildByLevel(int[] parentDetailIds, StrategyWin parentWin, Integer curIdx,
                              FilterFildEnum fildEnum) {
        int level = parentWin.getStrategyCodeSet().size() + 1;
        if (level > LEVEL_LIMIT) {
            return;
        }
        Function<StrategyWin, Double> getter = fildEnum.getGetter();
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

            StrategyWin win = calcStrategyWin(parentWin.getStrategyCodeSet(), getter.apply(parentWin),
                    strategy.getStrategyCode(), curRetainAllDetailIds, getter);

            if (fildEnum.getFunc().apply(win)) {
                continue;
            }
            win.fillData2();
            addToWinBatch(win);
            if (level < LEVEL_LIMIT && taskCnt.get() < cpuThreadPool.getCorePoolSize()) {
                int finalI = i;
                taskCnt.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        buildByLevel(curRetainAllDetailIds, win, finalI, fildEnum);
                    } finally {
                        taskCnt.decrementAndGet();
                    }
                }, cpuThreadPool);
            } else {
                buildByLevel(curRetainAllDetailIds, win, i, fildEnum);
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
        private final Function<StockDetail, Double> detailGetter;
        private final BiConsumer<StrategyWin,Double> winSetter;
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
                                        String curStrategyCode, int[] details, Function<StrategyWin, Double> getter) {
        StrategyWin win = new StrategyWin(curStrategyCode, parentWinStrategyCodeSet,
                parentFieldValue, details);
        for (int detail : details) {
            win.addToResult(idToDetailMap.get(detail));
        }
        win.fillData1(getter);
        return win;
    }

}