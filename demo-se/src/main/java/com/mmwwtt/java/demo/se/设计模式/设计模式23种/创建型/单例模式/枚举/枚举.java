package com.mmwwtt.java.demo.se.设计模式.设计模式23种.创建型.单例模式.枚举;


import org.junit.jupiter.api.Test;

public class 枚举 {

    @Test
    public void testDemo1() {
        Demo1 demo1_1 = Demo1.DEMO1;
        Demo1 demo1_2 = Demo1.DEMO1;
        System.out.println(demo1_1 == demo1_2);
    }
}

/**
 * 利用枚举特性，保证线程安全和单例问题
 * 还可以规避反射/序列化攻击
 *
 * 枚举项：不能通过new来创建对象，枚举类的实例只能通过枚举类.枚举项来获取
 * 创建枚举项会执行其中的无参构造，且只会在第一次时执行
 */
enum Demo1 {
    DEMO1;
    Demo1() {
        System.out.println("构造对象");
    }
}