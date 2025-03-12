package com.mmwwtt.demo.se.多线程.线程池;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalThreadExecutorPool {
    // 定义固定线程数为5的线程池
    private static final int POOL_SIZE = 5;
    private static volatile ExecutorService executorService;

    private GlobalThreadExecutorPool() {
    }

    public static ExecutorService getInstance() {
        if (executorService == null) {
            synchronized (GlobalThreadExecutorPool.class) {
                if (executorService == null) {
                    executorService = Executors.newFixedThreadPool(POOL_SIZE);
                }
            }
        }
        return executorService;
    }
}

