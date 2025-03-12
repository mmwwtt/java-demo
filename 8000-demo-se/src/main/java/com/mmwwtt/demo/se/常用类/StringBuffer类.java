package com.mmwwtt.demo.se.常用类;


import org.junit.jupiter.api.Test;

public class StringBuffer类 {

    @Test
    public void stringBuffer类常用函数() {
        StringBuffer s1 = new StringBuffer();

        //将不同类型进行字符串拼接
        s1.append("hello");
        s1.append(15);
        s1.append(false);

        //反转字符串
        s1.reverse();

        //将StringBuffer类转为String类，并返回
        s1.toString();

        //匹配字符串，有则返回第一次的索引，无则返回-1
        s1.indexOf("h");

        //返回指定下标下的字符
        s1.charAt(0);

        //返回字符串长度
        s1.length();

        //删除指定下标下的字符串,一个参数表示单个字符，两个参数表示起始和结束位置
        s1.deleteCharAt(0);

        //替换指定索引下的字符串
        s1.setCharAt(0,'a');
    }
}
