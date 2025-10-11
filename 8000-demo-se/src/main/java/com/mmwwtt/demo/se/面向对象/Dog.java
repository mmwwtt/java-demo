package com.mmwwtt.demo.se.面向对象;


public class Dog implements InitStrategy{

    @Override
    public void init() {
        //调用父类接口中的默认方法
        InitStrategy.super.init();
    }
}
