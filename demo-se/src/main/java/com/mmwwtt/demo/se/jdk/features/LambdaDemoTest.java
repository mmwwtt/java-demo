package com.mmwwtt.demo.se.jdk.features;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LambdaDemoTest {

    /**
     * () -> {} 形式的函数
     * 更加灵活，无需进行函数声明
     */
    @Test
    public void Lambda表达式() {
        List<Integer> list = Arrays.asList(3,6,4,10,88,22);
        list.sort((s1, s2) -> s1 - s2);
        assertNotNull(list);
    }
}
