package com.mmwwtt.demo.se.延迟队列;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class DelayedTask<T> implements Delayed {
    /**
     * 任务到期时间
     */
    private long deadlineNanos;

    /**
     * 待处理数据
     */
    private T data;

    public DelayedTask(Duration delayTime, T data) {
        this.deadlineNanos = System.nanoTime() + delayTime.toNanos();
        this.data = data;
    }

    /**
     * 查看当前任务还有多久到期
     *
     * @param unit 时间单位
     * @return 到期时间
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Math.max(0, deadlineNanos - System.nanoTime()), TimeUnit.NANOSECONDS);
    }

    /**
     * 延迟队列需要到期时间升序入队，所以我们需要实现compareTo进行到期时间比较
     *
     * @param o 延迟任务
     * @return 比较大小
     */
    @Override
    public int compareTo(Delayed o) {
        long l = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
