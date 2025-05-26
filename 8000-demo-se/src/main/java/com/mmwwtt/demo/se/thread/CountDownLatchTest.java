package com.mmwwtt.demo.se.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 *     new CountDownLatch(int count):计数器初始值(多少个先执行，就设置为几)
 *     countDown():计数器值-1，直到减为0，所有线程执行完毕
 *     getCount():当前计数器值
 *     await():等待计数器变为0，即等待所有的异步线程执行完毕
 *     boolean await(long timeout, TimeUnit unit):等待计数器为0/到达超时时间后，继续执行后续代码
 *     使用场景，主线程需要等n个线程都执行完毕后，再执行主线程的代码
 */
public class CountDownLatchTest {

    @Test
    @DisplayName("CountDownLatch使用")
    public void demo() throws InterruptedException {
        //初始化线程运行的数量
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 1; i <= 10; i++) {
            Callable<String> callable = () -> {
                Thread.sleep(100);
                return "";
            };
            FutureTask<String> futureTask = new FutureTask<>(callable);
            Thread thread = new Thread(futureTask, "thread1");
            thread.start();
            countDownLatch.countDown();
        }
        countDownLatch.await();
    }
}
