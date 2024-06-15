package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.责任链模式;

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

class Handler1 extends Handler {

    @Override
    public void handleRequest(int info) {
        System.out.println("Handler1处理");
        if (getNext() != null) {
            getNext().handleRequest(info);
            return;
        }
        System.out.println("结束");
    }
}

class Handler2 extends Handler {

    @Override
    public void handleRequest(int info) {
        System.out.println("Handler2处理");
        if (getNext() != null) {
            getNext().handleRequest(info);
            return;
        }
        System.out.println("结束");
    }
}