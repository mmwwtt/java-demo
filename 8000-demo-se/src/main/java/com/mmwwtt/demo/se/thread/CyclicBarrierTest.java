package com.mmwwtt.demo.se.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * CyclicBarrier: 线程到达屏障后被阻塞，直到所有线程都达到屏障后，所有线程才能继续执行
 * cb.await() 类似计数器减1 表示一个线程到达屏障位置
 * 等多个线程同时执行到某位置后，多个线程再同时执行
 */
@Slf4j
public class CyclicBarrierTest {

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    @Test
    @DisplayName("CyclicBarrier使用")
    public void demo() {
        CyclicBarrier cb = new CyclicBarrier(2);
        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    cb.await();
                    log.info("thread-1--{}", i);
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.info("error");
                }
            }
        });

        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    cb.await();
                    log.info("thread-2--{}", i);
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.info("error");
                }
            }
        });
        pool.shutdown();
    }
}
