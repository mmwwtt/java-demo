package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.备忘录模式;

import lombok.Data;

/**
 * 备忘录模式：类似象棋悔棋
 */
public class 备忘录模式 {
    public static void main(String[] args) {
        Originator originator = new Originator();
        originator.setState("1");
        Caretaker caretaker = new Caretaker();
        caretaker.setMemento(originator.createMemento());
        System.out.println(originator.getState());

        originator.setState("2");
        System.out.println(originator.getState());

        originator.restoreMemento(caretaker.getMemento());
        System.out.println(originator.getState());
    }
}

/**
 * 备忘录
 */
@Data
class Memento {
    private String state;
}

/**
 * 原发器：记录当前类的状态
 */
@Data
class Originator {
    private String state;
    public Memento createMemento() {
        Memento memento = new Memento();
        memento.setState(state);
        return memento;
    }

    public void restoreMemento(Memento memento) {
        this.state = memento.getState();
    }
}

/**
 * 负责人，保存备忘录
 */
@Data
class Caretaker {
    private Memento memento;
}