package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.抽象工厂模式;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

public class 抽象工厂模式 {

    @Test
    public void test() {
        Factory factory = new ConcreteFactoryA();
        Product product = factory.getProduct("productA");
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
class ProductA extends Product{

    @Override
    public void getName() {
        log.info("具体产品A");
    }
}

/**
 * 抽象工厂
 */
abstract class Factory {
    public abstract Product getProduct(String name);
}

class ConcreteFactoryA extends Factory {

    @Override
    public Product getProduct(String name) {
        if (name.equals("productA")) {
            return new ProductA();
        }
        return null;
    }
}