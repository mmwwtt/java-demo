package com.mmwwtt.stock.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 是要实现队列数量大于核心线程数2倍时， 就创建非核心线程来处理任务   并且队列满时，再执行抛弃策略
 * 待完善
 */
@Slf4j
public class CustomThreadPool extends ThreadPoolExecutor {

    private final BlockingQueue<Runnable> queue;
    private final int earlyThreshold; //触发条件

    public CustomThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.queue = workQueue;
        this.earlyThreshold = corePoolSize * 2;
    }

    @Override
    public void execute(Runnable command) {
        // 提前判断：队列任务数 >= 核心线程数×2，且还没达到最大线程数
        if (queue.size() >= earlyThreshold && getPoolSize() < getMaximumPoolSize()) {
            log.info("tt");
            // 提交一个空任务，触发线程池扩容（非核心线程）
            super.execute(() -> {});
        }
        super.execute(command);
    }
}
