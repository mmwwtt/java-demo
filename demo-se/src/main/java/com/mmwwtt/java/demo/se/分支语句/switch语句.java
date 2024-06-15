package com.mmwwtt.java.demo.se.分支语句;


import org.junit.jupiter.api.Test;

public class switch语句 {


    /**
     * 括号种变量类型为String时，需要确保不为空
     * switch语句要有default分支，除非添加变量是枚举类型
     */
    @Test
    public void switch使用() {
        String str = "a";
        switch (str) {
            case "a":
            case "b":
                System.out.println("b");
            case "c":
                System.out.println("c");
            case "d":
                System.out.println("d");
                break;
            default:
                System.out.println("default");
                break;
        }
    }
}
