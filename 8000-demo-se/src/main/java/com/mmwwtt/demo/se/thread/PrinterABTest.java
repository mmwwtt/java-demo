package com.mmwwtt.demo.se.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class PrinterABTest {

    private final ThreadPoolExecutor pool = GlobalThreadPool.getCpuThreadPool();
    //volatile 可见性确保本地更新后立即刷新共享变量
    private volatile Boolean isA = false;


    @Test
    @DisplayName("通过volatile变量控制当前谁打印")
    public void test1() throws InterruptedException {
        pool.submit(() -> {
            int count = 0;
            while (count < 100) {
                if (isA) {
                    log.info("A-{}", count);
                    count++;
                    isA = !isA;
                }
            }
        });
        pool.submit(() -> {
            int count = 0;
            while (count < 100) {
                if (!isA) {
                    log.info("B-{}", count);
                    count++;
                    isA = !isA;
                }
            }
        });
        pool.shutdown();
    }

    @Test
    @DisplayName("通过synchronized对资源上锁来控制当前谁打印")
    public void test2() throws InterruptedException, ExecutionException {
        pool.submit(() -> {
            synchronized (isA) {
                for (int i = 0; i < 100; i++) {
                    log.info("A-{}", i);
                    isA.notifyAll();    //通知其他线程
                    try {
                        isA.wait();     //停止当前线程，释放锁
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        pool.submit(() -> {
            synchronized (isA) {
                for (int i = 0; i < 100; i++) {
                    log.info("B-{}", i);
                    isA.notifyAll();
                    try {
                        isA.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        pool.shutdown();
    }
}
