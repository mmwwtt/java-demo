package com.mmwwtt.demo.se.多线程.线程池;

import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService的线程池
 */
@Data
public class GlobalThreadExecutorPool {
    private static final int POOL_SIZE = 5;
    private static volatile ExecutorService executorService;

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

