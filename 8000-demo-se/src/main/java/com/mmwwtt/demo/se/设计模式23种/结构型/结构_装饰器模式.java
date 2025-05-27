package com.mmwwtt.demo.se.设计模式23种.结构型;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 结构_装饰器模式 {
    @Test
    @DisplayName("测试装饰器模式")
    public void test() {
        Subject subject = new Subject();
        Decorator decorator = new Decorator(subject);
        decorator.decorate();
    }

    class Subject {
        public void fun() {
            log.info("基本操作");
        }

    }

    @Data
    @AllArgsConstructor
    class Decorator {
        private Subject subject;

        public void decorate() {
            log.info("装饰类新增的步骤");
            subject.fun();
            log.info("装饰类新增的步骤");
        }
    }
}
