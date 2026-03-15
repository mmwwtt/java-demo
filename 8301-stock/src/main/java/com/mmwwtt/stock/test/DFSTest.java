package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.PriorityFuture;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import com.mmwwtt.stock.vo.DfsTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(1048576);
    private static final BlockingQueue<DfsTask> taskQueue = new LinkedBlockingQueue<>(1048576);

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
        AtomicInteger task1Num = new AtomicInteger();
        AtomicInteger task2Num = new AtomicInteger();
        AtomicInteger task3Num = new AtomicInteger();
        AtomicInteger task4Num = new AtomicInteger();

        // 用原子计数代替 futures 列表，避免主循环对百万级列表做 stream，越跑越慢
        while (true) {
            List<DfsTask> taskList = new ArrayList<>();
            taskQueue.drainTo(taskList, taskQueue.size());
            if (!taskList.isEmpty()) {
                List<DfsTask> size1List = new ArrayList<>();
                List<DfsTask> size2List = new ArrayList<>();
                List<DfsTask> size3List = new ArrayList<>();
                List<DfsTask> size4List = new ArrayList<>();
                taskList.forEach(task -> {
                    if (task.getParentDetails().length > 300000) {
                        size1List.add(task);
                    } else if (task.getParentDetails().length > 30000) {
                        size2List.add(task);
                    } else if (task.getParentDetails().length > 3000) {
                        size3List.add(task);
                    } else {
                        size4List.add(task);
                    }
                });
                int n1 = size1List.size();
                int n2 = (size2List.size() + 9) / 10;
                int n3 = (size3List.size() + 99) / 100;
                int n4 = (size4List.size() + 999) / 1000;
                task1Num.addAndGet(n1);
                task2Num.addAndGet(n2);
                task3Num.addAndGet(n3);
                task4Num.addAndGet(n4);

                size1List.forEach(task ->
                        PriorityFuture.runAsync(4, () -> {
                            try {
                                buildByLevel(task);
                            } finally {
                                task1Num.decrementAndGet();
                            }
                        }));
                ListUtils.partition(size2List, 10).forEach(partTaskList ->
                        PriorityFuture.runAsync(3, () -> {
                            try {
                                partTaskList.forEach(this::buildByLevel);
                            } finally {
                                task2Num.decrementAndGet();
                            }
                        }));
                ListUtils.partition(size3List, 100).forEach(partTaskList ->
                        PriorityFuture.runAsync(2, () -> {
                            try {
                                partTaskList.forEach(this::buildByLevel);
                            } finally {
                                task3Num.decrementAndGet();
                            }
                        }));
                ListUtils.partition(size4List, 1000).forEach(partTaskList ->
                        PriorityFuture.runAsync(1, () -> {
                            try {
                                partTaskList.forEach(this::buildByLevel);
                            } finally {
                                task4Num.decrementAndGet();
                            }
                        }));
            }

            int pendingCount = task1Num.get() + task2Num.get() + task3Num.get() + task4Num.get();
            log.info("队列任务数{}， 剩余任务数{} 类型1数量{}  类型2数量{} 类型3数量{} 类型4数量{}",
                    taskQueue.size(), pendingCount,
                    task1Num.get(), task2Num.get(), task3Num.get(), task4Num.get());

            if (taskQueue.size() < 2000) {
                if (taskQueue.isEmpty() && pendingCount == 0) {
                    break;
                }
                Thread.sleep(20000);
            }
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
            taskQueue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                    win.getDetails(), i, dfsTask.getIsNotFunc()));
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
        taskQueue.clear();

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
            taskQueue.add(new DfsTask(strategyWin.getStrategyCodeSet(),
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