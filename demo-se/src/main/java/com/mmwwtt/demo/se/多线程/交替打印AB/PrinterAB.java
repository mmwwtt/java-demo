package com.mmwwtt.demo.se.多线程.交替打印AB;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class PrinterAB {
    private final Object monitor = new Object();

    private volatile boolean flag = true;


    @Test
    public void testSys() {

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (monitor) {
                    while (!flag) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": foo");
                    flag = false;
                    monitor.notifyAll();
                }
            }
        }, "1");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (monitor) {
                    while (flag) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": bar");
                    flag = true;
                    monitor.notifyAll();
                }
            }
        }, "2");
        thread1.start();
        thread2.start();
    }


    private Semaphore foo = new Semaphore(1);
    private Semaphore bar = new Semaphore(0);

    @Test
    public void testSem() {
        Thread thread1 = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
            try {
                foo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": foo");
            //每个release()方法都会释放持有许可证的线程，并且归还Semaphore一个可用的许可证。
            bar.release();
        }
    }, "1");
        Thread thread2 = new Thread(()-> {
        for (int i = 0; i < 1000; i++) {
            try {
                bar.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": bar");
            foo.release();
        }
    }, "2");
        thread1.start();
        thread2.start();
    }

    private BlockingQueue<Integer> blockBar = new LinkedBlockingQueue<>(1);
    @Test
    public void queueSem() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    blockBar.put(i);
                    System.out.println(Thread.currentThread().getName() + ": foo," + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "1");
        Thread thread2 = new Thread(()-> {
            for (int i = 0; i < 1000; i++) {
                try {
                    blockBar.take();
                    System.out.println(Thread.currentThread().getName() + ": bar," + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "2");
        thread1.start();
        thread2.start();
    }


    //CyclicBarrier: 可循环（cyclic）使用的屏障。组线程到达一个屏障时被阻塞，直到最后一个线程到达屏障时，所有被屏障拦截的线程才会继续干活
    private CyclicBarrier cb = new CyclicBarrier(2);
    private volatile boolean fin = true;

    private void cycFoo() {
        for (int i = 0; i < 1000; i++) {

            while (!fin) ;
            fin = false;
            System.out.println(Thread.currentThread().getName() + ": foo," + i);
            try {
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }

    private void cycBar() {
        for (int i = 0; i < 1000; i++) {

            try {
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + ": bar," + i);
            fin = true;
        }
    }


    private volatile boolean permitFoo = true;

    private void perFoo() {
        for (int i = 0; i < 1000; ) {
            if (permitFoo) {
                System.out.println(Thread.currentThread().getName() + ": foo," + i);
                i++;
                permitFoo = false;
            } else {
                //使用Thread.yield，来防止超时
                Thread.yield();
            }
        }
    }

    private void perBar() {
        for (int i = 0; i < 1000; ) {
            if (!permitFoo) {
                System.out.println(Thread.currentThread().getName() + ": bar," + i);
                i++;
                permitFoo = true;
            } else {
                Thread.yield();
            }
        }
    }

    private Lock reentrantLock = new ReentrantLock(true);
    private final Condition cond = reentrantLock.newCondition();

    private void reFoo() {
        for (int i = 0; i < 1000; i++) {
            reentrantLock.lock();
            try {
                while (!flag) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + ": foo," + i);
                flag = false;
                cond.signal();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    private void reBar() {
        for (int i = 0; i < 1000; i++) {
            reentrantLock.lock();
            try {
                while (flag) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + ": bar," + i);
                flag = true;
                cond.signal();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    //Exchanger类可用于两个线程之间交换信息。可简单地将Exchanger对象理解为一个包含两个格子的容器，通过exchanger方法可以向两个格子中填充信息。
    // 当两个格子中的均被填充时，该对象会自动将两个格子的信息交换，然后返回给线程，从而实现两个线程的信息交换。
    //超过两个线程,得到的结果是随机的.未配对的线程，则会被阻塞，永久等待，直到与之配对的线程到达位置.
    private Exchanger<Integer> ex1 = new Exchanger<>();
    private Exchanger<Integer> ex2 = new Exchanger<>();

    private void exFoo() {

        for (int i = 0; i < 1000; i++) {
            System.out.println(Thread.currentThread().getName() + ": foo," + i);
            try {
                ex1.exchange(1);
                ex2.exchange(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void exBar() {

        for (int i = 0; i < 1000; i++) {
            try {
                ex1.exchange(2);
                System.out.println(Thread.currentThread().getName() + ": bar," + i);
                ex2.exchange(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static Thread thread15 = null;
    private static Thread thread16 = null;

    public static void main(String[] args) throws InterruptedException {
        PrinterAB printer = new PrinterAB();
//
//        Thread thread5 = new Thread(printer::cycFoo, "5");
//        Thread thread6 = new Thread(printer::cycBar, "6");
//        Thread thread7 = new Thread(printer::perFoo, "7");
//        Thread thread8 = new Thread(printer::perBar, "8");
//        Thread thread9 = new Thread(printer::reFoo, "9");
//        Thread thread10 = new Thread(printer::reBar, "10");
//        Thread thread11 = new Thread(printer::exFoo, "11");
//        Thread thread12 = new Thread(printer::exBar, "12");;

        //LockSupport
        thread15 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(Thread.currentThread().getName() + ": foo," + i);
                LockSupport.unpark(thread16);
                LockSupport.park();
            }
        }, "15");

        thread16 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                LockSupport.park();
                System.out.println(Thread.currentThread().getName() + ": bar," + i);
                LockSupport.unpark(thread15);
            }
        }, "16");
//
//        long l1 = System.currentTimeMillis();
//        thread1.start();
//        thread2.start();
//        thread1.join();
//        thread2.join();
//        long l2 = System.currentTimeMillis();
//
//        thread3.start();
//        thread4.start();
//        thread3.join();
//        thread4.join();
//        long l3 = System.currentTimeMillis();
//
//        thread5.start();
//        thread6.start();
//        thread5.join();
//        thread6.join();
//        long l4 = System.currentTimeMillis();
//
//        thread7.start();
//        thread8.start();
//        thread7.join();
//        thread8.join();
//        long l5 = System.currentTimeMillis();
//
//        thread9.start();
//        thread10.start();
//        thread9.join();
//        thread10.join();
//        long l6 = System.currentTimeMillis();
//
//        thread11.start();
//        thread12.start();
//        thread11.join();
//        thread12.join();
//        long l7 = System.currentTimeMillis();
//
//        thread13.start();
//        thread14.start();
//        thread13.join();
//        thread14.join();
//        long l8 = System.currentTimeMillis();
//
//        thread15.start();
//        thread16.start();
//        thread15.join();
//        thread16.join();
//        long l9 = System.currentTimeMillis();
//
//        System.out.println(".......................................................");
//        System.out.println("Semaphore信号量:" + (l2 - l1));
//        System.out.println("BlockingQueue阻塞队列:" + (l3 - l2));
//        System.out.println("CyclicBarrier循环栅栏:" + (l4 - l3));
//        System.out.println("yield:" + (l5 - l4));
//        System.out.println("cas/reentrantLock:" + (l6 - l5));
//        System.out.println("Exchanger:" + (l7 - l6));
//        System.out.println("synchronized:" + (l8 - l7));
//        System.out.println("lockSupport:" + (l9 - l8));
    }

}
