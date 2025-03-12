package com.mmwwtt.demo.se.多线程.线程池;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局线程池
 * @author Tarzan写bug
 * @since 2022/09/08
 */
public class GlobalThreadPool {

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

    // 私有构造方法，防止外部实例化
    private GlobalThreadPool() {
    }

    // 获取线程池实例的静态方法，采用双重检查锁定（DCL）的单例模式
    public static ThreadPoolExecutor getInstance() {
        if (threadPoolExecutor == null) {
            synchronized (GlobalThreadPool.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            UNIT,
                            WORK_QUEUE
                    );
                }
            }
        }
        return threadPoolExecutor;
    }
}
