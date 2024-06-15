package com.mmwwtt.java.demo.se.多线程;

import org.junit.jupiter.api.Test;

public class 线程中断方法 {


    @Test
    public void stop中断线程() throws InterruptedException{
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    System.out.println("线程关闭");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        thread.start();
        Thread.sleep(500);
        thread.stop();
    }


    @Test
    public void interrupt中断线程() throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    System.out.println("线程运行中");
                }
                System.out.println(Thread.currentThread().isInterrupted());
                System.out.println("线程关闭");
            }
        };

        t.start();
        System.out.println(t.interrupted());
        Thread.sleep(1000);
        t.interrupt();
        Thread.sleep(1000);
        System.out.println("结束");
    }
}
