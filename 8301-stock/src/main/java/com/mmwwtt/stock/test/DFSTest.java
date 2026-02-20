package com.mmwwtt.stock.test;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.*;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
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
import static com.mmwwtt.stock.service.impl.CommonService.*;

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



    private StrategyWin saveStrategyWin(Set<String> strategyCodeSet, Map<String, Set<Integer>> stockToDetailIdSetMap) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        stockToDetailIdSetMap.forEach((stock, detailIdSet) ->
                detailIdSet.forEach(detailId ->
                        win.addToResult(idToDetailMap.get(detailId))));
        win.fillData();
        return win;
    }

    private boolean isNotByFiveMax(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < 40 || lessThan(win.getFiveMaxPercRate(), "0.05")) {
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

