package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
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

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();


    /**
     * 收集待保存的 StrategyWin，批量写入减少 DB 往返
     */
    private final List<StrategyWin> winBatch = Collections.synchronizedList(new ArrayList<>());

    private static List<StrategyWin> l1WinList;
    private static final Map<String, Integer> md5ToLevelMap = new ConcurrentHashMap<>(1000000);
    private final AtomicInteger taskCnt = new AtomicInteger(0);

    private static final BloomFilter<String> bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charsets.UTF_8),           // 数据类型转换器（Funnel）
            40000000, //期望插入大小
            0.001  //误判概率
    );

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
        dfsInit();
        for (int i = 0; i < l1WinList.size(); i++) {
            StrategyWin strategyWin = l1WinList.get(i);
            int[] detailIds = strategyToDetailsMap.get(strategyWin.getStrategyCode());
            if (detailIds == null) {
                continue;
            }
            int finalI = i;
            taskCnt.incrementAndGet();
            CompletableFuture.runAsync(() -> {
                try {
                    buildByLevel(detailIds, strategyWin, finalI, isNotFunc);
                } finally {
                    taskCnt.decrementAndGet();
                }
            }, cpuThreadPool);
        }
        while (taskCnt.get() != 0) {
            Thread.sleep(10000);
            log.info("任务数 taskCnt:{}", taskCnt.get());
        }
        flushWinBatch();
        log.info("md5Map数量 ： {}", md5ToLevelMap.size());
    }


    private void buildByLevel(int[] parentDetailIds, StrategyWin parentWin, Integer curIdx,
                              Function<StrategyWin, Boolean> isNotFunc) {
        int level = parentWin.getStrategyCodeSet().size() + 1;
        if (level > 7) {
            return;
        }
        for (int i = curIdx + 1; i < l1WinList.size(); i++) {
            StrategyWin strategy = l1WinList.get(i);
            if (parentWin.getStrategyCodeSet().contains(strategy.getStrategyCode())) {
                continue;
            }
            int[] curDetailIds = strategyToDetailsMap.get(strategy.getStrategyCode());
            if (curDetailIds == null) {
                continue;
            }
            int[] curRetainAllDetailIds = retainAll(parentDetailIds, curDetailIds);

            //先布隆初步过滤，不存在的直接计算win (不存在的一定不存在)
            //在布隆中存在则，去md5中找(存在的有概率不存在，接受了(小概率))
            //md5中不存在则表示已经被策略抛弃-continue
            //md5中存在且level比当前小/等于表示之前已经有该情况，且策略更少-contine
            //md5中存在且level比当前大到了该情况，用的策略比之前少-继续执行
            String md5Key = getMd5Key(curRetainAllDetailIds);
            boolean bloomHave = bloomFilter.mightContain(md5Key);
            if (bloomHave) {
                Integer newLevel = md5ToLevelMap.get(md5Key);
                if (newLevel == null || newLevel < level) {
                    continue;
                }
            } else {
                bloomFilter.put(md5Key);
            }

            StrategyWin win = calcStrategyWin(parentWin.getStrategyCodeSet(), parentWin.getFiveMaxPercRate(),
                    strategy.getStrategyCode(), curRetainAllDetailIds);

            if (isNotFunc.apply(win)) {
                continue;
            }
            md5ToLevelMap.put(md5Key, level);
            win.fillData2();
            addToWinBatch(win);
            if (curDetailIds.length > 1000 && taskCnt.get() < cpuThreadPool.getCorePoolSize()) {
                int finalI = i;
                taskCnt.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        buildByLevel(curRetainAllDetailIds, win, finalI, isNotFunc);
                    } finally {
                        taskCnt.decrementAndGet();
                    }
                }, cpuThreadPool);
            } else {
                buildByLevel(curRetainAllDetailIds, win, i, isNotFunc);
            }
        }
    }

    private void dfsInit() {
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

    private StrategyWin calcStrategyWin(Set<String> parentWinStrategyCodeSet, BigDecimal parentFiveMaxPercRate,
                                        String curStrategyCode, int[] details) {
        StrategyWin win = new StrategyWin(curStrategyCode, parentWinStrategyCodeSet,
                parentFiveMaxPercRate, details);
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

    /**
     * 返回 128 位紧凑 key，用于 md5ToLevelMap，不分配 String，适合几十万 key 的大 map
     */
    private String getMd5Key(int[] arr) {
        try {
            // 1. 将 int[] 转换为 byte[] (每个 int 4字节)
            byte[] input = new byte[arr.length * 4];
            for (int i = 0; i < arr.length; i++) {
                input[i * 4] = (byte) (arr[i] >> 24);
                input[i * 4 + 1] = (byte) (arr[i] >> 16);
                input[i * 2 + 2] = (byte) (arr[i] >> 8);
                input[i * 4 + 3] = (byte) (arr[i]);
            }

            // 2. 计算 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input);

            // 3. 转为 32位字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String fullMD5 = sb.toString();

            // 4. 截取前 8 位 (最短建议不要少于 8 位)
            return fullMD5.substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}