package com.mmwwtt.demo.se.面向对象;


import com.mmwwtt.demo.common.entity.animal.Animal;
import com.mmwwtt.demo.common.entity.animal.Dog;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
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
        log.info(dog.getName());
        log.info(dog.getSort());
    }
}
