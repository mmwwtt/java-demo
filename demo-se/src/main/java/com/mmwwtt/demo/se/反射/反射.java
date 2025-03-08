package com.mmwwtt.demo.se.反射;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
@Slf4j
public class 反射 {

    @Test
    public void 反射生成类() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        //反射生成类
        Class clazz = Class.forName("com.mmwwtt.demo.se.反射.Demo");
        //反射生成对象
        Object object = clazz.getConstructor().newInstance();
        //获取类中的方法
        Method method = clazz.getMethod("getAge");
        //执行方法
        method.invoke(object);
        //获取属性列表
        Field[] fields = clazz.getFields();

        //获取私有属性并修改
        Field field = clazz.getDeclaredField("age");
        //可以通过setAccessible(true) 修改默认值，如此会屏蔽Java语言的（运行时）访问检查，使得对象的私有成员可以访问，而不报错
        field.setAccessible(true);
        field.set(object, 19);
        log.info("\n");
    }
}
