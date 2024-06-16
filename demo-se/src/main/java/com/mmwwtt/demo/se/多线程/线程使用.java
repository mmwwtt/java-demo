package com.mmwwtt.demo.se.多线程;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class 线程使用 {

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
     *  假设有两个操作A,B同时并行执行这个序列
     *  线程A在步骤3 unlock(m1) 后，线程B开始执行步骤1 lock(m1),
     *  此时 线程A锁住了m2,  线程B锁住了m1, 两个线程都无法执行下一步
     */
    public void 要保持枷锁顺序一致() {

    }
}
