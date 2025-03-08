package com.mmwwtt.demo.se.多线程.线程同步方式;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class await和signal {

    private Lock reentrantLock = new ReentrantLock(true);
    private volatile boolean flag = true;
    private final Condition cond = reentrantLock.newCondition();

    @Test
    public void test() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                reentrantLock.lock();
                try {
                    while (!flag) {
                        try {
                            cond.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info(Thread.currentThread().getName() + ": foo," + i);
                    flag = false;
                    cond.signal();
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "1");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                reentrantLock.lock();
                try {
                    while (flag) {
                        try {
                            cond.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info(Thread.currentThread().getName() + ": bar," + i);
                    flag = true;
                    cond.signal();
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "2");

        thread1.start();
        thread2.start();
    }
}
