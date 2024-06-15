package com.mmwwtt.java.demo.se.基础;


import org.junit.jupiter.api.Test;

public class 数据预校验 {

    @Test
    public void 引用类型不能为null() {
        String str = null;
        if (str != null && !str.equals("")) {
            System.out.println("通过");
        }
    }

    @Test
    public void 除数不能为0() {
        int a = 0;
        if (a != 0) {
            System.out.println("通过");
        }
    }
}
