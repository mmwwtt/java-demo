package com.mmwwtt.demo.se.基础;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 基本数据类型和包装类型 {

    //整形默认int类型
    private byte b = 1; //byte范围 -128到127
    private short s = 1;
    private int i = 1;
    private long a = 1L;

    //浮点型默认double类型
    private float f = 1F;
    private double d1 = 1D;
    private double d2 = 1;

    //大类型转小类型要显示声明
    float num3 = (float) d1;

    /**
     * 基本数据类型在性能上要优于包装类型
     */
    @Test
    public void 创建包装类对象() {
        // 不推荐，存在自动拆装箱，会有性能消耗
        Integer num2 = 100;
        // 不推荐，已经过时
        Integer num1 = new Integer(100);
        // 推荐写法，-128到127会取内存值，效率更高
        Integer num3 = Integer.valueOf(100);
        log.info("{},{},{}", num1, num2, num3);
    }

    public void 数据类型比较大小() {
        int num1 = 1;
        int num2 = 2;
        //基本数据类型通过 == 判断值是否相等
        log.info("{}", num1 == num2);

        Integer num3 = Integer.valueOf(1);
        Integer num4 = Integer.valueOf(2);
        //包装类型通过equals()方法判断值是否相等
        log.info("{}", num3.equals(num4));
    }

    @Test
    public void 浮点型比较() {
        // 浮点基本数据类型 不能用 == 判断
        // 小数的二进制除不尽，存在误差，要改为BigDecimal做精确计算
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        log.info("{}", a == b);

        // 浮点包装类型不能用 equals判断   但是能用compareTo比较大小
        Double num5 = Double.valueOf(0.3);
        Double num6 = Double.valueOf(0.3);
        log.info("{}", num5.compareTo(num6));

        // 非要使用，则在误差范围内都相等
        if ((num5 - num6) < 1e-6f) {
            log.info("num5 == num6");
        }
    }
}
