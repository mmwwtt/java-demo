package com.mmwwtt.demo.se.基础;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 基本数据类型和包装类型 {

    @Test
    public void 定义变量并赋值() {
        //整形默认int类型
        byte b = 1; //byte范围 -128到127
        short s = 1;
        int i = 1;

        //不推荐 long a = 1l 因为小写的L和1容易混淆
        long a = 1L;

        //浮点型默认double类型
        float f = 1F;
        double d1 = 1D;
        double d2 = 1;

    }

    @Test
    public void 类型转换() {
        float num1 = 1.0f;
        double num2 = 1.0;

        //大类型转小类型要显示声明
        float num3 = (float) num2;

        //小类型转大类型可以隐式自动转换，少的长度部分自动补0
        double num4 = num1;

        //类型大小不一致，相互转换可能存在意外截断，需要避免隐式类型转换
        //如： int，long转为float, long转为double等
        int a = 1799999999;
        float b = 1.0f;
        float c = a * b;
        double d = (double)a * b;
        log.info("{}",c);   //1.8E9
        log.info("{}",d);   //1.799999999E9 正确

        int a2 = 1000000000;
        float b2 = a2 + 1.0f;          //1.0E9
        double c2 = (double)a2 + 1.0f; //1.000000001E9 错误
        log.info("{}",b2);
        log.info("{}",c2);
    }

    /**
     * 通过 Integer num = 1
     * 或者 Integer.valueof(1)
     * 创建对象时， -128到127会被缓存在Integer内存中，相同值的对象都是同一个
     */
    @Test
    public void Integer类型的缓存池() {
        Integer num1 = 100;
        Integer num2 = 100;
        Integer num3 = 200;
        Integer num4 = 200;
        log.info("{}",num1 == num2);
        log.info("{}",num3 == num4);
    }


    /**
     * 基本数据类型在性能上要优于包装类型
     * 基本数据类型和包装类型会在比较，运算，赋值时进行自动转换
     * 自动拆装箱会存在性能消耗，要减少不必要的拆装箱情况
     */
    @Test
    public void 自动拆装箱() {
        // 不推荐，存在自动拆装箱，会有性能消耗
        Integer num2 = 100;

        // 不推荐，已经过时
        //Integer num1 = new Integer(100);

        // 推荐写法，-128到127会取内存值，效率更高
        Integer num3 = Integer.valueOf(100);

        int num4 = 100;

        //比较时拆装箱
        log.info("{}",num3 == num4);
        //运算时拆装箱
        log.info("{}",num3 + num4);
    }

    public void 数据类型比较大小() {
        int num1 = 1;
        int num2 = 2;
        //基本数据类型通过 == 判断值是否相等
        log.info("{}",num1 == num2);

        Integer num3 = Integer.valueOf(1);
        Integer num4 = Integer.valueOf(2);
        //包装类型通过equals()方法判断值是否相等
        log.info("{}",num3.equals(num4));
    }

    @Test
    public void 浮点型比较() {

        // 小数表示二进制除不尽，所以存在误差，要改为用BigDecimal类型做精确计算
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        log.info("{}",a == b);

        //浮点基本数据类型 不能用 == 判断
        float num1 = 0.3F;
        double num2 = 0.3D;
        double num3 = 0.3D;
        log.info("{}",num2 == num1);
        log.info("{}",num2 == num3);

        //浮点包装类型不能用 equals判断   但是能用compareTo比较大小
        Float num4 = Float.valueOf(0.3f);
        Double num5 = Double.valueOf(0.3);
        Double num6 = Double.valueOf(0.3);
        log.info("{}",num5.equals(num4));
        log.info("{}",num5.equals(num6));
        log.info("{}",num5.compareTo(num6));

        //非要使用，则在误差范围内都相等
        if ((num1 - num2) < 1e-6f) {
            log.info("num1 == num2");
        }

    }

    /**
     * 禁止和NaN 做 < = > != 等判断，
     * 需要用Float.isNaN()/Double.isNaN()来判断
     */
    @Test
    public void 禁止和NaN比较() {
        double res = 1/0D;  //结果为NaN
        if (res == Float.NaN) {
            log.info("任何与NaN做 !=以外 的比较， 都返回flase");
        }
        if (res != Float.NaN) {
            log.info("任何与NaN做 != 比较， 都返回true");
        }

        //正确做法  判断一个对象是否为NaN
        if(Double.isNaN(res)) {

        }
    }
}
