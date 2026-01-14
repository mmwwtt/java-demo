package com.mmwwtt.demo.se.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * wait:释放当前对象的锁
 * notify：唤醒其他线程
 */
@Slf4j
public class WaitNotifyTest {
    private static final Object lock = new Object();
    private volatile boolean isTrue = false;
    private final ThreadPoolExecutor pool = GlobalThreadPool.getCpuThreadPool();

    @Test
    public void demo() {
        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {
                    while (!isTrue) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    log.info("{}: stop", Thread.currentThread().getName());
                    isTrue = false;
                    lock.notify();
                }
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {
                    while (isTrue) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            log.info("error");
                        }
                    }
                    log.info("{}: stop", Thread.currentThread().getName());
                    isTrue = true;
                    lock.notify();
                }
            }
        });
        pool.shutdown();
    }
}
