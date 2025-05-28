package com.mmwwtt.demo.se.设计模式23种;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 备忘录模式：类似象棋悔棋
 */
@Slf4j
public class 行为_备忘录模式Test {
    @Test
    @DisplayName("测试备忘录模式")
    public void test() {
        Originator originator = new Originator();
        originator.setState("1");
        Caretaker caretaker = new Caretaker();
        caretaker.setMemento(originator.createMemento());
        log.info(originator.getState());

        originator.setState("2");
        log.info(originator.getState());

        originator.restoreMemento(caretaker.getMemento());
        log.info(originator.getState());
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
}
