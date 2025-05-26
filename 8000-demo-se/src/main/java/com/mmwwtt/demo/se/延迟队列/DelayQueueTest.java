package com.mmwwtt.demo.se.延迟队列;

import com.mmwwtt.demo.se.thread.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DelayQueueTest {

    public static final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    @Test
    @DisplayName("延迟队列基本使用")
    public void testDelayQueue() throws InterruptedException {
        DelayQueue<DelayedTask<String>> delayQueue = new DelayQueue<>();
        //分别添加1s、2s、3s到期的任务，只有到延迟的时间之后，才会被放到队列中
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(2), "Task 2"));
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(1), "Task 1"));
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(3), "Task 3"));

        pool.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    // 获取队首任务，如果队列为空则阻塞
                    DelayedTask<String> messageDelayed = delayQueue.take();
                    //对任务进行操作
                    log.info(messageDelayed.getData());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        //等线程池中的方法全部执行完，超时后返回false
        Boolean flag = pool.awaitTermination(60, TimeUnit.SECONDS);
    }
}
