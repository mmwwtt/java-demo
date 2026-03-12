package com.mmwwtt.stock.test;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;

/**
 * 深度优先遍历各种策略
 * 区间60向上 区间40_20_30 下影线长度_08_09   是100%胜率
 * l1 cnt限值不能大于50
 * <p>
 * 收益口径说明：
 * - fiveMaxPercRate（5日最高）= 若在接下来5日内的最高价卖出，理论涨幅；实盘很难卖在最高，通常达不到。
 * - fivePercRate（5日收盘）= 持有5日、按第5日收盘价卖出，更接近实盘，可用 buildStrateResultByFiveClose 按此口径选策略。
 * <p>
 * 过拟合说明：buildStrateResultAll 在全历史上做交集，条件越多样本越少，fiveMax 越容易“凑”出好看数字。
 * 缓解：① 提高 cnt 下限、拒绝胜率过高 ② 同时要求 fivePercRate 达标 ③ 限制层数 ④ 用 buildStrateResultAllAntiOverfit
 */
@Slf4j
@SpringBootTest
public class DFSTest {

    private static final int CNT_THRESHOLD = 100;
    private static final int BATCH_SAVE_SIZE = 50;

    // 防止过拟合的参数
    private static final int ANTI_OVERFIT_CNT_THRESHOLD = 150; // 更高的样本量要求
    private static final BigDecimal MIN_FIVE_PERC_RATE = new BigDecimal("0.03"); // 5日收盘价涨幅下限
    private static final BigDecimal MAX_WIN_RATE = new BigDecimal("0.95"); // 胜率上限，拒绝过于完美的策略
    private static final int TRAIN_TEST_SPLIT_YEAR = 2023; // 训练集和测试集的分割年份

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);

    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        winBatch.clear();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<StrategyWin> l1WinList = l1StrategyList.stream()
                .filter(item -> moreThan(item.getFiveMaxPercRate(), "0.04"))
                .filter(item -> item.getStrategyName().startsWith("T0")
                        || item.getStrategyName().startsWith("T1")
                        //|| item.getStrategyName().startsWith("T2")
                        //|| item.getStrategyName().startsWith("T3")
                )
                .sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                LinkedHashSet<String> strategySet = new LinkedHashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevel(2, stockCodeToDateSetMap, strategySet, strategyWin, finalI);
            }, fixedThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        flushWinBatch();
    }


    private void buildByLevel(Integer level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                              LinkedHashSet<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 4) {
            return;
        }
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }

            Map<String, Set<Integer>> curStockToDetailIdSetMap = copyStockToDetailIdMap(stockToDetailIdSetMap);
            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));

            int totalCnt = curStockToDetailIdSetMap.values().stream().mapToInt(Set::size).sum();
            if (totalCnt < CNT_THRESHOLD) {
                continue;
            }

            LinkedHashSet<String> curStrategyCodeSet = new LinkedHashSet<>();
            curStrategyCodeSet.add(strategy.getStrategyCode());
            curStrategyCodeSet.addAll(strategySet);
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curStockToDetailIdSetMap);
            if (isNotByFiveMax(win, parentWin, level)) {
                continue;
            }
            addToWinBatch(win);
            buildByLevel(level + 1, curStockToDetailIdSetMap, curStrategyCodeSet, win, i);
        }
    }



    /**
     * 轻量复制 Map，避免 MapStruct DeepClone 开销
     */
    private Map<String, Set<Integer>> copyStockToDetailIdMap(Map<String, Set<Integer>> source) {
        Map<String, Set<Integer>> copy = new HashMap<>(source.size());
        source.forEach((k, v) -> copy.put(k, new HashSet<>(v)));
        return copy;
    }

    private void addToWinBatch(StrategyWin win) {
        winBatch.add(win);
        if (winBatch.size() >= BATCH_SAVE_SIZE) {
            flushWinBatch();
        }
    }

    private void flushWinBatch() {
        if (winBatch.isEmpty()) return;
        List<StrategyWin> toSave = new ArrayList<>(winBatch);
        winBatch.clear();
        strategyWinService.saveBatch(toSave);
    }

    private StrategyWin saveStrategyWin(LinkedHashSet<String> strategyCodeSet, Map<String, Set<Integer>> stockToDetailIdSetMap) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        stockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                detailIdSet.forEach(detailId -> win.addToResult(idToDetailMap.get(detailId))));
        win.fillData();
        return win;
    }

    private boolean isNotByFiveMax(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < CNT_THRESHOLD || lessThan(win.getFiveMaxPercRate(), "0.05")) {
            return true;
        }
        if (lessAndEqualsThan(win.getFiveMaxPercRate(), parentWin.getFiveMaxPercRate())
                || lessThan(win.getFiveMaxPercRate(), multiply(parentWin.getFiveMaxPercRate(), 1.05))
                || (level == 2 && lessThan(win.getFiveMaxPercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxPercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"))
        ) {
            return true;
        }
        return false;
    }



}