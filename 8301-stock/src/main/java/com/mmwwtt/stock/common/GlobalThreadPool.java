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
    private static final long KEEP_ALIVE_TIME = 5;
    // 时间单位（秒）
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    // 用于存放任务的阻塞队列
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    // 线程池实例，使用volatile保证可见性以及禁止指令重排序
    private static volatile ThreadPoolExecutor ioThreadPool;

    // 线程池实例，使用volatile保证可见性以及禁止指令重排序
    private static volatile ThreadPoolExecutor cpuThreadPool;

    private static volatile ThreadPoolExecutor middleThreadPool2;

    private static volatile ThreadPoolExecutor middleThreadPool3;

    private static volatile ThreadPoolExecutor middleThreadPool4;

    private static volatile ThreadPoolExecutor priorityThreadPool;

    private static volatile PreemptivePriorityExecutor preemptivePriorityExecutor;

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
                    cpuThreadPool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return cpuThreadPool;
    }

    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getMiddleThreadPool2() {
        if (middleThreadPool2 == null) {
            synchronized (GlobalThreadPool.class) {
                if (middleThreadPool2 == null) {
                    middleThreadPool2 = new ThreadPoolExecutor(
                            CPU_CORE_SIZE * 2,
                            CPU_CORE_SIZE * 3,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                    middleThreadPool2.allowCoreThreadTimeOut(true);
                }
            }
        }
        return middleThreadPool2;
    }

    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getMiddleThreadPool3() {
        if (middleThreadPool3 == null) {
            synchronized (GlobalThreadPool.class) {
                if (middleThreadPool3 == null) {
                    middleThreadPool3 = new ThreadPoolExecutor(
                            CPU_CORE_SIZE * 3,
                            CPU_CORE_SIZE * 4,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                    middleThreadPool3.allowCoreThreadTimeOut(true);
                }
            }
        }
        return middleThreadPool3;
    }

    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getMiddleThreadPool4() {
        if (middleThreadPool4 == null) {
            synchronized (GlobalThreadPool.class) {
                if (middleThreadPool4 == null) {
                    middleThreadPool4 = new ThreadPoolExecutor(
                            CPU_CORE_SIZE * 4,
                            CPU_CORE_SIZE * 5,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                    middleThreadPool4.allowCoreThreadTimeOut(true);
                }
            }
        }
        return middleThreadPool4;
    }

    // 获取线程池实例的静态方法，用双重校验的单例模式
    public static ThreadPoolExecutor getIoThreadPool() {
        if (ioThreadPool == null) {
            synchronized (GlobalThreadPool.class) {
                if (ioThreadPool == null) {
                    ioThreadPool = new ThreadPoolExecutor(
                            CPU_CORE_SIZE * 6,
                            CPU_CORE_SIZE * 7,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE,
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                    ioThreadPool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return ioThreadPool;
    }

    public static ThreadPoolExecutor getPriorityThreadPool() {
        if (priorityThreadPool == null) {
            synchronized (GlobalThreadPool.class) {
                if (priorityThreadPool == null) {
                    priorityThreadPool = new ThreadPoolExecutor(
                            CPU_CORE_SIZE +1,
                            CPU_CORE_SIZE * 7,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            new PriorityBlockingQueue<>(),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                    priorityThreadPool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return priorityThreadPool;
    }

    /**
     * 抢占式优先级执行器：高优先级任务入队时会中断当前低优先级任务，让其重新入队后先跑高优先级。
     * 任务需能响应线程中断才能被抢占。
     */
    public static PreemptivePriorityExecutor getPreemptivePriorityExecutor() {
        if (preemptivePriorityExecutor == null) {
            synchronized (GlobalThreadPool.class) {
                if (preemptivePriorityExecutor == null) {
                    preemptivePriorityExecutor = new PreemptivePriorityExecutor(MAXIMUM_POOL_SIZE);
                }
            }
        }
        return preemptivePriorityExecutor;
    }
}
