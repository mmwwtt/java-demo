package com.mmwwtt.demo.se.设计模式.设计模式23种.结构型.装饰器模式;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

public class 装饰器类 {
    @Test
    public void test() {
        Subject subject = new Subject();
        Decorator decorator = new Decorator(subject);
        decorator.decorate();
    }
}

/**
 * 被装饰的类
 */
@Slf4j
class Subject {
    public void fun() {
        log.info("基本操作");
    }
}

@Data
@AllArgsConstructor
@Slf4j
class Decorator {
    private Subject subject;

    public void decorate() {
        log.info("装饰类新增的步骤");
        subject.fun();
        log.info("装饰类新增的步骤");
    }
}