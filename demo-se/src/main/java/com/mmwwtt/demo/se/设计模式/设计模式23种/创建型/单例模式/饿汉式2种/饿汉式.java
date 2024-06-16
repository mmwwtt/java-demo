package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.单例模式.饿汉式2种;


import org.junit.jupiter.api.Test;

public class 饿汉式 {

    @Test
    public void testDemo1() {
        Demo1 demo1_1 = Demo1.getDemo1();
        Demo1 demo1_2 = Demo1.getDemo1();
        System.out.println(demo1_1 == demo1_2);
    }

    @Test
    public void testDemo2() {
        Demo2 demo2_1 = Demo2.getDemo2();
        Demo2 demo2_2 = Demo2.getDemo2();
        System.out.println(demo2_1 == demo2_2);
    }
}

/**
 * 饿汉式-静态常量
 * 当类加载器将类加载到内存时，就实例化一个单例
 * 缺点：不论是否用到，都会创建时例，浪费资源
 *
 * 构造方法私有：避免被直接new出来
 */
class Demo1 {
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