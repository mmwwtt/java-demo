package com.mmwwtt.demo.se.常用类;

import org.junit.jupiter.api.Test;

public class Math类 {

    @Test
    public void Math类常用方法() {
        //取最大值
        Math.max(1,2);

        //取最小值
        Math.min(1,2);

        //四舍五入
        Math.round(4.45);

        //向上取整
        Math.ceil(4.45);

        //向下取整
        Math.floor(4.45);

        //取绝对值
        Math.abs(-4.45);

        //取平方根
        Math.sqrt(5);

        //取立方根
        Math.cbrt(5);

        //计算a的b次方
        Math.pow(2,8);
    }


    @Test
    public void 防止溢出的add方法() {
        try {
            // Math.addExact() 当计算溢出时会抛出异常
            Math.addExact(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }

    }
}
