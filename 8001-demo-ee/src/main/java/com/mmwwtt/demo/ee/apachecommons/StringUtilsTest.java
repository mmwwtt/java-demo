package com.mmwwtt.demo.ee.apachecommons;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class StringUtilsTest {

    @Test
    public void demo() {
        String str = "Hello World";
        String str1 = "";
        String str2 = "   ";

        log.info("字符串空校验");
        //检查字符串是否为null/长度为0/全是空格的字符串
        System.out.println(StringUtils.isBlank(str1));
        System.out.println(StringUtils.isBlank(str2));

        //检查字符串是否为null/长度为0
        System.out.println(StringUtils.isEmpty(str1));
        System.out.println(StringUtils.isEmpty(str2));

        //数组/列表中的字符串拼接
        log.info("字符串拼接");
        List<String> strList = Arrays.asList("Hello", "World", "!");
        String result = StringUtils.join(strList, ",");
        System.out.println("join result: " + result);


        //字符串截取
        log.info("字符串截取");
        System.out.println(StringUtils.left(str, 5));   // 输出：Hello
        System.out.println(StringUtils.right(str, 5)); // 输出：World
        System.out.println(StringUtils.mid(str, 3, 5)); // 输出：loWor


        //字符串大小转换
        log.info("字符串大小写转换");
        System.out.println(StringUtils.upperCase(str)); // 输出：HELLOWORLD
        System.out.println(StringUtils.lowerCase(str)); // 输出：helloworld

        //去除两端空白字符
        log.info("去除两端空白字符");
        String str3 = "  Hello World\n  ";
        //去除两端空格
        System.out.println(StringUtils.trim(str3));
        //去除两端空白字符(包括换行等)
        System.out.println(StringUtils.strip(str3));


        //字符串替换
        log.info("字符串替换");
        System.out.println(StringUtils.replace(str, "World", "Java"));

        //是否存在子串
        log.info("是否存在子串");
        System.out.println(StringUtils.contains(str3, "Hello"));

        //比较字符串是否相等(忽略大小写)
        System.out.println(StringUtils.equalsIgnoreCase("hello", "hello"));
    }

}
