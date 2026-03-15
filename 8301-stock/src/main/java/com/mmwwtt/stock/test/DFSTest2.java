package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import com.mmwwtt.stock.vo.DfsTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

/**
 * DFS 优先级版：task1 为最上层任务，执行时产生 1、2、3、4 子任务；
 * 子任务按 4→3→2→1 的优先级处理（task4 最先处理，task1 最后），避免小任务堆积。
 */
@Slf4j
@SpringBootTest
public class DFSTest2 {

    private static final int CNT_THRESHOLD = 80;
    private static final int BATCH_SAVE_SIZE = 1000;

    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(1048576);
    private static final BlockingQueue<DfsTask> task1Queue = new LinkedBlockingQueue<>(5000);
    private static final BlockingQueue<DfsTask> task2Queue = new LinkedBlockingQueue<>(50000);
    private static final BlockingQueue<DfsTask> task3Queue = new LinkedBlockingQueue<>(500000);
    private static final BlockingQueue<DfsTask> task4Queue = new LinkedBlockingQueue<>(500000);

    @Test
    @DisplayName("DFS2 - 五日最大涨幅平均值（优先级先执行）")
    public void DFS1() throws Exception {
        DfsMain(this::isNotByFiveMaxAvg);
    }

    @Test
    @DisplayName("DFS2 - 五日最大涨幅中位数（优先级先执行）")
    public void DFS2() throws Exception {
        DfsMain(this::isNotByFiveMaxMiddle);
    }

    public void DfsMain(Function<StrategyWin, Boolean> isNotFunc) throws InterruptedException {
        dfsInit(isNotFunc);
        AtomicInteger pending = new AtomicInteger(0);

        while (true) {
            if (pending.get() > 32) {
                Thread.sleep(10000);
            }
            List<DfsTask> list = new ArrayList<>();
            // 子任务按 4→3→2→1 优先级取（task4 先处理）
            if (!task4Queue.isEmpty()) {
                task4Queue.drainTo(list, 100);
            } else if (!task3Queue.isEmpty()) {
                task3Queue.drainTo(list, 50);
            } else if (!task2Queue.isEmpty()) {
                task2Queue.drainTo(list, 20);
            } else if (!task1Queue.isEmpty()) {
                task1Queue.drainTo(list, 2);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                pending.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        list.forEach(this::buildByLevel);
                    } finally {
                        pending.decrementAndGet();
                    }
                }, cpuThreadPool);
            } else {
                if (pending.get() == 0) {
                    break;
                }
                Thread.sleep(5000);
            }
            log.info("任务堆积数：{}", pending.get());
        }
        flushWinBatch();
    }

    /**
     * 先按 4→3→2→1 执行已有子任务（阻塞当前任务不继续产生），再展开当前节点；
     * 每产生一个子任务入队后再次 drain，避免子任务堆积。
     */
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

            if (win.getDetails().length > 300000) {
                task1Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else if (win.getDetails().length > 30000) {
                task2Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else if (win.getDetails().length > 3000) {
                task3Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            } else {
                task4Queue.add(new DfsTask(win.getStrategyCodeSet(), win.getFiveMaxPercRate(),
                        win.getDetails(), i, dfsTask.getIsNotFunc()));
            }
            drainByPriority();
        }
    }

    private void drainByPriority() {
        DfsTask t;
        while ((t = task4Queue.poll()) != null) buildByLevel(t);
        while ((t = task3Queue.poll()) != null) buildByLevel(t);
        while ((t = task2Queue.poll()) != null) buildByLevel(t);
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
        task1Queue.clear();
        task2Queue.clear();
        task3Queue.clear();
        task4Queue.clear();

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
            BlockingQueue<DfsTask> curQueue = null;
            if (detailIds.length > 300000) {
                curQueue = task1Queue;
            } else if (detailIds.length > 30000) {
                curQueue = task2Queue;
            } else if (detailIds.length > 3000) {
                curQueue = task3Queue;
            } else {
                curQueue = task4Queue;
            }
            curQueue.add(new DfsTask(strategyWin.getStrategyCodeSet(),
                    strategyWin.getFiveMaxPercRate(),
                    detailIds, i, isNotFunc));
        }
    }

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
