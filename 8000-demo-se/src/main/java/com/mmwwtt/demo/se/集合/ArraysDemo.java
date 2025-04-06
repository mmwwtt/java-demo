package com.mmwwtt.demo.se.集合;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ArraysDemo {

    @Test
    public void test() {
        Integer[] array0 = {1, 2, 3};
        int[] array1 = {1, 3, 5};
        //array转list
        List<Integer> list2 = Arrays.asList(array0);


        //将数组排序(默认升序，用于基本数据类型)
        Arrays.sort(array1);

        //自定义数组排序(引用类型)
        Arrays.sort(array0, (num1, num2) -> {
            /**
             * 返回正数，第一个元素在后面；
             * 返回0,不交换
             * 返回负数，第一个元素在前面；
             */
            return num1.intValue() > num2.intValue() ? 1 : -1;
        });
        //对数组进行填充
        Arrays.fill(array1, 16);

        //对数组进行填充,[起始索引，结束索引)
        Arrays.fill(array1, 1, 2, 77);


    }
}
