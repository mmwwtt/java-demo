package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式.使用设计模式后;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class D {

    private E e;

    public D() {
        e = new E();
    }

    public void getD() {
        log.info("getD");
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
