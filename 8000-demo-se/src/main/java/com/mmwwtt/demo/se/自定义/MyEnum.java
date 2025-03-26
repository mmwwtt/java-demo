package com.mmwwtt.demo.se.自定义;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义枚举类
 */
@Slf4j
public enum MyEnum {
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

    MyEnum(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        log.info("{}",MyEnum.ONE);
        log.info("{}",MyEnum.ONE.getValue());
    }
}
