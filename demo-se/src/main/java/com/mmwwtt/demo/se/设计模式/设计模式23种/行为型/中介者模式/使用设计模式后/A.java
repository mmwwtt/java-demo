package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式.使用设计模式后;

public class A {

    private E e;

    public A() {
        e = new E();
    }

    public void getA() {
        System.out.println("getA");
    }

    public void getB() {
        e.getB();
    }

    public void getC() {
        e.getC();
    }

    public void getD() {
        e.getD();
    }
}
