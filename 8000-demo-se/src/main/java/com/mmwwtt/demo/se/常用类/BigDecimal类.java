package com.mmwwtt.demo.se.常用类;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * double存在精度损失，当用到精确计算时，需要用到BigDecimal类
 */
@Slf4j
public class BigDecimal类 {

    @Test
    public void BigDecimal使用() {
        //浮点型时，会先转成double，已经丢失了精度，需要适用字符串赋值
        BigDecimal num = new BigDecimal(123.123);
        log.info("{}",num);

        BigDecimal num1 = new BigDecimal("123.123");
        BigDecimal num2 = new BigDecimal("12345.12345678910");
        //加  scale(保留位数， 四舍五入类型）
        BigDecimal num3 = num1.add(num2).setScale(2, RoundingMode.HALF_UP);
        //减
        BigDecimal num4 = num1.subtract(num2);
        //乘
        BigDecimal num5 = num1.multiply(num2);

        //获得值
        log.info("{}",num5.doubleValue());
    }
}
