package com.mmwwtt.demo.se.常用类;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class IntegerDemo {

    @Test
    public void Integer类基本方法() {
        Integer num1 = Integer.valueOf(123);
        Integer num2 = Integer.valueOf("123");

        //Integer 转为 String
        String str = num1.toString();

        //Integer 比较值相等
        log.info("{}",num1.equals(1));

        //字符串转为16进制
        int num = Integer.parseInt("FF", 16);
        log.info("{}",num);
    }
}
