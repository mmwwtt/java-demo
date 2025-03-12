package com.mmwwtt.demo.se.自定义;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum 枚举类型写法 {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    枚举类型写法(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        log.info("{}",枚举类型写法.ONE);
        log.info("{}",枚举类型写法.ONE.getValue());
    }
}
