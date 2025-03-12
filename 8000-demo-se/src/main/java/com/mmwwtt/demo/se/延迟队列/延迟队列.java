package com.mmwwtt.demo.se.延迟队列;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.DelayQueue;

public class 延迟队列 {

    @Test
    public void testDelayQueue() {
        DelayQueue<DelayedTask<String>> delayQueue = new DelayQueue<>();
        //分别添加1s、2s、3s到期的任务，只有到延迟的时间之后，才会被放到队列中
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(2), "Task 2"));
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(1), "Task 1"));
        delayQueue.add(new DelayedTask<>(Duration.ofSeconds(3), "Task 3"));

        new Thread(() -> {
            try {
                while (true){
                    // 获取队首任务，如果队列为空则阻塞
                    DelayedTask<String> messageDelayed = delayQueue.take();
                    //对任务进行操作
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}
