package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class 函数式接口 {

    /**
     * 只有一个抽象方法的接口，可以直接使用lambda表达式来实现
     */
    @Test
    public void 使用() {
        List<Integer> list = new ArrayList<>();
        Collections.sort(list, (Integer a, Integer b) -> {
            /**
             * 返回正数，第一个元素在后面；
             * 返回0,不交换
             * 返回负数，第一个元素在前面；
             */
            return a.intValue() > b.intValue() ? -1 : 1;
        });
    }
}
