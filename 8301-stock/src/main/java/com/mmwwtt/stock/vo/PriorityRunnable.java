package com.mmwwtt.stock.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    private final int priority; // 数字越小越先执行
    private final Runnable task;

    @Override
    public void run() {
        task.run();
    }

    @Override
    public int compareTo(PriorityRunnable o) {
        return Integer.compare(this.priority, o.priority);
    }
}
