package com.mmwwtt.demo.se.分支语句;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
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
                log.info("b");
            case "c":
                log.info("c");
            case "d":
                log.info("d");
                break;
            default:
                log.info("default");
                break;
        }
    }
}
