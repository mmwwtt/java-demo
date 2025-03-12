package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.中介者模式.使用设计模式后;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class B {

    private E e;

    public B() {
        e = new E();
    }

    public void getB() {
        log.info("getB");
    }

    public void getA() {
        e.getA();
    }

    public void getC() {
        e.getC();
    }

    public void getD() {
        e.getD();
    }
}
