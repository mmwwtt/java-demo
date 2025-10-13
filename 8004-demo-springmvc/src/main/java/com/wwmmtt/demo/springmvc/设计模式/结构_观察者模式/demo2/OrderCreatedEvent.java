package com.wwmmtt.demo.springmvc.设计模式.结构_观察者模式.demo2;

import org.springframework.context.ApplicationEvent;

import java.util.List;


public class OrderCreatedEvent extends ApplicationEvent {
    private final List<Integer> ids;


    public OrderCreatedEvent(Object source, List<Integer> ids) {
        super(source);
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }
}