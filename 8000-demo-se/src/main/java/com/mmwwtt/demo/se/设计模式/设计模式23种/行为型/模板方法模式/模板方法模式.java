package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.模板方法模式;

import lombok.extern.slf4j.Slf4j;

/**
 * 模板方法
 * 优：将公共的代码放到父类中，实现复用
 * 父类中进行方法申明，子类中实现详细细节
 */
public abstract class 模板方法模式 {
    public static void main(String[] args) {
        A b = new B();
        A c = new C();
        b.sayHello();
        c.sayHello();

        b.sayName();
        c.sayName();
    }
}

@Slf4j
abstract class A {

    /**
     * 模板方法
     */
    public void sayHello() {
        log.info("hello");
    }

    /**
     * 非模板方法
     */
    public abstract void sayName();
}

@Slf4j
class B extends A {


    @Override
    public void sayName() {
        log.info("B");
    }
}

@Slf4j
class C extends A {

    @Override
    public void sayName() {
        log.info("A");
    }
}