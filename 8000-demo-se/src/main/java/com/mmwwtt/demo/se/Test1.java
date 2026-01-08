package com.mmwwtt.demo.se;

import com.mmwwtt.demo.se.thread.GlobalThreadPool;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Test1 {

    private static final ThreadLocal<List<Integer>> threadLocal = new ThreadLocal<>();
    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();
    private static final List<Integer> list = new ArrayList<>();

    @Test
    public  void test1() {
        pool.submit(() -> {
            threadLocal.set(list);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Integer> tmpList = threadLocal.get();
            tmpList.add(22);
            tmpList.add(33);
            tmpList.add(44);
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(tmpList.toString());
            }
        });

        pool.submit(() -> {
            threadLocal.set(list);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Integer> tmpList = threadLocal.get();
            tmpList.add(555);
            tmpList.add(666);
            tmpList.add(777);
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(tmpList.toString());
            }
        });
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pool.shutdown();

    }

    @Test
    public void test2() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("1", Collections.singletonList("1"));
        map.get("1").add("2");
    }
}
