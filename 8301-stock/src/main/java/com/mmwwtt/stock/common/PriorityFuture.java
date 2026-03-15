package com.mmwwtt.stock.common;

import com.mmwwtt.stock.vo.PriorityRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * CompletableFuture + 优先级队列：通过带优先级的线程池提交任务，数字越小越先被调度。
 * 普通方法使用 {@link GlobalThreadPool#getPriorityThreadPool()}；
 * 抢占式方法使用 {@link GlobalThreadPool#getPreemptivePriorityExecutor()}，高优先级入队时会中断低优先级任务并重新入队。
 */
public final class PriorityFuture {

    private static final Executor PRIORITY_EXECUTOR = GlobalThreadPool.getPriorityThreadPool();
    private static final Executor PREEMPTIVE_EXECUTOR = GlobalThreadPool.getPreemptivePriorityExecutor();

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
        CompletableFuture<Void> future = new CompletableFuture<>();
        PRIORITY_EXECUTOR.execute(new PriorityRunnable(priority, () -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }));
        return future;
    }

    /**
     * 带优先级的 supplyAsync，有返回值。数字越小越先执行。
     *
     * @param priority 优先级，越小越先执行
     * @param task     要执行的任务
     * @return CompletableFuture&lt;T&gt;
     */
    public static <T> CompletableFuture<T> supplyAsync(int priority, Callable<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        PRIORITY_EXECUTOR.execute(new PriorityRunnable(priority, () -> {
            try {
                future.complete(task.call());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }));
        return future;
    }

    /**
     * 若需指定自定义的优先级线程池（队列已是 PriorityBlockingQueue），可用此重载。
     */
    public static CompletableFuture<Void> runAsync(int priority, Runnable task, Executor executor) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(new PriorityRunnable(priority, () -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }));
        return future;
    }

    // --------------- 抢占式：高优先级入队时会让当前低优先级任务「休眠」（中断并重新入队） ---------------

    /**
     * 抢占式 runAsync：若有更高优先级任务入队，当前低优先级任务会被中断并重新入队，先执行高优先级。
     * 任务需能响应中断（如循环内检查 {@link Thread#interrupted()} 或抛出 {@link InterruptedException}），否则无法被抢占。
     * 被抢占后任务会从头再执行一次。
     */
    public static CompletableFuture<Void> runAsyncPreemptive(int priority, Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        PREEMPTIVE_EXECUTOR.execute(new PriorityRunnable(priority, () -> {
            try {
                task.run();
                if (Thread.interrupted()) throw new PreemptedException();
                future.complete(null);
            } catch (InterruptedException e) {
                throw new PreemptedException(e);
            } catch (PreemptedException e) {
                throw e;
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }));
        return future;
    }

    /**
     * 抢占式 supplyAsync，语义同 {@link #runAsyncPreemptive(int, Runnable)}。
     */
    public static <T> CompletableFuture<T> supplyAsyncPreemptive(int priority, Callable<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        PREEMPTIVE_EXECUTOR.execute(new PriorityRunnable(priority, () -> {
            try {
                T result = task.call();
                if (Thread.interrupted()) throw new PreemptedException();
                future.complete(result);
            } catch (InterruptedException e) {
                throw new PreemptedException(e);
            } catch (PreemptedException e) {
                throw e;
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }));
        return future;
    }
}
