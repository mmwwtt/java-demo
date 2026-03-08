package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;

/**
 * 深度优先遍历各种策略
 * 区间60向上 区间40_20_30 下影线长度_08_09   是100%胜率
 * l1 cnt限值不能大于50
 *
 * 收益口径说明：
 * - fiveMaxPercRate（5日最高）= 若在接下来5日内的最高价卖出，理论涨幅；实盘很难卖在最高，通常达不到。
 * - fivePercRate（5日收盘）= 持有5日、按第5日收盘价卖出，更接近实盘，可用 buildStrateResultByFiveClose 按此口径选策略。
 */
@Slf4j
@SpringBootTest
public class DFSTest {

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final VoConvert voConvert = VoConvert.INSTANCE;

    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1StrategyList.size(); i++) {
            StrategyWin strategyWin = l1StrategyList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Set<String> strategySet = new HashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevel(2, stockCodeToDateSetMap, strategySet, strategyWin, finalI);
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    /** 按 5日收盘涨幅 剪枝的 DFS，选出的策略数字更接近实盘（持有5日按收盘卖），避免 5日最高 理论值虚高。 */
    @Test
    @DisplayName("DFS深度遍历-按5日收盘")
    public void buildStrateResultByFiveClose() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1StrategyList.size(); i++) {
            StrategyWin strategyWin = l1StrategyList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            if (stockCodeToDateSetMap == null || stockCodeToDateSetMap.isEmpty()) continue;
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Set<String> strategySet = new HashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevelFiveClose(2, stockCodeToDateSetMap, strategySet, strategyWin, finalI);
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    }

    private void buildByLevelFiveClose(Integer level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                                      Set<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 7) return;
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) continue;
            Map<String, Set<Integer>> curStockToDetailIdSetMap = voConvert.convertToMap(stockToDetailIdSetMap);
            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            if (l1StockToDetailIdMap == null) continue;
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));
            Set<String> curStrategyCodeSet = new HashSet<>(strategySet);
            curStrategyCodeSet.add(strategy.getStrategyCode());
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curStockToDetailIdSetMap);
            if (isNotByFiveClose(win, parentWin, level)) continue;
            strategyWinService.save(win);
            log.info("策略(5日收盘)：{} 5日收盘涨幅:{} 5日最高:{} 开始计算并保存完成",
                    win.getStrategyCode(), win.getFivePercRate(), win.getFiveMaxPercRate());
            buildByLevelFiveClose(level + 1, curStockToDetailIdSetMap, curStrategyCodeSet, win, i);
        }
    }

    /** 按 5日收盘涨幅 剪枝：阈值比 fiveMax 低，更接近实盘可达收益。 */
    private boolean isNotByFiveClose(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 30 || lessThan(win.getFivePercRate(), "0.01")) return true;
        if (parentWin.getFivePercRate() != null && lessAndEqualsThan(win.getFivePercRate(), parentWin.getFivePercRate()))
            return true;
        if (Objects.equals(win.getCnt(), parentWin.getCnt()) || isEquals(win.getWinRate(), BigDecimal.ONE)) return true;
        if (level == 2 && lessThan(win.getFivePercRate(), "0.02")) return true;
        if (level == 3 && lessThan(win.getFivePercRate(), "0.025")) return true;
        if (level == 4 && lessThan(win.getFivePercRate(), "0.03")) return true;
        return false;
    }

    private void buildByLevel(Integer level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                              Set<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 7) {
            return;
        }
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }

            Map<String, Set<Integer>> curStockToDetailIdSetMap = voConvert.convertToMap(stockToDetailIdSetMap);

            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));
            Set<String> curStrategyCodeSet = new HashSet<>();
            curStrategyCodeSet.add(strategy.getStrategyCode());
            curStrategyCodeSet.addAll(strategySet);
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curStockToDetailIdSetMap);
            if (isNotByFiveMax(win, parentWin, level)) {
                continue;
            }
            strategyWinService.save(win);
            log.info("策略：{} 开始计算并保存完成", win.getStrategyCode());
            buildByLevel(level + 1, curStockToDetailIdSetMap, curStrategyCodeSet, win, i);
        }
    }

    /**
     * 我的看法（简要）：
     * - 深度优先 + 按 fiveMax 剪枝很合理，能穷举高潜力组合，但分支多时耗时长、且等权扩展。
     * - 改进方向：每层只保留“最优的若干条分支”再往下扩，即 beam search，用 fiveMax 排序截断，控制宽度。
     *
     * 策略：Beam Search。每层扩展时先收集所有通过 isNotByFiveMax 的子节点，按 fiveMaxPercRate 降序（再按 cnt 降序）
     * 只保留前 beamSize 个再递归，其余剪掉。这样在同样剪枝规则下，优先扩展历史收益更好的组合，减少无效分支。
     */
    @Test
    @DisplayName("Beam Search 策略组合")
    public void buildStrateResultByBeam() throws ExecutionException, InterruptedException {
        final int beamSize = 5;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1StrategyList.size(); i++) {
            StrategyWin strategyWin = l1StrategyList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            if (stockCodeToDateSetMap == null || stockCodeToDateSetMap.isEmpty()) continue;
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Set<String> strategySet = new HashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevelBeam(2, stockCodeToDateSetMap, strategySet, strategyWin, finalI, beamSize);
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    }

    private void buildByLevelBeam(int level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                                  Set<String> strategySet, StrategyWin parentWin, int curIdx, int beamSize) {
        if (level > 7) return;
        List<BeamCandidate> candidates = new ArrayList<>();
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) continue;
            Map<String, Set<Integer>> curStockToDetailIdSetMap = voConvert.convertToMap(stockToDetailIdSetMap);
            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            if (l1StockToDetailIdMap == null) continue;
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));
            Set<String> curStrategyCodeSet = new HashSet<>(strategySet);
            curStrategyCodeSet.add(strategy.getStrategyCode());
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curStockToDetailIdSetMap);
            if (isNotByFiveMax(win, parentWin, level)) continue;
            candidates.add(new BeamCandidate(win, curStockToDetailIdSetMap, curStrategyCodeSet, i));
        }
        Comparator<BeamCandidate> cmp = Comparator
                .comparing(BeamCandidate::getWin, Comparator.comparing(StrategyWin::getFiveMaxPercRate, Comparator.nullsLast(Comparator.naturalOrder())))
                .reversed()
                .thenComparing(c -> c.win.getCnt(), Comparator.nullsLast(Comparator.naturalOrder()));
        List<BeamCandidate> top = candidates.stream().sorted(cmp).limit(beamSize).toList();
        for (BeamCandidate c : top) {
            strategyWinService.save(c.win);
            log.info("策略(beam)：{} 开始计算并保存完成", c.win.getStrategyCode());
            buildByLevelBeam(level + 1, c.stockToDetailIdSetMap, c.strategyCodeSet, c.win, c.nextIdx, beamSize);
        }
    }

    private static class BeamCandidate {
        final StrategyWin win;
        final Map<String, Set<Integer>> stockToDetailIdSetMap;
        final Set<String> strategyCodeSet;
        final int nextIdx;

        BeamCandidate(StrategyWin win, Map<String, Set<Integer>> stockToDetailIdSetMap, Set<String> strategyCodeSet, int nextIdx) {
            this.win = win;
            this.stockToDetailIdSetMap = stockToDetailIdSetMap;
            this.strategyCodeSet = strategyCodeSet;
            this.nextIdx = nextIdx;
        }

        StrategyWin getWin() { return win; }
    }

    private StrategyWin saveStrategyWin(Set<String> strategyCodeSet, Map<String, Set<Integer>> stockToDetailIdSetMap) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        stockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                detailIdSet.forEach(detailId ->
                        win.addToResult(idToDetailMap.get(detailId))));
        win.fillData();
        return win;
    }

    private boolean isNotByFiveMax(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 30 || lessThan(win.getFiveMaxPercRate(), "0.05")) {
            return true;
        }
        if (lessAndEqualsThan(win.getFiveMaxPercRate(), parentWin.getFiveMaxPercRate())
                || Objects.equals(win.getCnt(), parentWin.getCnt())
                || isEquals(win.getWinRate(), BigDecimal.ONE)

                || (level == 2 && lessThan(win.getFiveMaxPercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxPercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"))
//                || (level == 5 && lessThan(win.getWinRate(), "0.84"))
//                || (level == 6 && lessThan(win.getWinRate(), "0.88"))
//                || (level == 7 && lessThan(win.getWinRate(), "0.89"))
//                || (level == 8 && lessThan(win.getWinRate(), "0.91"))
//
//                || (win.getCnt() < 50 && lessThan(win.getWinRate(), "0.7")
//                || (win.getCnt() < 100 && lessThan(win.getWinRate(), "0.6"))
        ) {
            return true;
        }
        return false;
    }

    private boolean isNot(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 20 || lessThan(win.getWinRate(), "0.40")) {
            return true;
        }
        if (moreThan(win.getFiveMaxPercRate(), "0.12") || moreThan(win.getWinRate(), "0.94")) {
            return false;
        }
        if (lessAndEqualsThan(win.getWinRate(), parentWin.getWinRate())
                || Objects.equals(win.getCnt(), parentWin.getCnt())
                || isEquals(win.getWinRate(), BigDecimal.ONE)

                || (level == 2 && lessThan(win.getWinRate(), "0.55"))
                || (level == 3 && lessThan(win.getWinRate(), "0.66"))
                || (level == 4 && lessThan(win.getWinRate(), "0.77"))
                || (level == 5 && lessThan(win.getWinRate(), "0.84"))
                || (level == 6 && lessThan(win.getWinRate(), "0.88"))
                || (level == 7 && lessThan(win.getWinRate(), "0.89"))
                || (level == 8 && lessThan(win.getWinRate(), "0.91"))

                || (win.getCnt() < 50 && lessThan(win.getWinRate(), "0.7")
                || (win.getCnt() < 100 && lessThan(win.getWinRate(), "0.6")))) {
            return true;
        }
        return false;
    }

}

