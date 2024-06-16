package com.mmwwtt.demo.se.常用类;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * double存在精度损失，当用到精确计算时，需要用到BigDecimal类
 */
public class BigDecimal类 {

    @Test
    public void BigDecimal使用() {
        BigDecimal num1 = new BigDecimal(123.123);
        BigDecimal num2 = new BigDecimal(12345.12345678910);

        //加
        BigDecimal num3 = num1.add(num2);
        //减
        BigDecimal num4 = num1.subtract(num2);
        //乘
        BigDecimal num5 = num1.multiply(num2);

        //获得值
        System.out.println(num5.doubleValue());
    }
}
