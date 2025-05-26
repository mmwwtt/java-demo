package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.单例模式.懒汉式4种;


/**
 * 懒汉式
 * 线程不安全
 * 可能存在两个线程同时判断 demo1 == null 为true,而new出两个实例
 */
class Demo1 {
    private static Demo1 demo1;

    private Demo1() {}

    public static Demo1 getDemo1() {
        if (demo1 == null) {
            demo1 = new Demo1();
        }
        return demo1;
    }
}

/**
 * 懒汉式
 * 线程安全
 * 但是每次getDemo2时，都要同步，效率低
 */
class Demo2 {
    private static Demo2 demo2;

    private Demo2() {}

    public static synchronized Demo2 getDemo2() {
        if (demo2 == null) {
            demo2 = new Demo2();
        }
        return demo2;
    }
}

/**
 * 懒汉式
 * 线程不安全
 * 为了减少Demo2中的同步范围，在demo3 == null时再进行上锁同步
 * 当两个线程都判断 demo3 == null时，会先后创建对象实例，所以线程不安全
 */
class Demo3 {
    private static Demo3 demo3;

    private Demo3() {}

    public static Demo3 getDemo3() {
        if (demo3 == null) {
            synchronized (Demo3.class) {
                demo3 = new Demo3();
            }
        }
        return demo3;
    }
}

/**
 * 饿汉式-双重校验
 * 既提升了性能，也保证了线程安全
 * volatile保证了对象创建后，其他线程立即可知
 */
public class Demo4 {
    private volatile static Demo4 demo4 = null;

    private Demo4() {}

    public static Demo4 getDemo4() {
        if (demo4 == null) {
            synchronized (Demo4.class) {
                if (demo4 == null) {
                    demo4 = new Demo4();
                }
            }
        }
        return demo4;
    }
}