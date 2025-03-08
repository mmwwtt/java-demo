package com.mmwwtt.demo.se.设计模式.SOLID五大设计原则.依赖倒置原则.反例;

import lombok.extern.slf4j.Slf4j;

/**
 * Customer类作为高层模块
 * Apple,Banana作为底层模块
 * 高层模块太过于依赖底层模块(底层模块太具体了，不够抽象)
 * 如果再新增一个西瓜类，又要再写一个shopping方法
 */
@Slf4j
public class Customer {

    public void shopping(Apple apple) {
        log.info("花了 " + apple.getPrice());
    }

    public void shopping(Banana banana) {
        log.info("花了 " + banana.getPrice());
    }
}

class Apple {
    public int getPrice() {
        return 1;
    }
}

class Banana {
    public int getPrice() {
        return 2;
    }
}