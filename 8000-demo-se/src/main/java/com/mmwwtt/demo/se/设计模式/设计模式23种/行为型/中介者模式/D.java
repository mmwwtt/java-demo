package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class D {

    private A a;
    private B b;
    private C c;

    public D() {
        a = new A();
        b = new B();
        c = new C();
    }

    public void getD() {
        log.info("getD");
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
}
