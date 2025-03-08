package com.mmwwtt.demo.se.集合;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Arrays类 {

    @Test
    public void test() {

        //用Arrays生成list
        List<Integer> list1 = Arrays.asList(new Integer[]{1, 2, 3});

        //array转list
        Integer[] array0 = new Integer[]{1, 2, 3};
        List<Integer> list2 = Arrays.asList(array0);


        //将数组排序(默认升序，用于基本数据类型)
        int[] array1 = {1, 3, 5};
        Arrays.sort(array1);

        //自定义数组排序(引用类型)
        Integer[] array2 = new Integer[]{1, 5, -10};
        Arrays.sort(array2, (num1, num2) -> {
            /**
             * 返回正数，第一个元素在后面；
             * 返回0,不交换
             * 返回负数，第一个元素在前面；
             */
            return num1.intValue() > num2.intValue() ? 1 : -1;
        });
        log.info("{}",array2);


        //对数组进行填充
        Arrays.fill(array1, 16);

        //对数组进行填充,[起始索引，结束索引)
        Arrays.fill(array1, 1, 2, 77);


    }
}
