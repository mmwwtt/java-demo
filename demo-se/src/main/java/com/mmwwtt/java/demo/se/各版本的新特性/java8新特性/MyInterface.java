package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import org.junit.jupiter.api.Test;

/**
 * 接口中允许有具体实现的方法
 */
public interface MyInterface {
    public default void say() {
        System.out.println("jdk8新增特性-接口默认方法");
    }
}

class MyClass implements MyInterface {

    @Test
    public void test() {
        MyClass myClass = new MyClass();
        myClass.say();
    }
}