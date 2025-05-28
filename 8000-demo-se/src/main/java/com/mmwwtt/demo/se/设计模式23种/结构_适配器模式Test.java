package com.mmwwtt.demo.se.设计模式23种;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 结构_适配器模式Test {
    @Test
    @DisplayName("测试适配器模式")
    public void test1() {
        Power220V power220V = new Power220V();
        power220V.usePower5V();
    }

    class Power5V {
        public void usePower5V() {
            log.info("正在使用5V的电压");
        }
    }

    class Power220V extends Power5V {
        public void usePower220V() {
            log.info("220v转5v中");
            super.usePower5V();
        }
    }
}