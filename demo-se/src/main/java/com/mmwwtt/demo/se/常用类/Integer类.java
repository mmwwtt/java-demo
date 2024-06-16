package com.mmwwtt.demo.se.常用类;


import org.junit.jupiter.api.Test;

public class Integer类 {

    @Test
    public void Integer类基本方法() {
        Integer num1 = Integer.valueOf(123);
        Integer num2 = Integer.valueOf("123");

        //Integer 转为 String
        String str = num1.toString();

        //Integer 比较值相等
        System.out.println(num1.equals(1));

        //字符串转为16进制
        int num = Integer.parseInt("FF", 16);
        System.out.println(num);
    }
}
