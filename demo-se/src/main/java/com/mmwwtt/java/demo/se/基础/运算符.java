package com.mmwwtt.java.demo.se.基础;


import org.junit.jupiter.api.Test;

public class 运算符 {

    /**
     * 三目运算符的返回值类型为两个条件中长度大的类型一致
     * 建议两个条件中的类型要一致，否则会存在意外的类型转换
     * char两个字节 0-65535
     */
    @Test
    public void 三目运算符() {
        char c = 'A';
        int num = 65;
        boolean flag = true;
        System.out.println(flag ? c : 65535);
        System.out.println(flag ? c : 1000000);
        System.out.println(flag ? c : num);
    }

    @Test
    public void 三目运算符中的两个返回对象不能为null() {
        Integer num1 = null;
        int num2 = 1;
        try {
            System.out.println(true ? num1 : num2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(true ? null : 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
