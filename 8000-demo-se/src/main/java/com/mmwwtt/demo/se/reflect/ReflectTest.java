package com.mmwwtt.demo.se.reflect;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
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

    @Test
    @Disabled("反射-Class类测试")
    public void testClass() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Integer num = strTo( Integer.class,"1");
    }

    //根据 class对象 进行数据处理
    private <T> T strTo(Class<T> clazz, String str) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> res = new ArrayList<>();

        //判断当前类对象是什么并处理
        if (clazz == Integer.class) {
            //将对象强转为对应的泛型类 ， 不同类型转换会报错，所以需要Integer.parseInt解析字符串
             T tmp =  clazz.cast(Integer.parseInt(str));
        }

        //把类对象转换成子类的对象
        //Class<? extends Animal> animalClass = clazz.asSubclass(Animal.class);

        //获得类的加载器 可以通过类加载器加载资源
        clazz.getClassLoader();

        //返回类中所有公共类和接口类对象
        Class<?>[] classes = clazz.getClasses();

        //返回类中所有类和接口类对象
        Class<?>[] declaredClasses = clazz.getDeclaredClasses();

        //根据类的全限定路径返回类对象
        Class<?> t = Class.forName("java.lang.Integer");
        String tClassName = t.getName();

        //通过反射创建实例化对象， jdk9已弃用.newInstance, 使用.getDeclaredConstructor().newInstance()
        Object object = t.newInstance();
        object = t.getDeclaredConstructor().newInstance();

        //获得类的包名
        clazz.getPackage();

        //获得类的名字
        clazz.getSimpleName();

        //获得当前类的父类名字
        clazz.getSuperclass();

        //获得当前类实现的类/接口
        clazz.getInterfaces();
        return null;
    }
}
