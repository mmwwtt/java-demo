package com.mmwwtt.demo.se.多线程.线程同步方式;

import com.mmwwtt.demo.se.多线程.线程池.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class wait和notify {
    private static final Object lock = new Object();

    private volatile boolean isTrue = false;
    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();
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
                    log.info(Thread.currentThread().getName() + ": stop");
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
                            e.printStackTrace();
                        }
                    }
                    log.info(Thread.currentThread().getName() + ": stop");
                    isTrue = true;
                    lock.notify();
                }
            }
        });
    }

}
