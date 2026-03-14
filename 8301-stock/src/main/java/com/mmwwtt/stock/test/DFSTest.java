package com.mmwwtt.stock.test;

import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    private static List<StrategyWin> l1WinList;

    @Test
    @DisplayName("DFS深度遍历")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        winBatch.clear();
        l1WinList = l1StrategyList.stream()
                .filter(item -> moreThan(item.getFiveMaxPercRate(), "0.04"))
                .filter(item -> item.getStrategyName().startsWith("T0")
                                || item.getStrategyName().startsWith("T1")
                                || item.getStrategyName().startsWith("T2")
//                      || item.getStrategyName().startsWith("T3")
                )
                .sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Set<String> strategySet = new HashSet<>();
                strategySet.add(strategyWin.getStrategyCode());
                buildByLevel(2, strategyToDetailsMap.get(strategyWin.getStrategyCode()), strategySet, strategyWin, finalI);
            }, fixedThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        flushWinBatch();
    }


    private void buildByLevel(Integer level, int[] parentDetailIds,
                              Set<String> strategySet, StrategyWin parentWin, Integer curIdx) {
        if (level > 7) {
            return;
        }
        for (int i = curIdx + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (strategySet.contains(strategy.getStrategyCode())) {
                continue;
            }
            int[] curRetainAllDetailIds = retainAll(parentDetailIds, strategyToDetailsMap.get(strategy.getStrategyCode()));
            Set<String> curStrategyCodeSet = new HashSet<>();
            curStrategyCodeSet.add(strategy.getStrategyCode());
            curStrategyCodeSet.addAll(strategySet);
            StrategyWin win = calcStrategyWin(curStrategyCodeSet, curRetainAllDetailIds);
            if (isNotByFiveMax(win, parentWin, level)) {
                continue;
            }
            win.fillData2();
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

    private StrategyWin calcStrategyWin(Set<String> strategyCodeSet, int[] details) {
        StrategyWin win = new StrategyWin(strategyCodeSet);
        for (int detail : details) {
            win.addToResult(idToDetailMap.get(detail));
        }
        win.fillData1();
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
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"))
                || (level == 5 && lessThan(win.getFiveMaxPercRate(), "0.11"));
    }

    /**
     * 取两个 list的交集
     * 前提：两个list 都要升序,且无重复
     */
    private int[] retainAll(int[] list1, int[] list2) {
        int[] tmpArr = new int[Math.min(list1.length, list2.length)];
        if (tmpArr.length == 0) {
            return new int[0];
        }
        int j = 0;
        int arrIdx = 0;
        for (int num1 : list1) {
            while (num1 > list2[j]) {
                j++;
                if (j >= list2.length) {
                    return Arrays.copyOfRange(tmpArr, 0, arrIdx);
                }
            }
            if (num1 == list2[j]) {
                tmpArr[arrIdx] = num1;
                j++;
                arrIdx++;
                if (j >= list2.length) {
                    return Arrays.copyOfRange(tmpArr, 0, arrIdx);
                }
            }
        }
        return Arrays.copyOfRange(tmpArr, 0, arrIdx);
    }
}