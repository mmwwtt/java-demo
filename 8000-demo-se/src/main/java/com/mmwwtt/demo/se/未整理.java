package com.mmwwtt.demo.se;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class 未整理 {
    /**
     * 只有一个抽象方法的接口，可以直接使用lambda表达式来实现
     */
    @Test
    public void 使用() {
        List<Integer> list = new ArrayList<>();
        Collections.sort(list, (Integer a, Integer b) -> {
            /**
             * 返回正数，第一个元素在后面；
             * 返回0,不交换
             * 返回负数，第一个元素在前面；
             */
            return a.intValue() > b.intValue() ? -1 : 1;
        });

    }

    /**
     * 在使用尝试机制来获取锁的lock.tryLock()方式种，进入业务代码块之前，必须判断当前线程是否持有锁
     */
    public void 使用tryLock来尝试获取锁() {
        Lock lock = new ReentrantLock() {

        };

        boolean isLocked = lock.tryLock();
        if (isLocked) {
            try {

            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 多个资源，数据库表，对象，同时加锁时，需要保持一致的枷锁顺序，否则可能死锁
     * lock(m1) lock(m2) unlock(m1) lock(m1) unlock(m2) unlock(m1) 可能会带来死锁
     * 假设有两个操作A,B同时并行执行这个序列
     * 线程A在步骤3 unlock(m1) 后，线程B开始执行步骤1 lock(m1),
     * 此时 线程A锁住了m2,  线程B锁住了m1, 两个线程都无法执行下一步
     */
    public void 要保持枷锁顺序一致() {

    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int a = in.nextInt();
            int b = in.nextInt();
            System.out.println(a + b);
        }
    }

}
