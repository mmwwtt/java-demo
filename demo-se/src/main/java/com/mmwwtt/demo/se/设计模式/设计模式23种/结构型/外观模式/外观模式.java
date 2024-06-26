package com.mmwwtt.demo.se.设计模式.设计模式23种.结构型.外观模式;

/**
 * 为子系统中的一组接口提供一个统一的入口
 */
public class 外观模式 {
    public static void main(String[] args) {
        /**
         * 不直接调用各个子系统，
         * 而是通过外观类，调用外观类中的方法，
         * 再由外观类调用各个子系统中的方法
         */
        Facade facade = new Facade();
        facade.fun();
    }
}

/**
 * 外观角色
 */
class Facade {
    public A a;
    public B b;
    public C c;
    public Facade() {
        a = new A();
        b = new B();
        c = new C();
    }
    public void fun() {
        a.fun();
        b.fun();
        c.fun();
    }
}
class A {
    public void fun() {

    }
}

class B {
    public void fun() {

    }
}

class C {
    public void fun() {

    }
}