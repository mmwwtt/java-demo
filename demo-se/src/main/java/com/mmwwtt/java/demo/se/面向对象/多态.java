package com.mmwwtt.java.demo.se.面向对象;


import com.mmwwtt.java.demo.se.公共类.A;
import com.mmwwtt.java.demo.se.公共类.B;
import org.junit.jupiter.api.Test;

public class 多态 {

    /**
     * 编译看左，运行看右
     * 多态是方法的多态，和成员变量没有关系
     * 方法会执行左边子类的方法，成员变量依旧是右边类的成员变量
     */
    @Test
    public void 多态案例() {
        A a = new B();
        System.out.println(a.getMessage());
        System.out.println(a.message);
    }
}
