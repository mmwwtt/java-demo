package com.mmwwtt.java.demo.se.分支语句;


import org.junit.jupiter.api.Test;

public class if表达式 {


    /**
     * 布尔类型命名建议 isXX
     * 不建议 if (isXX == true)
     * 建议改为 (isBig) 更加简洁
     *
     * 为避免遗漏场景，建议多个 if-else分支 最后要有 else分支， 对遗漏的场景抛出异常/记录在日志
     */
    @Test
    public void if使用() throws Exception {
        int a = 1;
        if (a == 1) {

        } else if (a == 2) {

        } else {
            throw new Exception();
        }

        boolean isBig = false;
        if (isBig) {

        }
    }

    @Test
    public void 卫语句() {
        boolean flag = false;
        if (!flag) {
            return;
        }
        System.out.println("hello");
    }
}
