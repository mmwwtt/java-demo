package com.mmwwtt.java.demo.se.基础;

import com.mmwwtt.java.demo.se.common.BaseInfo;
import com.mmwwtt.java.demo.se.common.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class 反射 {

    @Test
    public void 反射基本方法() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {

        //Class.forName() 启动类加载器，根据类的全限定类名 完成类的加载和链接，会执行类的静态代码块
        //newInstance() 会创建类的对象(只能调用无参构造函数)(过时)
        //用 getDeclaredConstructor().newInstance() 替代
        User user1 = (User) Class.forName("com.mmwwtt.java.demo.se.common.User").newInstance();
        User user2 = (User) Class.forName("com.mmwwtt.java.demo.se.common.User").getDeclaredConstructor().newInstance();
    }

    @Test
    public void 反射修改对象属性() {
        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setName("小明");
        baseInfo.setBaseInfoId(18L);
        Class entityClass = BaseInfo.class;
        //获取public字段
        Field[] fields1 = entityClass.getFields();

        //获取全部字段
        Field[] fields = entityClass.getDeclaredFields();
        Arrays.stream(fields).forEach(item -> {
            if (item.getName().equals("name")){
                try {
                    //修改私有字段时需要酱accessible设置为true
                    item.setAccessible(true);
                    item.set(baseInfo, "小华");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return;
    }
}
