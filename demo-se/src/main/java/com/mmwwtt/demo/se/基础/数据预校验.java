package com.mmwwtt.demo.se.基础;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 数据预校验 {

    @Test
    public void 引用类型不能为null() {
        String str = null;
        if (str != null && !str.equals("")) {
            log.info("通过");
        }
    }

    @Test
    public void 除数不能为0() {
        int a = 0;
        if (a != 0) {
            log.info("通过");
        }
    }
}
