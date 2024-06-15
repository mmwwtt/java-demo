package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.中介者模式.使用设计模式后;

public class D {

    private E e;

    public D() {
        e = new E();
    }

    public void getD() {
        System.out.println("getD");
    }

    public void getA() {
        e.getA();
    }

    public void getB() {
        e.getB();
    }

    public void getC() {
        e.getC();
    }
}
