package com.mmwwtt.demo.se.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    //ReentrantLock 可重入锁， 同一个线程内持有一个锁后，再试图上锁还是会成功
    private final Lock lock = new ReentrantLock();

    public void test() throws InterruptedException {

        //获取锁， 成功返回 true  失败返回false
        if(lock.tryLock()) {
           //上锁成功
            try {

            }finally {
                //释放锁
                lock.unlock();
            }
        }

        //获取锁， 允许等待一段时间， 超时未获取后 返回fase
        if(lock.tryLock(1, TimeUnit.SECONDS)) {
            //上锁成功
        }
    }
}
