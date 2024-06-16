package com.mmwwtt.demo.se.基础;

import java.util.Collections;
import java.util.List;

public class 常量 {

    public static final String href = "www.baidu.com";

    //禁止将可变对象定义为常量，会造成功能异常
    //如下list虽然用 public static final修饰，但是对象中的内容还是可以改变的，是不推荐的
    //public static final List<String> list = new ArrayList<>();

    //保证list作为常量不可变的写法
    public static final List<String> list1 = Collections.emptyList();
    public static final List<String> list2 = List.of("hello", "world");

    //禁止将可变对象定义为常量，会造成功能异常
}
