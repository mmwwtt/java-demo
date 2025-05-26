package com.mmwwtt.demo.se.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class ThreadTest {

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    @Test
    @DisplayName("线程安全的类")
    public void test() {
        Stack<Integer> stack = new Stack<>();
        Vector<Integer> vector = new Vector<>();
        Map<String, String> hashtable = new Hashtable<>();
        Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        StringBuffer stringBuffer = new StringBuffer();

        //将线程不安全集合通过API 转为 线程安全的集合
        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        Set<String> set =  Collections.synchronizedSet(new HashSet<>());
        List<String> list = Collections.synchronizedList(new ArrayList<>());
    }


    @Test
    @DisplayName("继承Thread类(实现了runnable接口)，实现run方法")
    public void test1() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("创建了线程");
        }, "thread1");
        thread.start();

        //等待线程执行完毕
        thread.join();
    }

    @Test
    @DisplayName("实现Runnable接口，实现run方法")
    public void test2() throws InterruptedException {
        Runnable runnable = () -> log.info("创建了线程");
        Thread thread = new Thread(runnable, "thread1");
        thread.start();
        thread.join();
    }

    @Test
    @DisplayName("实现Callable接口,线程有返回值")
    public void test3() throws ExecutionException, InterruptedException {
        Callable<String> callable = () -> {
            log.info("创建了线程");
            return "hello";
        };
        //获得线程后的返回值
        FutureTask<String> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask, "thread1");
        thread.start();
        log.info("{}",futureTask.get());
        thread.join();
    }

    @Test
    @DisplayName("线程池提交任务")
    public void test4() {
        //execute执行任务无返回值
        pool.execute(() -> {

        });
        Runnable runnable = () -> {
            log.info("创建了线程");
        };
        pool.execute(runnable);


        Callable<String> callable = () -> {
            log.info("创建了线程");
            return "hello";
        };
        Future<String> future = pool.submit(callable);
        //获得线程返回值和 异常处理
        try {
            log.info("{}",future.get());
        } catch (Exception e) {
            log.info("error");
        }
        pool.shutdown();
    }

    @Test
    @DisplayName("CompletableFuture基本使用")
    public void test5() throws ExecutionException, InterruptedException {
        //有返回值的线程
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            log.info("创建了线程");
            return "hello";
        }, pool);

        //无返回值的线程
        CompletableFuture.runAsync(() -> {
            log.info("创建了线程");
        }, pool);

        //thenApply 接收上一步的结果，处理后返回新结果
        CompletableFuture<String> future2 = future1.thenApply(result -> result + " CompletableFuture");

        //thenAccept 接收上一步的结果，处理后无返回
        future2.thenAccept(System.out::println);

        //thenAccept 进行下一步处理
        future1.thenRun(() -> System.out.println("任务完成"));

        //获得线程返回值
        String res = future2.get();

        // exceptionally 只有异常后才执行
        future1.exceptionally(ex -> {
            System.out.println("捕获异常: " + ex.getMessage());
            return "默认值";
        }).thenAccept(result -> System.out.println("任务结果: " + result));

        // handle 无论异常都会执行，同时处理任务成功/失败
        future1.handle((result, ex) -> {
            if (ex != null) {
                System.out.println("捕获异常: " + ex.getMessage());
                return "默认值";
            }
            return result;
        }).thenAccept(result -> System.out.println("任务结果: " + result));
    }

    @Test
    @DisplayName("CompletableFuture合并2个任务结果")
    public void test6() throws Exception {
        // 提交第一个任务
        CompletableFuture<List<String>> future1 = CompletableFuture.supplyAsync(() -> {
            return List.of("hello", "a");
        }, pool);

        // 提交第二个任务
        CompletableFuture<List<String>> future2 = CompletableFuture.supplyAsync(() -> {
            return List.of("world", "b");
        }, pool);

        // 处理两个任务的结果并返回新结果
        CompletableFuture<List<String>> combinedFuture = future1.thenCombine(future2, (list1, list2) -> {
            List<String> combinedList = new ArrayList<>(list1);
            combinedList.addAll(list2);
            return combinedList;
        });
        List<String> combinedResult = combinedFuture.get();

        // 处理两个任务的结果，不返回新结果
        future1.thenAcceptBoth(future2, (result1, result2) -> System.out.println("组合结果: " + result1 + " " + result2));

        log.info("Combined result: {}", combinedResult);
    }

    @Test
    @DisplayName("CompletableFuture合并n个任务的结果")
    public void test7() {
        List<CompletableFuture<String>> futureList =  Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < 10; i++) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return "success";
            });
            futureList.add(future);
        }
        List<String> resList = futureList.stream()
            .map(item -> {
                try {
                    return item.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();
        log.info(resList.toString());
    }

    @Test
    @DisplayName("CompletableFuture等n个任务同时处理完后再处理下一个")
    public void test8() throws ExecutionException, InterruptedException {
        // 创建多个异步任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return "success";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "success";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            return "success";
        });

        // 使用 allOf 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

        // 等待 allOf 完成
        allFutures.get();
        //等所有任务执行后再执行后续步骤
        allFutures.thenRun(()->{});
    }


    /**
     * interrupt():会释放线程持有的资源(数据库连接，锁等),会等待当前线程执行完
     */
    @Test
    @DisplayName("使用Thread.currentThread().interrupt()中断线程")
    public void test9() {
        pool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                log.info("任务正在执行，线程: {}", Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("线程被中断，准备退出...");
                    throw new RuntimeException(e);
                }
            }
        });
        pool.shutdown();
    }

    /**
     * shutdown()：平滑关闭线程池，不再接受新任务，等待当前线程池中任务完成后调用每个线程的 interrupt()
     * shutdownNow()：立即关闭线程池，中断所有正在执行的任务，并返回尚未执行的任务列表。
     */
    @Test
    @DisplayName("使用shutdownNow立即中断所有线程")
    public void test10() {
        pool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("任务正在执行，线程: " + Thread.currentThread().getName());
            }
        });
        //中断所有线程
        pool.shutdownNow();
    }

    /**
     * stop()立即中断线程，不会释放持有的资源(数据库连接，锁等)，导致内存泄露，死锁等
     */
    @Test
    @DisplayName("不推荐使用stop中断线程")
    public void test11() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
        t.stop();
        System.out.println("主线程中断了子线程");
    }
}
