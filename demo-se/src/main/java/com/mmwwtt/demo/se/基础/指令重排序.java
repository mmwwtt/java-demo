package com.mmwwtt.demo.se.基础;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class 指令重排序 {
    @Test
    public void 指令重排序() {
        ExecutorService pool = Executors.newFixedThreadPool(1000);
        for(int i =0;i<1000;i++){
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Demo demo = new Demo();
                    demo.actor1();
                    demo.actor2();
                }
            });
        }

    }
}

class Demo {

    public static int num = 0;
    public static boolean ready = false;
    // 线程1 执行此方法
    public void actor1() {
        if(ready) {
            System.out.println(num);
        }
    }
    // 线程2 执行此方法
    public void actor2() {
        num = 2;
        ready = true;
    }

}
