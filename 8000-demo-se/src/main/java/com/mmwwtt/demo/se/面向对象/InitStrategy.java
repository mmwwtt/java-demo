package com.mmwwtt.demo.se.面向对象;

/**
 * 初始化策略接口
 */
public interface InitStrategy {

    /**
     * 默认实现方法
     */
    default void init() {
        System.out.println("InitStrategy中的默认实现方法");
    }
}
