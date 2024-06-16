package com.mmwwtt.demo.se.jdk.features;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalDemoTest {

    /**
     * Optional类：避免大量的是否为null判断，代码更加优雅
     * Optional.empty():创建空的Optional对象
     * Optional.of(): 创建非空的Optional对象
     * Optional.ofNullable: 创建可能为空的Optional对象
     */
    @Test
    public void test() {
        Optional<String> empty = Optional.empty();
        System.out.println(empty);
        assertEquals(false, empty.isPresent());


        Optional<String> notNull = Optional.of("hello");
        System.out.println(notNull);
        assertEquals(true, notNull.isPresent());

        Optional<String> ableNull = Optional.ofNullable(null);
        System.out.println(ableNull);
        assertEquals(false, ableNull.isPresent());


    }

}
