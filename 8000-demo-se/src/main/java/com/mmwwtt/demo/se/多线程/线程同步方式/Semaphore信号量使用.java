package com.mmwwtt.demo.se.多线程.线程同步方式;

import com.mmwwtt.demo.se.多线程.线程池.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class Semaphore信号量使用 {

    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    /**·
     * acquire() 线程占用一个许可.
     * acquire(int) 线程占用int个许可
     * acquireUninterruptibly() 线程占用一个许可, 调用不可以打断
     * acquireUninterruptibliy(int) 线程占用int个许可,调用并且不可打断
     *
     * release() 释放一个许可
     * release(int) 释放int个许可
     */
    @Test
    public void demo() {
        Semaphore foo = new Semaphore(1);
        Semaphore bar = new Semaphore(0);

        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    foo.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(Thread.currentThread().getName() + ": foo");
                //每个release()方法都会释放持有许可证的线程，并且归还Semaphore一个可用的许可证。
                bar.release();
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    bar.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(Thread.currentThread().getName() + ": bar");
                foo.release();
            }
        });
    }
}
