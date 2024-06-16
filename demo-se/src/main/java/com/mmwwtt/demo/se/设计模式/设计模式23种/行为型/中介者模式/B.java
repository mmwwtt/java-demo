package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式;

public class B {

    private A a;
    private C c;
    private D d;

    public B() {
        a = new A();
        c = new C();
        d = new D();
    }

    public void getB() {
        System.out.println("getB");
    }

    public void getA() {
        a.getA();
    }

    public void getC() {
        c.getC();
    }

    public void getD() {
        d.getD();
    }
}
