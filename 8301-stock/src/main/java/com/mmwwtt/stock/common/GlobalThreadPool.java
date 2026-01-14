package com.mmwwtt.stock.common;

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

    //服务器的CPU核数
    private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();


    // 定义线程池的核心线程数
    private static final int CORE_POOL_SIZE = CPU_CORE_SIZE + 1;
    // 定义线程池的最大线程数
    private static final int MAXIMUM_POOL_SIZE = 100;
    // 线程空闲存活时间
    private static final long KEEP_ALIVE_TIME = 20;
    // 时间单位（秒）
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    // 用于存放任务的阻塞队列
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    // 线程池实例，使用volatile保证可见性以及禁止指令重排序
    private static volatile ThreadPoolExecutor ioThreadPool;

    // 线程池实例，使用volatile保证可见性以及禁止指令重排序
    private static volatile ThreadPoolExecutor cpuThreadPool;


    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getCpuThreadPool() {
        if (cpuThreadPool == null) {
            synchronized (GlobalThreadPool.class) {
                if (cpuThreadPool == null) {
                    cpuThreadPool = new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return cpuThreadPool;
    }

    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getIoThreadPool() {
        if (ioThreadPool == null) {
            synchronized (GlobalThreadPool.class) {
                if (ioThreadPool == null) {
                    ioThreadPool = new ThreadPoolExecutor(
                            CPU_CORE_SIZE * 10,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return ioThreadPool;
    }
}
