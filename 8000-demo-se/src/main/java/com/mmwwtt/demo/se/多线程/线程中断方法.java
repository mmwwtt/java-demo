package com.mmwwtt.demo.se.多线程;

import com.mmwwtt.demo.se.多线程.线程池.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 不推荐使用stop中断(已过时)
 */
@Slf4j
public class 线程中断方法 {

    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();


    @Test
    public void interrupt中断线程() throws InterruptedException {
        Thread thread = new Thread(() -> {

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    log.info("任务正在执行，线程: " + Thread.currentThread().getName());
                    Thread.sleep(1000); // 模拟任务执行
                }
            } catch (InterruptedException e) {
                log.info("任务被中断，线程: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt(); // 捕获InterruptedException，中断状态会清除,需要重新设置中断状态
            }
        });

        thread.start();
        thread.interrupt();
        Thread.sleep(1000);
        log.info("结束");
    }

    /**
     * 线程池提供shutdown中断线程，调用每个线程的 interrupt()
     * shutdown()：尝试平滑关闭线程池，不再接受新任务，会等待当前任务完成。
     * shutdownNow()：尝试立即关闭线程池，中断所有正在执行的任务，并返回尚未执行的任务列表。
     *
     * @throws InterruptedException
     */
    @Test
    public void tt() throws InterruptedException {
        pool.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("任务正在执行，线程: " + Thread.currentThread().getName());
                    Thread.sleep(1000); // 模拟任务执行
                }
            } catch (InterruptedException e) {
                System.out.println("任务被中断，线程: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt(); // 重新设置中断状态
            }
        });
        //中断所有线程
        pool.shutdownNow();
        Thread.sleep(30000);
    }
}
