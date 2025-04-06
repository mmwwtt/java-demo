package com.mmwwtt.demo.se.多线程;

import com.mmwwtt.demo.se.多线程.线程池.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class 创建线程 {

    private ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    /**
     * 继承Thread类，实现run方法
     *
     * @throws InterruptedException
     */
    @Test
    public void testThread() {
        Thread thread = new Thread(() -> {
            log.info("创建了线程");
        }, "thread1");
        thread.start();

        //获得线程优先级
        thread.getPriority();
    }

    /**
     * 实现Rallable接口
     */
    @Test
    public void testRunnable() {
        Runnable runnable = () -> {
            log.info("创建了线程");
        };
        Thread thread = new Thread(runnable, "thread1");
        thread.start();
    }

    /**
     * 实现Callable接口,可获得线程中放大的返回值
     */
    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        Callable<String> callable = () -> {
            log.info("创建了线程");
            return "hello";
        };
        //获得线程后的返回值
        FutureTask<String> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask, "thread1");
        thread.start();
        log.info("{}",futureTask.get());
    }

    //使用线程池提交任务
    @Test
    public void testFutureTask() throws ExecutionException, InterruptedException {
        Callable<String> callable = () -> {
            log.info("创建了线程");
            Thread.sleep(10000);
            return "hello";
        };
        FutureTask<String> futureTask = new FutureTask<>(callable);
        pool.submit(futureTask);
        log.info("{}",futureTask.get());
    }

    /**
     * 使用CompletableFuture创建线程
     * 支持返回值，链式调用，异常处理，组合操作
     */
    @Test
    public void testCompletableFuture() throws ExecutionException, InterruptedException {
        //有返回值的线程
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            log.info("创建了线程");
            return "hello";
        }, pool);

        //无返回值的线程
        CompletableFuture.runAsync(() -> {
            log.info("创建了线程");
        }, pool);

        // 链式调用 thenApply 对结果进行处理，返回新结果
        CompletableFuture<String> future2 = future1.thenApply(result -> result + " CompletableFuture");
        // thenAccept 消费异步任务的结果
        future2.thenAccept(result -> System.out.println(result));
        // 执行无返回值的操作
        future1.thenRun(() -> System.out.println("任务完成"));

        // 对异常处理
        future1.exceptionally(ex -> {
            System.out.println("捕获异常: " + ex.getMessage());
            return "默认值";
        }).thenAccept(result -> System.out.println("任务结果: " + result));

        // 处理结果或异常
        future1.handle((result, ex) -> {
            if (ex != null) {
                System.out.println("捕获异常: " + ex.getMessage());
                return "默认值";
            }
            return result;
        }).thenAccept(result -> System.out.println("任务结果: " + result));


        // 组合两个任务的结果
        CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> result1 + " " + result2);
        // 消费结果
        combinedFuture.thenAccept(result -> System.out.println("组合结果: " + result));

        // 消费两个任务的结果
        future1.thenAcceptBoth(future2, (result1, result2) -> System.out.println("组合结果: " + result1 + " " + result2));
    }


    /**
     * 多线程执行完任务后，将结果合并返回
     * @throws Exception
     */
    @Test
    public void testSubmitAndCombineResults() throws Exception {
        // 提交第一个任务
        CompletableFuture<List<String>> futureA = CompletableFuture.supplyAsync(() -> {
            return List.of("hello", "a");
        }, pool);

        // 提交第二个任务
        CompletableFuture<List<String>> futureB = CompletableFuture.supplyAsync(() -> {
            return List.of("world", "b");
        }, pool);

        // 合并两个任务的结果
        CompletableFuture<List<String>> combinedFuture = futureA.thenCombine(futureB, (list1, list2) -> {
            List<String> combinedList = new ArrayList<>(list1);
            combinedList.addAll(list2);
            return combinedList;
        });

        // 等待合并后的结果
        List<String> combinedResult = combinedFuture.get();
        log.info("Combined result: " + combinedResult);
    }
}
