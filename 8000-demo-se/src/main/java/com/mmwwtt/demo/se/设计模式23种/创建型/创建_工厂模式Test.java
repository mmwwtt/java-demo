package com.mmwwtt.demo.se.设计模式23种.创建型;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * 抽象工厂、具体工厂、抽象产品、具体产品
 * 给工厂传入类型名(无需指定具体类型)，生成对应的子类对象
 */
@Slf4j
public class 创建_工厂模式Test {

    @Test
    @DisplayName("测试工程方法模式")
    public void test() {
        Factory factory = new FactoryImpl();
        Product product = factory.getProduct("ProductA");
        product.getName();
    }

    /**
     * 抽象产品
     */
    interface Product {
        void getName();
    }

    /**
     * 具体产品
     */
    class ProductA implements Product {

        @Override
        public void getName() {
            log.info("具体产品A");
        }
    }

    /**
     * 具体产品
     */
    class ProductB implements Product {

        @Override
        public void getName() {
            log.info("具体产品B");
        }
    }

    /**
     * 抽象工厂
     */
    interface Factory {
        Product getProduct(String productType);
    }

    /**
     * 具体工厂
     */
    class FactoryImpl implements Factory {
        @Override
        public Product getProduct(String productType) {
            if (Objects.equals(productType, "ProductA")) {
                return new ProductA();
            } else if (Objects.equals(productType, "ProductB")) {
                return new ProductB();
            } else {
                return null;
            }
        }
    }
}
