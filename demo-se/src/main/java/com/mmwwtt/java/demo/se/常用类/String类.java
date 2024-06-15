package com.mmwwtt.java.demo.se.常用类;


import org.junit.jupiter.api.Test;

import java.util.Locale;

public class String类 {

    @Test
    public void 格式化字符串() {
        double num1 = 3.1415916;
        int num2 = 1;
        //格式化字符串
        String str1 = String.format("%f", num1);

        //保留一位小数
        String str2 = String.format("%.1f", num1);

        //前位补0
        String str3 = String.format("%03d", num2);

        System.out.println(str1);
        System.out.println(str2);
        System.out.println(str3);
    }


    @Test
    public void string类常用方法() {
        String str1 = "hello";
        String str2 = "world";

        //判断字符串是否相等,返回boolean
        str1.equals(str2);

        //返回指定索引下的字符，return char
        str1.charAt(0);

        //返回字符串中存在字串的索引，无则返回-1
        str1.indexOf("ll");

        //根据指定 字符串进行分割，'.'需要转为'\\.', '\'需要转为'\\\\'
        String[] words = str1.split(" ");

        // 用|隔开，可以按照多个条件进行分割
        String[] words1 = str1.split(" |l");

        //去除字符串头尾空格后，返回新串
        str1.trim();

        //截取字符串后，返回新串。若只有一个参数则表示起始到末尾。两个参数表示【起始-结束)位置
        str1.substring(0);

        //返回字符串长度
        str1.length();

        //将字符串转为小写后，返回新串
        //考虑到地区语言上存在差异，大小写转换时，必须加上Locale.ROOT/Locale.ENGLISH
        str1.toLowerCase(Locale.ROOT);

        //将字符串转为大写后，返回新船
        str1.toUpperCase(Locale.ROOT);

        //将字符串中的某个字符替换为新的字符后返回   字符串替换
        str1.replace(',','&');
        str1.replace(" "," ");
        //将字符串中的某个字符串替换为新的字符串后返回   正则替换
        str1.replaceAll("aa","bb");
        //将首个符合条件的字符串替换为新串
        str1.replaceFirst("a","b");

        //将String转为int
        Integer.parseInt(str1);

        //equals方法的参数可用为null
        str1.equals(null);

        //判断字符串是否以指定字符串开头
        str1.startsWith("hello");

        //字符串字典序比较
        str1.compareTo(str2);
    }
}
