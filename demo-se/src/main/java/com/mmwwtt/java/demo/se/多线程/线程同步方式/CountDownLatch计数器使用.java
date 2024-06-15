package com.mmwwtt.java.demo.se.多线程.线程同步方式;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

public class CountDownLatch计数器使用 {



    /**
     *      new CountDownLatch(int count):count为计数器的初始值(一般需要多少个先吃执行，count就设置为几)
     *    countDown():每调用一次计数器值-1，直到count被减为0，代表所有线程全部执行完毕。
     *     getCount():获取当前的计数器值。
     *     await():等待计数器变为0，即等待所有的异步线程执行完毕
     *     boolean await(long timeout, TimeUnit unit):此方法至多会等待指定的时间，然后超时会自动唤醒，若timeout小于等于0，则不会等待。当计数器变为0，则返回true。若指定的等待时间过去了，则返回false。
     *
     * @throws InterruptedException
     */
    @Test
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
