package com.mmwwtt.demo.se.设计模式23种.行为型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 行为_中介者模式 {

    @Test
    @DisplayName("测试中介者模式")
    public void test(){

    }

    class A {

        private E e;

        public A() {
            e = new E();
        }

        public void getA() {
            log.info("getA");
        }

        public void getB() {
            e.getB();
        }

        public void getC() {
            e.getC();
        }

        public void getD() {
            e.getD();
        }
    }

    class B {

        private E e;

        public B() {
            e = new E();
        }

        public void getB() {
            log.info("getB");
        }

        public void getA() {
            e.getA();
        }

        public void getC() {
            e.getC();
        }

        public void getD() {
            e.getD();
        }
    }

    class C {

        private E e;

        public C() {
            e = new E();
        }

        public void getC() {
            log.info("getC");
        }

        public void getA() {
            e.getA();
        }

        public void getB() {
            e.getB();
        }

        public void getD() {
            e.getD();
        }
    }

    class D {

        private E e;

        public D() {
            e = new E();
        }

        public void getD() {
            log.info("getD");
        }

        public void getA() {
            e.getA();
        }

        public void getB() {
            e.getB();
        }

        public void getC() {
            e.getC();
        }
    }
    public class E {

        private A a;
        private B b;
        private C c;
        private D d;

        public E() {
            a = new A();
            b = new B();
            c = new C();
            d = new D();
        }

        public void getA() {
            a.getA();
        }

        public void getB() {
            b.getB();
        }

        public void getC() {
            c.getC();
        }

        public void getD() {
            d.getD();
        }
    }


}
