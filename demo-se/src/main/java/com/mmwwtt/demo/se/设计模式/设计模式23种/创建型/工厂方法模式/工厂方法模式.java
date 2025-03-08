package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.工厂方法模式;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 只抽象了产品
 */
public class 工厂方法模式 {

    @Test
    public void test() {
        Factory factory = new Factory();
        Product product = factory.getConcreteProductA();
        product.getName();
    }
}

/**
 * 抽象产品
 */
abstract class Product {
    public abstract void getName();
}

/**
 * 具体产品
 */
@Slf4j
class ConcreteProductA extends Product {

    @Override
    public void getName() {
        log.info("具体产品A");
    }
}

/**
 * 具体工厂
 */
class Factory {
    public ConcreteProductA getConcreteProductA() {
        return new ConcreteProductA();
    }
}