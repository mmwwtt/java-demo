package com.mmwwtt.demo.se.设计模式23种;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 外观/门面模式：为子系统中的一组接口提供一个统一的入口
 *
 */
public class 结构_外观模式Test {
    @Test
    @DisplayName("测试外观模式")
    public void test() {
        Facade facade = new Facade();
        facade.fun();
    }

    /**
     * 外观角色
     */
    class Facade {
        public A a;
        public B b;
        public C c;
        public Facade() {
            a = new A();
            b = new B();
            c = new C();
        }
        public void fun() {
            a.fun();
            b.fun();
            c.fun();
        }
    }
    class A {
        public void fun() {

        }
    }

    class B {
        public void fun() {

        }
    }

    class C {
        public void fun() {

        }
    }
}
