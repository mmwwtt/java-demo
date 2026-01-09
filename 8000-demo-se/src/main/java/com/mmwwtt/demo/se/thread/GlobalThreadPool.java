package com.mmwwtt.demo.se.thread;

import lombok.Data;

import java.util.concurrent.*;

/**
 * 自定义线程池
 */
@Data
public class GlobalThreadPool {
    //定义指定的线程池
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);
    private final ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(100);


    // 定义线程池的核心线程数
    private static final int CORE_POOL_SIZE = 5;
    // 定义线程池的最大线程数
    private static final int MAXIMUM_POOL_SIZE = 10;
    // 线程空闲存活时间
    private static final long KEEP_ALIVE_TIME = 60;
    // 时间单位（秒）
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    // 用于存放任务的阻塞队列
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    // 线程池实例，使用volatile保证可见性以及禁止指令重排序
    private static volatile ThreadPoolExecutor threadPoolExecutor;


    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getInstance() {
        if (threadPoolExecutor == null) {
            synchronized (GlobalThreadPool.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    ) {{
                        // 设置允许核心线程超时空闲 后也被销毁
                        allowCoreThreadTimeOut(true);
                    }};
                }
            }
        }
        return threadPoolExecutor;
    }
}
