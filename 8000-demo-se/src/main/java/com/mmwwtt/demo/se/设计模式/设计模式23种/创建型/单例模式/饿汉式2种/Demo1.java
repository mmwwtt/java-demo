package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.单例模式.饿汉式2种;

/**
 * 饿汉式-静态常量
 * 类加载器加载类时，就实例化单例
 * 缺点：不论是否用到，都会创建，浪费资源
 *构造方法私有：避免被直接new出来
 */
public class Demo1 {
    private static final Demo1 demo1 = new Demo1();

    private Demo1() {}

    public static Demo1 getDemo1() {
        return demo1;
    }
}

class Demo2 {
    private static Demo2 demo2;

    static {
        demo2 = new Demo2();
    }

    private Demo2() {}

    public static Demo2 getDemo2() {
        return demo2;
    }
}