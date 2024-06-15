package com.mmwwtt.java.demo.se.多线程;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class 创建线程 {

    /**
     * 实现Thread类来创建线程
     *
     * @throws InterruptedException
     */
    @Test
    public void testThread() throws InterruptedException {
        Thread thread = new Thread(() -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            System.out.println(sum);
        }, "thread1");
        thread.start();
    }

    @Test
    public void testRunnable() {
        Runnable runnable = () -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            System.out.println(sum);
        };
        Thread thread = new Thread(runnable, "thread1");
        thread.start();
    }

    /**
     * 实现Callable接口
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        Callable<String> callable = () -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        };
        FutureTask<String> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask, "thread1");
        thread.start();
        System.out.println(futureTask.get());
    }

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        Callable<String> callable = () -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        };
        Future<String> future = service.submit(callable);

        //获取callable接口中的异常
        try {
            System.out.println("myTask任务执行结果为" + future.get());
        } catch (InterruptedException e) {
            System.out.println("任务被中断！");
        } catch (ExecutionException e) {
            System.out.println("任务内部抛出未受检异常！");
        } catch (CancellationException e){
            System.out.println("任务被取消！");
        }
        System.out.println(future.get());
    }

    @Test
    public void testFutureTask() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        Callable<String> callable = () -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        };
        FutureTask<String> futureTask = new FutureTask<>(callable);
        service.submit(futureTask);
        System.out.println(futureTask.get());
    }


    @Test
    public void testCompletableFuture() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            int sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        }, service);
        System.out.println(completableFuture.get());
    }
}
