package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.中介者模式;

public class C {

    private A a;
    private B b;
    private D d;

    public C() {
        a = new A();
        b = new B();
        d = new D();
    }

    public void getC() {
        System.out.println("getC");
    }

    public void getA() {
        a.getA();
    }

    public void getB() {
        b.getB();
    }

    public void getD() {
        d.getD();
    }
}
