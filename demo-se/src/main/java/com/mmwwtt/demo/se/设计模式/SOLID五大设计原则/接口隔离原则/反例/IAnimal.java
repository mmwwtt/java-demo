package com.mmwwtt.demo.se.设计模式.SOLID五大设计原则.接口隔离原则.反例;

/**
 * 并不是每一种动物都具备这三种类型的行为
 * 鱼只需要 游相关的接口即可，其他根本用不着，但是也要被迫去实现
 * 违背了接口隔离原则
 */
public interface IAnimal {

    //跑相关的方法
    void run();

    //飞相关的方法
    void fly();

    //游相关的方法
    void swim();
}
