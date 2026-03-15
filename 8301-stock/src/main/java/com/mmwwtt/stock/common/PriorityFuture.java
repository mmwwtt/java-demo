package com.mmwwtt.stock.common;

import com.mmwwtt.stock.vo.PriorityRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * CompletableFuture + 优先级队列：通过带优先级的线程池提交任务，数字越小越先被调度。
 * 内部使用 {@link GlobalThreadPool#getPriorityThreadPool()}，队列为 PriorityBlockingQueue。
 */
public final class PriorityFuture {

    private static final Executor PRIORITY_EXECUTOR = GlobalThreadPool.getPriorityThreadPool();

    private PriorityFuture() {
    }

    /**
     * 带优先级的 runAsync，无返回值。数字越小越先执行。
     *
     * @param priority 优先级，越小越先执行（如 0=高，10=低）
     * @param task     要执行的任务
     * @return CompletableFuture&lt;Void&gt;
     */
    public static CompletableFuture<Void> runAsync(int priority, Runnable task) {
        return CompletableFuture.runAsync(new PriorityRunnable(priority, task), PRIORITY_EXECUTOR);
    }

    /**
     * 带优先级的 supplyAsync，有返回值。数字越小越先执行。
     *
     * @param priority 优先级，越小越先执行
     * @param task     要执行的任务
     * @return CompletableFuture&lt;T&gt;
     */
    public static <T> CompletableFuture<T> supplyAsync(int priority, Callable<T> task) {
        Executor exec = r -> PRIORITY_EXECUTOR.execute(new PriorityRunnable(priority, r));
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, exec);
    }

    /**
     * 若需指定自定义的优先级线程池（队列已是 PriorityBlockingQueue），可用此重载。
     */
    public static CompletableFuture<Void> runAsync(int priority, Runnable task, Executor executor) {
        return CompletableFuture.runAsync(new PriorityRunnable(priority, task), executor);
    }
}
