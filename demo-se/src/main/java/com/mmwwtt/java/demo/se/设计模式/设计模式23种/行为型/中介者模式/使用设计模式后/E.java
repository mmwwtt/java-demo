package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.中介者模式.使用设计模式后;

public class E {

    private A a;
    private B b;
    private C c;
    private D d;

    public E() {
        a = new A();
        b = new B();
        c = new C();
        d = new D();
    }

    public void getA() {
        a.getA();
    }

    public void getB() {
        b.getB();
    }

    public void getC() {
        c.getC();
    }

    public void getD() {
        d.getD();
    }
}
