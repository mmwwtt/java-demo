package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.模板方法模式;

/**
 * 模板方法
 *  优：将公共的代码放到父类中，实现复用
 *      父类中进行方法申明，子类中实现详细细节
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

abstract class A {

    /**
     * 模板方法
     */
    public void sayHello() {
        System.out.println("hello");
    }

    /**
     * 非模板方法
     */
    public abstract void sayName();
}

class B extends A {


    @Override
    public void sayName() {
        System.out.println("B");
    }
}

class C extends A {

    @Override
    public void sayName() {
        System.out.println("A");
    }
}