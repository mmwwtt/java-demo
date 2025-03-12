package com.mmwwtt.demo.se.多线程.线程池;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class GlobalThreadPoolDemo {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {

        //全局线程池使用
        ThreadPoolExecutor globalThreadPool = GlobalThreadPool.getInstance();
        globalThreadPool.execute(() -> {
            try {
                threadLocal.set("hello");
                log.info("Task executed by global thread pool");
            } finally {
                threadLocal.remove();
            }
        });


        ExecutorService globalFixedThreadPool = GlobalThreadExecutorPool.getInstance();
        globalFixedThreadPool.execute(() -> {
            try {
                threadLocal.set("hello");
                log.info("Task running in fixed thread pool");
            } finally {
                threadLocal.remove();
            }
        });

    }
}
