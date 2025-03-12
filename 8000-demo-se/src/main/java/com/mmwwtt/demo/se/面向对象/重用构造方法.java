package com.mmwwtt.demo.se.面向对象;

public class 重用构造方法 {
    Integer x;
    Integer y;
    Integer value;


    public 重用构造方法() {
        this(null);
    }

    public 重用构造方法(Integer x) {
        this(x, null);
    }

    public 重用构造方法(Integer x, Integer y) {
        this(x, y, null);
    }

    public 重用构造方法(Integer x, Integer y, Integer value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}
