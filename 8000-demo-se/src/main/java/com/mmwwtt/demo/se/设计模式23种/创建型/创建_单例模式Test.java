package com.mmwwtt.demo.se.设计模式23种.创建型;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class 创建_单例模式Test {

    @Test
    @DisplayName("测试单例模式")
    public void test1() {
        Demo1 demo1 = Demo1.getInstance();
        Demo2 demo2 = Demo2.getINSTANCE();
    }

    /**
     * 饿汉式-静态常量
     * 类加载器加载类时，就实例化单例
     * 缺点：不论是否用到，都会创建，浪费资源
     */
    public static class Demo1 {
        private static final Demo1 INSTANCE = new Demo1();

        //构造方法私有：避免被直接new出来
        private Demo1() {
        }

        public static Demo1 getInstance() {
            return INSTANCE;
        }
    }


    /**
     * 饿汉式-双重校验
     * 既提升了性能，也保证了线程安全
     * volatile保证了对象创建后，其他线程立即可知
     */
    public static class Demo2 {
        private volatile static Demo2 INSTANCE = null;

        //构造方法私有：避免被直接new出来
        private Demo2() {
        }

        public static Demo2 getINSTANCE() {
            //判断单例是否生成
            if (INSTANCE == null) {
                //防止多个线程同时访问
                synchronized (Demo2.class) {
                    //如果多个线程同时访问后，判断单例是否已经生成过
                    if (INSTANCE == null) {
                        INSTANCE = new Demo2();
                    }
                }
            }
            return INSTANCE;
        }
    }
}