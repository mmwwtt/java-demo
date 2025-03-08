package com.mmwwtt.demo.se.多线程;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 线程中断方法 {


    @Test
    public void stop中断线程() throws InterruptedException{
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    log.info("线程关闭");
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
                    log.info("线程运行中");
                }
                log.info("{}",Thread.currentThread().isInterrupted());
                log.info("线程关闭");
            }
        };

        t.start();
        log.info("{}",t.interrupted());
        Thread.sleep(1000);
        t.interrupt();
        Thread.sleep(1000);
        log.info("结束");
    }
}
