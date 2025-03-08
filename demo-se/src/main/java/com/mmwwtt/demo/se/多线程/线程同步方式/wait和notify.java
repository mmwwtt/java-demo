package com.mmwwtt.demo.se.多线程.线程同步方式;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class wait和notify {
    private final Object monitor = new Object();

    private volatile boolean flag = true;

    @Test
    public void testSys() {

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (monitor) {
                    while (!flag) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info(Thread.currentThread().getName() + ": foo");
                    flag = false;
                    monitor.notifyAll();
                }
            }
        }, "1");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (monitor) {
                    while (flag) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info(Thread.currentThread().getName() + ": bar");
                    flag = true;
                    monitor.notifyAll();
                }
            }
        }, "2");
        thread1.start();
        thread2.start();
    }

}
