package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.责任链模式;

import lombok.extern.slf4j.Slf4j;

/**
 * 责任链模式
 */
public class Demo {

    public static void main(String[] args) {
        Handler handler1 = new Handler1();
        Handler handler2 = new Handler2();
        handler1.setNext(handler2);
        handler1.handleRequest(10);
    }

}

abstract class Handler {
    private Handler next;
    public void setNext(Handler handler) {
        this.next = handler;
    }
    public Handler getNext() {
        return next;
    }
    public abstract void handleRequest(int info);
}

@Slf4j
class Handler1 extends Handler {

    @Override
    public void handleRequest(int info) {
        log.info("Handler1处理");
        if (getNext() != null) {
            getNext().handleRequest(info);
            return;
        }
        log.info("结束");
    }
}

@Slf4j
class Handler2 extends Handler {

    @Override
    public void handleRequest(int info) {
        log.info("Handler2处理");
        if (getNext() != null) {
            getNext().handleRequest(info);
            return;
        }
        log.info("结束");
    }
}