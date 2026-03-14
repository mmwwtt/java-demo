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


@Slf4j
@SpringBootTest
public class DFSTest {

    private static final int CNT_THRESHOLD = 80;
    private static final int BATCH_SAVE_SIZE = 50;

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(32);

    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static  List<StrategyWin> l1WinList;
    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        winBatch.clear();
        l1WinList = l1StrategyList.stream()
                .filter(item -> moreThan(item.getFiveMaxPercRate(), "0.04"))
                .filter(item -> item.getStrategyName().startsWith("T0")
                        || item.getStrategyName().startsWith("T1")
                        //|| item.getStrategyName().startsWith("T2")
                        //|| item.getStrategyName().startsWith("T3")
                )
                .sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                LinkedHashSet<String> strategySet = new LinkedHashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevel(2, strategyToDetailsMap.get(strategyWin.getStrategyCode()), strategySet, strategyWin, finalI);
            }, fixedThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        flushWinBatch();
    }


    private void buildByLevel(Integer level, List<Integer> parentDetailIds,
                              LinkedHashSet<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 7) {
            return;
        }
        for (int i = curIdx + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }
            List<Integer> curRetainAllDetailIds = retainAll(parentDetailIds, strategyToDetailsMap.get(strategy.getStrategyCode()));
            LinkedHashSet<String> curStrategyCodeSet = new LinkedHashSet<>();
            curStrategyCodeSet.add(strategy.getStrategyCode());
            curStrategyCodeSet.addAll(strategySet);
            StrategyWin win = saveStrategyWin(curStrategyCodeSet, curRetainAllDetailIds);
            if (isNotByFiveMax(win, parentWin, level)) {
                continue;
            }
            addToWinBatch(win);
            buildByLevel(level + 1, curRetainAllDetailIds, curStrategyCodeSet, win, i);
        }
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

    private StrategyWin saveStrategyWin(LinkedHashSet<String> strategyCodeSet, List<Integer> details) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        details.forEach(detailId -> win.addToResult(idToDetailMap.get(detailId)));
        win.fillData();
        return win;
    }

    private boolean isNotByFiveMax(StrategyWin win, StrategyWin parentWin, Integer level) {
        if (win.getCnt() < CNT_THRESHOLD || lessThan(win.getFiveMaxPercRate(), "0.05")) {
            return true;
        }
        return lessAndEqualsThan(win.getFiveMaxPercRate(), parentWin.getFiveMaxPercRate())
                || lessThan(win.getFiveMaxPercRate(), multiply(parentWin.getFiveMaxPercRate(), 1.01))
                || (level == 2 && lessThan(win.getFiveMaxPercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxPercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"));
    }

    /**
     * 取两个 list的交集
     * 前提：两个list 都要升序
     * @param list1
     * @param list2
     * @return
     */
    private List<Integer> retainAll(List<Integer> list1, List<Integer> list2) {
        List<Integer> list = new ArrayList<>();

        int j =0;
        for (Integer num1 : list1) {
            while (num1 > list2.get(j)) {
                j++;
                if (j >= list2.size()) {
                    return list;
                }
            }
            if (Objects.equals(num1, list2.get(j))) {
                list.add(num1);
            }
        }
        return list;
    }

}