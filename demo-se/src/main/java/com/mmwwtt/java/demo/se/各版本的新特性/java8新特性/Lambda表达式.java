package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Lambda表达式 {

    /**
     * () -> {} 形式的函数
     * 更加灵活，无需进行函数声明
     */
    @Test
    public void Lambda表达式() {
        List<Integer> list = getList();
        Collections.sort(list, (s1,s2) -> s1 - s2);
        assertNotNull(list);
    }

    public List<Integer>  getList() {
        List<Integer> list = Arrays.asList(3,6,4,10,88,22);
        return list;
    }
}
