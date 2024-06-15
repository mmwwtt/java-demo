package com.mmwwtt.java.demo.se.多线程.线程同步方式;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrier循环屏障使用 {

    //CyclicBarrier: 可循环（cyclic）使用的屏障。组线程到达一个屏障时被阻塞，直到最后一个线程到达屏障时，所有被屏障拦截的线程才会继续干活
    private volatile boolean fin = true;

    @Test
    public void demo() {
        CyclicBarrier cb = new CyclicBarrier(2);
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                while (!fin) {
                }
                System.out.println(Thread.currentThread().getName() + " " + i);
                try {
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                fin = false;
            }
        }, "thread1");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                System.out.println(Thread.currentThread().getName() + " " + i);
                try {
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                fin = true;
            }
        }, "thread2");
        thread1.start();
        thread2.start();
    }
}
