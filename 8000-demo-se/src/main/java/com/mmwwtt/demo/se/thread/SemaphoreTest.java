package com.mmwwtt.demo.se.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Semaphore:控制同时访问某个线程的进程数
 * acquire() 占用一个许可.
 * acquire(int) 线程占用n个许可
 * acquireUninterruptibly() 占用一个许可, 调用不可以打断
 * acquireUninterruptibliy(int) 占用n个许可,调用不可打断
 * release() 释放一个许可
 * release(int) 释放n个许可
 */
@Slf4j
public class SemaphoreTest {

    private final ThreadPoolExecutor pool = GlobalThreadPool.getCpuThreadPool();

    @Test
    @DisplayName("Semaphore使用")
    public void demo() {
        Semaphore foo = new Semaphore(1);

        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                log.info("{}",i);
                try {
                    foo.acquire();
                } catch (InterruptedException e) {
                    log.info("error");
                } finally {
                    foo.release();
                }
            }
        });
        pool.shutdown();
    }
}
