package com.mmwwtt.demo.se.设计模式23种;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *将对象缓存在内存中，减少创建对象的数量
 * Integer的享元对象(-128-127)在JVM启动时就提前创建好了
 * 享元模式为了对象复用，减少重复对象创建
 * 单例模式为了限制对象个数，而不是复用
 */
public class 结构_享元模式Test {

    @Test
    @DisplayName("测试享元模式")
    public void test1() {
        Integer a = 1;   //会使用享元模式
        //Integer b = new Integer(1);  //不会使用享元模式，已经过时
        Integer c = Integer.valueOf(1);  //会使用享元模式

    }
}
