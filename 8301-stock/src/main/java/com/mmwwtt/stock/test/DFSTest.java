package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

/**
 * 深度优先遍历各种策略
 * 区间60向上 区间40_20_30 下影线长度_08_09   是100%胜率
 * l1 cnt限值不能大于50
 */
@Slf4j
@SpringBootTest
public class DFSTest {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockDetailServiceImpl stockDetailService;

    @Autowired
    private StrategyResultServiceImpl strategyResultService;


    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    @Autowired
    private CalcCommonService calcCommonService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor middleThreadPool2 = GlobalThreadPool.getMiddleThreadPool2();
    private final ThreadPoolExecutor middleThreadPool3 = GlobalThreadPool.getMiddleThreadPool3();
    private final ThreadPoolExecutor middleThreadPool4 = GlobalThreadPool.getMiddleThreadPool4();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();


    private final VoConvert voConvert = VoConvert.INSTANCE;

    private Map<String, Map<String, Set<Integer>>> l1StrategyToStockToDetailIdSetMap;
    private Map<Integer, StockDetail> idToDetailMap = new HashMap<>();
    private List<StrategyWin> l1StrategyList;

    public static Set<String> set = Set.of("00014", "01072", "01108");

    @PostConstruct
    public void init() {
        l1StrategyToStockToDetailIdSetMap = strategyResultService.getL1StrategyToStockToDateIdSetMap();
        l1StrategyList = strategyWinService.getL1StrategyWin().stream()
                .filter(item -> moreThan(item.getWinRate(), "0.40"))
                .sorted(Comparator.comparing(StrategyWin::getWinRate).reversed()).toList();
        Map<String, Map<Integer, StockDetail>> map = new HashMap<>();
        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<Integer, StockDetail> idToDetailMap = stockDetailService.getStockDetail(stock.getCode(), null)
                            .stream().collect(Collectors.toMap(StockDetail::getStockDetailId, item -> item));
                    this.idToDetailMap.putAll(idToDetailMap);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allTask.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        calcCommonService.buildStrateResultLevel1();
    }

    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1StrategyList.size(); i++) {
            StrategyWin strategyWin = l1StrategyList.get(i);
            Map<String, Set<Integer>> stockCodeToDateSetMap = l1StrategyToStockToDetailIdSetMap.get(strategyWin.getStrategyCode());
            int finalI = i;
//            if(!set.contains(strategyWin.getStrategyCode())){
//                continue;
//            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Set<String> strategySet = new HashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevel(2, stockCodeToDateSetMap, strategySet, strategyWin, finalI);
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    private void buildByLevel(Integer level, Map<String, Set<Integer>> stockToDetailIdSetMap,
                              Set<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 10) {
            return;
        }
        for (int i = curIdx + 1; i < l1StrategyList.size(); i++) {
            StrategyWin strategy = l1StrategyList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }
//            if (!set.contains(strategy.getStrategyCode())) {
//                continue;
//            }

            Map<String, Set<Integer>> curStockToDetailIdSetMap = voConvert.convertToMap(stockToDetailIdSetMap);

            Map<String, Set<Integer>> l1StockToDetailIdMap = l1StrategyToStockToDetailIdSetMap.get(strategy.getStrategyCode());
            curStockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                    detailIdSet.retainAll(l1StockToDetailIdMap.getOrDefault(stock, Collections.emptySet())));
            Set<String> curStrategyCodeSet = new HashSet<>();
            curStrategyCodeSet.add(strategy.getStrategyCode());
            curStrategyCodeSet.addAll(strategySet);
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curStockToDetailIdSetMap);
            if (isNot1(win, parentWin, level)) {
                continue;
            }
            strategyWinService.save(win);
            log.info("策略：{} 开始计算并保存完成", win.getStrategyCode());
            buildByLevel(level + 1, curStockToDetailIdSetMap, curStrategyCodeSet, win, i);
        }
    }

    private StrategyWin saveStrategyWin(Set<String> strategyCodeSet, Map<String, Set<Integer>> stockToDetailIdSetMap) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        stockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                detailIdSet.forEach(detailId ->
                        win.addToResult(idToDetailMap.get(detailId))));
        win.fillData();
        return win;
    }

    private boolean isNot(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 10) {
            return true;
        }
        if (moreThan(win.getTenMaxPercRate(), "0.09") || moreThan(win.getOnePercRate(), "0.2")) {
            return false;
        }
        if (lessAndEqualsThan(win.getWinRate(), parentWin.getWinRate())
                || win.getCnt() < 20 || lessThan(win.getWinRate(), "0.40")
                || Objects.equals(win.getCnt(), parentWin.getCnt())
                || isEquals(win.getWinRate(), BigDecimal.ONE)
                || (win.getCnt() > 500 && lessThan(win.getWinRate(), multiply(parentWin.getWinRate(), "1.1")))
                || (win.getCnt() < 500 && lessThan(win.getWinRate(), multiply(parentWin.getWinRate(), "1.05")))
                || (win.getCnt() < 250 && lessThan(win.getWinRate(), "0.7") && level > 6)
                || (win.getCnt() < 400 && lessThan(win.getWinRate(), "0.65") && level > 6)
                || (win.getCnt() < 500 && lessThan(win.getWinRate(), "0.60") && level > 6)
                || (win.getCnt() < 3000 && lessThan(win.getWinRate(), "0.50") && level > 6)) {
            return true;
        }
        return false;
    }

    private boolean isNot1(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (lessAndEqualsThan(win.getWinRate(), parentWin.getWinRate())
                || win.getCnt() < 20 || lessThan(win.getWinRate(), "0.40")
                || Objects.equals(win.getCnt(), parentWin.getCnt())
                || isEquals(win.getWinRate(), BigDecimal.ONE)
                || (win.getCnt() < 250 && lessThan(win.getWinRate(), "0.7") && level > 6)
                || (win.getCnt() < 400 && lessThan(win.getWinRate(), "0.65") && level > 6)
                || (win.getCnt() < 500 && lessThan(win.getWinRate(), "0.60") && level > 6)
                || (win.getCnt() < 3000 && lessThan(win.getWinRate(), "0.50") && level > 6)) {
            return true;
        }
        return false;
    }
}

