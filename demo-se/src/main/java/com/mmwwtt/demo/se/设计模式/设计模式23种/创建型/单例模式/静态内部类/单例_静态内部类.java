package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.单例模式.静态内部类;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 单例_静态内部类 {
    @Test
    public void testDemo1() {
        Demo1 demo1_1 = Demo1.getDemo1();
        Demo1 demo1_2 = Demo1.getDemo1();
        log.info("{}",demo1_1 == demo1_2);
    }
}

class Demo1 {
    private static class Demo1Builder {
        private static Demo1 demo1 = new Demo1();
    }

    private Demo1() {}

    public static Demo1 getDemo1() {
        return Demo1Builder.demo1;
    }
}