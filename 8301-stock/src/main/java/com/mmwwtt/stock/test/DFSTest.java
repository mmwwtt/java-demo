package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import com.mmwwtt.stock.vo.DfsTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;


@Slf4j
@SpringBootTest
public class DFSTest {

    private static final int CNT_THRESHOLD = 80;
    private static final int BATCH_SAVE_SIZE = 1000;

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(1048576);
    private static final BlockingQueue<DfsTask> task1Queue = new LinkedBlockingQueue<>(5000);
    private static final BlockingQueue<DfsTask> task2Queue = new LinkedBlockingQueue<>(50000);
    private static final BlockingQueue<DfsTask> task3Queue = new LinkedBlockingQueue<>(500000);
    private static final BlockingQueue<DfsTask> task4Queue = new LinkedBlockingQueue<>(500000);

    @Test
    @DisplayName("DFS深度遍历 - 五日最大涨幅的平均值")
    public void DFS1() throws ExecutionException, InterruptedException {
        DfsMain(this::isNotByFiveMaxAvg);
    }

    @Test
    @DisplayName("DFS深度遍历 - 五日最大涨幅的中位数")
    public void DFS2() throws ExecutionException, InterruptedException {
        DfsMain(this::isNotByFiveMaxMiddle);
    }


    public void DfsMain(Function<StrategyWin, Boolean> isNotFunc) throws ExecutionException, InterruptedException {
        dfsInit(isNotFunc);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        boolean haveTaskRun = false;
        while (!task1Queue.isEmpty() || haveTaskRun) {
            if (task1Queue.isEmpty()) {
                Thread.sleep(10000);
            } else {
                futures.add(CompletableFuture.runAsync(() -> {
                    DfsTask peek = task1Queue.poll();
                    if (peek != null) {
                        buildByLevel(peek);
                    }
                }, cpuThreadPool));
            }
            haveTaskRun = futures.stream().anyMatch(item -> !item.isDone());
        }

        futures = new ArrayList<>();
        while (!task2Queue.isEmpty() || haveTaskRun) {
            if (task2Queue.isEmpty()) {
                Thread.sleep(10000);
            } else {
                futures.add(CompletableFuture.runAsync(() -> {
                    DfsTask peek = task2Queue.poll();
                    if (peek != null) {
                        buildByLevel(peek);
                    }
                }, cpuThreadPool));
            }
            haveTaskRun = futures.stream().anyMatch(item -> !item.isDone());
        }

        futures = new ArrayList<>();
        while (!task3Queue.isEmpty() || haveTaskRun) {
            if (task3Queue.isEmpty()) {
                Thread.sleep(10000);
            } else {
                futures.add(CompletableFuture.runAsync(() -> {
                    DfsTask peek = task3Queue.poll();
                    if (peek != null) {
                        buildByLevel(peek);
                    }
                }, cpuThreadPool));
            }
            haveTaskRun = futures.stream().anyMatch(item -> !item.isDone());
        }

        futures = new ArrayList<>();
        while (!task4Queue.isEmpty() || haveTaskRun) {
            if (task4Queue.isEmpty()) {
                Thread.sleep(10000);
            } else {
                futures.add(CompletableFuture.runAsync(() -> {
                    DfsTask peek = task4Queue.poll();
                    if (peek != null) {
                        buildByLevel(peek);
                    }
                }, cpuThreadPool));
            }
            haveTaskRun = futures.stream().anyMatch(item -> !item.isDone());
        }
        flushWinBatch();
    }


    private void buildByLevel(DfsTask dfsTask) {
        int level = dfsTask.getParentStrategyCodeSet().size() + 1;
        if (level > 7) {
            return;
        }


        for (int i = dfsTask.getCurIdx() + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (dfsTask.getParentStrategyCodeSet().contains(strategy.getStrategyCode())) {
                continue;
            }
            int[] curDetailIds = strategyToDetailsMap.get(strategy.getStrategyCode());
            if (curDetailIds == null) {
                continue;
            }
            int[] curRetainAllDetailIds = retainAll(dfsTask.getParentDetails(), curDetailIds);
            String md5 = getMd5(curRetainAllDetailIds);
            // 单次 compute 替代 get+put，减少争用与重复查找
            Integer storedLevel = md5ToLevelMap.compute(md5, (k, v) -> (v != null && v > level) ? v : level);
            if (storedLevel > level) {
                continue;
            }

            StrategyWin win = calcStrategyWin(dfsTask.getParentStrategyCodeSet(), dfsTask.getParentFiveMaxPercRate(),
                    dfsTask.getParentDetails(), strategy.getStrategyCode(), curRetainAllDetailIds);

            if (dfsTask.getIsNotFunc().apply(win)) {
                continue;
            }
            win.fillData2();
            addToWinBatch(win);
            if (curDetailIds.length > 300000) {
                task1Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else if (curDetailIds.length > 30000) {

                task2Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else if (curDetailIds.length > 3000) {

                task3Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else {
                task4Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            }

            while (!task4Queue.isEmpty() && dfsTask.getParentDetails().length > 3000) {
                DfsTask task = task4Queue.poll();
                buildByLevel(task);
            }
            while (!task3Queue.isEmpty() && dfsTask.getParentDetails().length > 30000) {
                DfsTask task = task3Queue.poll();
                buildByLevel(task);
            }
            while (!task2Queue.isEmpty() && dfsTask.getParentDetails().length > 300000) {
                DfsTask task = task2Queue.poll();
                buildByLevel(task);
            }
            while (!task1Queue.isEmpty()) {
                DfsTask task = task1Queue.poll();
                buildByLevel(task);
            }
        }
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
            if (winBatch.isEmpty()) return;
            toSave = new ArrayList<>(winBatch);
            winBatch.clear();
        }
        strategyWinService.saveBatch(toSave);
    }

    private StrategyWin calcStrategyWin(Set<String> parentWinStrategyCodeSet,
                                        BigDecimal parentFiveMaxPercRate,
                                        int[] parentDetails, String curStrategyCode, int[] details) {
        StrategyWin win = new StrategyWin(curStrategyCode, parentWinStrategyCodeSet,
                parentFiveMaxPercRate, parentDetails, details);
        for (int detail : details) {
            win.addToResult(idToDetailMap.get(detail));
        }
        win.fillData1();
        return win;
    }

    private boolean isNotByFiveMaxAvg(StrategyWin win) {
        if (win.getCnt() < CNT_THRESHOLD || lessThan(win.getFiveMaxPercRate(), "0.05")) {
            return true;
        }
        int level = win.getStrategyCodeSet().size();
        return lessThan(win.getFiveMaxPercRate(), multiply(win.getParentFiveMaxPercRate(), 1.01))
                || (level == 2 && lessThan(win.getFiveMaxPercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxPercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxPercRate(), "0.10"))
                || (level == 5 && lessThan(win.getFiveMaxPercRate(), "0.11"))
                || (level == 6 && lessThan(win.getFiveMaxPercRate(), "0.115"))
                || (level == 7 && lessThan(win.getFiveMaxPercRate(), "0.12"));
    }

    private boolean isNotByFiveMaxMiddle(StrategyWin win) {
        if (win.getCnt() < CNT_THRESHOLD || lessThan(win.getFiveMaxMiddlePercRate(), "0.05")) {
            return true;
        }
        int level = win.getStrategyCodeSet().size();
        return lessThan(win.getFiveMaxMiddlePercRate(), multiply(win.getParentFiveMaxPercRate(), 1.02))
                || (level == 2 && lessThan(win.getFiveMaxMiddlePercRate(), "0.08"))
                || (level == 3 && lessThan(win.getFiveMaxMiddlePercRate(), "0.09"))
                || (level == 4 && lessThan(win.getFiveMaxMiddlePercRate(), "0.10"))
                || (level == 5 && lessThan(win.getFiveMaxMiddlePercRate(), "0.11"))
                || (level == 6 && lessThan(win.getFiveMaxMiddlePercRate(), "0.115"))
                || (level == 7 && lessThan(win.getFiveMaxMiddlePercRate(), "0.12"));
    }

    private void dfsInit(Function<StrategyWin, Boolean> isNotFunc) {
        QueryWrapper<StrategyWin> wrapper = new QueryWrapper<>();
        wrapper.apply("level!=1");
        strategyWinService.remove(wrapper);
        winBatch.clear();
        md5ToLevelMap.clear();

        l1WinList = l1StrategyList.stream()
                .filter(item -> moreThan(item.getFiveMaxPercRate(), "0.04"))
                .filter(item -> item.getStrategyName().startsWith("T0")
                        || item.getStrategyName().startsWith("T1")
                        || item.getStrategyName().startsWith("T2")
                        || item.getStrategyName().startsWith("T3")
                )
                .peek(item -> item.getStrategyCodeSet().add(item.getStrategyCode()))
                .sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();

        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            int[] detailIds = strategyToDetailsMap.get(strategyWin.getStrategyCode());
            if (detailIds == null) {
                continue;
            }
            task1Queue.add(new DfsTask(strategyWin.getStrategyCodeSet(),
                    strategyWin.getFiveMaxPercRate(),
                    detailIds, i, isNotFunc));
        }
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

    private static String getMd5(int[] intArray) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] digest = md.digest(Arrays.toString(intArray).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}