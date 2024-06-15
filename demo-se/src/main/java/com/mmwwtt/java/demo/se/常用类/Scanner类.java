package com.mmwwtt.java.demo.se.常用类;

import java.util.Scanner;

public class Scanner类 {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = "";

        //输入一行字符串(包括空格)
        str = scanner.nextLine();
        System.out.println(str);

        //输入一行字符串，直到空格结束
        str = scanner.next();
        System.out.println(str);

        //输入一个数值
        //如果使用next()/nextInt()/nextDouble()等后还要使用nextLine()读取下一行，则需要先使用nextLine()将当前行的换行读取掉
        int num = scanner.nextInt();

        //判断是否有下一个输入
        while(scanner.hasNext()) {

        }
    }
}
