package com.mmwwtt.demo.se.reflect;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ReflectTest {

    @Test
    public void reflectDemo() throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        //反射生成类
        Class<?> clazz = Class.forName("com.mmwwtt.demo.common.entity.BaseInfo");
        //反射生成对象
        Object object = clazz.getConstructor().newInstance();
        //获取类中的方法
        Method method = clazz.getMethod("getName");
        //执行方法
        method.invoke(object);
        //获取属性列表
        Field[] fields = clazz.getFields();

        //获取私有属性并修改
        Field field = clazz.getDeclaredField("name");
        //修改私有字段时需要酱accessible设置为true
        field.setAccessible(true);
        field.set(object, "小明");
        log.info("\n");
    }

    /**
     * 利用反射将Integer类型插入String数组中，原本不允许这么做，反射绕过了机制，干坏事
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
