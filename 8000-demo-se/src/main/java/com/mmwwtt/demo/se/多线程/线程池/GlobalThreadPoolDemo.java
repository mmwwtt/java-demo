package com.mmwwtt.demo.se.多线程.线程池;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class GlobalThreadPoolDemo {
    private ThreadLocal<String> str = new ThreadLocal<>();
    //提供默认值
    private ThreadLocal<Integer> number = ThreadLocal.withInitial(() -> 0);

    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();
    /**
     * 工具类创建线程池
     */

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    @Test
    public void demo() {
        //全局线程池使用
        pool.execute(() -> {
            try {
                str.set("hello");
                log.info("Task executed by global thread pool");
            } finally {
                str.remove();
            }
        });
        str.remove();
    }
}
