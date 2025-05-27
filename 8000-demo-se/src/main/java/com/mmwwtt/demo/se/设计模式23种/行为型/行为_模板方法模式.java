package com.mmwwtt.demo.se.设计模式23种.行为型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 模板方法
 * 优：将公共的代码放到父类中，实现复用
 * 父类中进行方法申明，子类中实现详细细节
 */
@Slf4j
public class 行为_模板方法模式 {

    @Test
    @DisplayName("测试模板方法模式")
    public void test() {
        A b = new B();
        A c = new C();
        b.sayHello();
        c.sayHello();

        b.sayName();
        c.sayName();
    }

    abstract class A {

        /**
         * 模板方法
         */
        public void sayHello() {
            log.info("hello");
        }

        /**
         * 非模板方法
         */
        public abstract void sayName();
    }

    class B extends A {


        @Override
        public void sayName() {
            log.info("B");
        }
    }

    class C extends A {

        @Override
        public void sayName() {
            log.info("A");
        }
    }
}