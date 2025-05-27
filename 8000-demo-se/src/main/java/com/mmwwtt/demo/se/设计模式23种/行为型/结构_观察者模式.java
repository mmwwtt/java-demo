package com.mmwwtt.demo.se.设计模式23种.行为型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 当被观察者改变时，主动通知观察者做出变动
 */
@Slf4j
public class 结构_观察者模式 {

    @Test
    @DisplayName("测试观察者模式")
    public void test() {
        Observer observer = new Observer();
        Subject subject = new Subject();
        subject.addObserver(observer);
        subject.addCnt();
    }

    //被观察者
    class Subject {
        List<Observer> list;
        int cnt;
        public Subject() {
            this.list = new ArrayList<>();
            this.cnt = 0;
        }
        public void addObserver(Observer observer) {
            list.add(observer);
        }
        public void removeObserver(Observer observer) {
            list.remove(observer);
        }

        //被观察者改变
        public void addCnt() {
            cnt++;
            notifyObserver();
        }

        //通知观察者
        public void notifyObserver() {
            log.info("通知观察者做出改变");
            for (Observer observer : list) {
                observer.response();
            }
        }
    }

    class Observer {
        public void response() {
            log.info("做出相应变动");
        }
    }
}
