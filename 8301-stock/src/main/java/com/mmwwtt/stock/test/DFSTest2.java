package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;

/**
 * 贪心DFS：每层遍历后只选 fiveMaxPercRate 最高的组合继续向下，其余抛弃
 */
@Slf4j
@SpringBootTest
public class DFSTest2 {

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final VoConvert voConvert = VoConvert.INSTANCE;
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<StrategyWin> l1WinList = l1StrategyList.stream()
                .filter(item -> moreThan(item.getFiveMaxPercRate(), "0.04"))
                .sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                strategyWin.getStrategyCodeSet().add(strategyWin.getStrategyCode());
                buildByLevel(2, stockCodeToDateSetMap, strategyWin, finalI);
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    private void buildByLevel(Integer level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                              StrategyWin parentWin, Integer curIdx) {
        if (level > 5) {
            return;
        }
        Set<String> strategySet = new HashSet<>(parentWin.getStrategyCodeSet());
        // 胜率   (股票code  符合策略的详情id列表) (下标)
       List<Triple<StrategyWin, Map<String, Set<Integer>>, Integer>> triples= new ArrayList<>();
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }

            Map<String, Set<Integer>> curStockToDetailIdSetMap = voConvert.convertToMap(stockToDetailIdSetMap);

            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));
            strategySet.add(strategy.getStrategyCode());
            StrategyWin win = saveStrategyWin(strategySet, curStockToDetailIdSetMap);
            if (isNotByFiveMax(win, parentWin, level)) {
                continue;
            }
            triples.add(Triple.of(win, stockToDetailIdSetMap, i));
            strategyWinService.save(win);
            log.info("策略：{} 开始计算并保存完成", win.getStrategyCode());
        }
        triples.sort(Comparator.comparing(Triple<StrategyWin, Map<String, Set<Integer>>, Integer>::getLeft, Comparator.comparing(StrategyWin::getFiveMaxPercRate)).reversed());
        for (int i = 0; i < 5; i++) {
            if(triples.size() >i ) {
                Triple<StrategyWin, Map<String, Set<Integer>>, Integer> triple = triples.get(i);
                buildByLevel(level + 1, triple.getMiddle(), triple.getLeft(), triple.getRight());
            }
        }
    }


    private StrategyWin saveStrategyWin(Set<String> strategyCodeSet, Map<String, Set<Integer>> stockToDetailIdSetMap) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        win.setStrategyCodeSet(strategyCodeSet);
        stockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                detailIdSet.forEach(detailId -> win.addToResult(idToDetailMap.get(detailId))));
        win.fillData();
        return win;
    }

    private boolean isNotByFiveMax(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 30 || lessThan(win.getFiveMaxPercRate(), "0.05")) {
            return true;
        }
        if (lessAndEqualsThan(win.getFiveMaxPercRate(), parentWin.getFiveMaxPercRate())
                || Objects.equals(win.getCnt(), parentWin.getCnt())
                || (win.getCnt() > 50 && lessThan(win.getFiveMaxPercRate(), multiply(parentWin.getFiveMaxPercRate(), 1.1)))
                || (win.getCnt() < 50 && lessThan(win.getFiveMaxPercRate(), multiply(parentWin.getFiveMaxPercRate(), 1.05)))
                || (level == 2 && lessThan(win.getFiveMaxPercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxPercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"))
        ) {
            return true;
        }
        return false;
    }
}
