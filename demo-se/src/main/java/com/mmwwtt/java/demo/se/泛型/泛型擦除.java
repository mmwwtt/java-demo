package com.mmwwtt.java.demo.se.泛型;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class 泛型擦除 {

    @Test
    public void test() {
        List<Integer> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        assertTrue(list1.getClass() == list2.getClass());
        System.out.println(list1.getClass());

    }

    /**
     * 利用反射将Integer类型插入String数组中，原本不允许这么做，反射绕过了机制，干坏事
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    @Test
    public void 反射利用泛型擦除绕过正常机制() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> list1 = new ArrayList<>();
        list1.add("123");
        Method method = list1.getClass().getMethod("add", Object.class);
        method.invoke(list1, 123);
        assertEquals(list1.size(), 2);
    }
}
