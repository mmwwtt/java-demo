package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式;

import lombok.extern.slf4j.Slf4j;

/**
 *     应用A调用BCD
 *     应用B调用ACD
 *     应用C调用ABD
 *     应用D调用ABC
 *     使用E来作为中介者，应用A、B、C、D都只调用E，应用E来调用ABCD
 */
@Slf4j
public class A {

    private B b;
    private C c;
    private D d;

    public A() {
        b = new B();
        c = new C();
        d = new D();
    }

    public void getA() {
        log.info("getA");
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
