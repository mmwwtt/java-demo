package com.mmwwtt.demo.se.多线程.交替打印AB;

import com.mmwwtt.demo.se.多线程.线程池.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class PrinterAB {
    private final Object monitor = new Object();

    private volatile boolean flag = true;

    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();
    //volatile 每个线程都会有共享线程的变量副本，可见性确保本地更新后刷新立即共享变量，共享变量不会bian
    private volatile Boolean isA = false;
    private volatile Boolean lock = false;

    @Test
    public void testPringAB1() throws InterruptedException {
        Runnable logA = () -> {
            Integer count = 0;
            while (count < 100) {
                if (isA) {
                    log.info("A-" + count);
                    count++;
                    isA = !isA;
                }
            }
        };
        Runnable logB = () -> {
            Integer count = 0;
            while (count < 100) {
                if (!isA) {
                    log.info("B-" + count);
                    count++;
                    isA = !isA;
                }
            }
        };
        pool.submit(logA);
        pool.submit(logB);
        Thread.sleep(100000);
    }

    @Test
    public void testSync() throws InterruptedException, ExecutionException {
        Runnable logA = () -> {
            Integer count = 0;
            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    log.info("A-" + count);
                    count++;
                    lock.notifyAll();    //通知其他线程
                    try {
                        lock.wait();     //停止当前线程，释放锁
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        Runnable logB = () -> {
            Integer count = 0;
            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    log.info("B-" + count);
                    count++;
                    lock.notifyAll();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        Future<?> futureA = pool.submit(logA);
        Future<?> futureB = pool.submit(logB);

        //等待两个线程结束
        futureA.get();
        futureB.get();
    }


}
