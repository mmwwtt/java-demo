package com.mmwwtt.demo.se.设计模式.SOLID五大设计原则.依赖倒置原则.正例;

import lombok.extern.slf4j.Slf4j;

/**
 * 将Apple类和Banan类抽象成Goods类
 * 让Customer依赖于Goods类-> 高层模块依赖于抽象
 * 让Apple类，Banana实现Goods类  -> 底层模块依赖于抽象
 */
@Slf4j
public class Customer {

    public void shopping(Goods goods) {
        log.info("花了 " + goods.getPrice());
    }
}

abstract class Goods {
    public abstract int getPrice();
}

class Apple extends Goods{

    @Override
    public int getPrice() {
        return 1;
    }
}

class Banana extends Goods{

    @Override
    public int getPrice() {
        return 2;
    }
}