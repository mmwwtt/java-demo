package com.mmwwtt.java.demo.se.面向对象;


import com.mmwwtt.demo.common.entity.Animal;
import com.mmwwtt.demo.common.entity.Dog;
import org.junit.jupiter.api.Test;

public class 多态 {

    /**
     * 编译看左，运行看右
     * 多态是方法的多态，和成员变量没有关系
     * 方法会执行左边子类的方法，成员变量依旧是右边类的成员变量
     * private 修饰不会被子类继承
     */
    @Test
    public void 多态案例() {
        Animal dog = new Dog();
        dog.say();
        System.out.println(dog.getName());
        System.out.println(dog.getSort());
    }
}
