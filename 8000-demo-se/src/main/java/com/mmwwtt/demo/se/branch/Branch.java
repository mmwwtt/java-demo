package com.mmwwtt.demo.se.branch;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class Branch {


    /**
     * 布尔类型命名建议 isXX
     */
    @Test
    public void ifDemo() {
        boolean isBig = false;
        if (isBig) {
            return;
        }
    }

    /**
     * 括号种变量类型为String时，需要确保不为空
     * switch语句要有default分支，除非添加变量是枚举类型
     */
    @Test
    public void switchDemo() {
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


    /**
     * continue : 跳出当层循环
     * break    ：跳出当前循环
     * 浮点型不做循环遍历，浮点型表示为 1.0E10 由于精度问题，1.0E10+10表示为 1.0E10,无法作为循环判断
     */
    @Test
    public void forDemo() {
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                continue;
            }
            if (i==8) {
                break;
            }
            log.info("{}",i);
        }
    }

    /**
     * continue A: 跳出标签的当层循环
     * break    A：跳出标签的循环
     */
    @Test
    public void label标签跳出多层循环() {
        A: for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 2) {
                    continue A;
                }
                if (i == 4) {
                    break A;
                }
            }
            log.info("{}",i);
        }
    }

    @Test
    public void 不要使用浮点型作为循环变量() {
        float num1 = 1.0f;
        float num2 = num1 + 10;
        for (double i = num1; i < num2; i++) {
            log.info("{}",i);
        }

        //当浮点型到一定长度后表示为 1.0E10 由于精度问题，1.0E10 + 10也会被表示为 1.0E10,无法作为循环判断
        float num3 = 10000000000.0f;
        float num4 = num3 + 10;
        for (double i = num3; i < num4; i++) {
            log.info("{}",i);
        }
    }
}
