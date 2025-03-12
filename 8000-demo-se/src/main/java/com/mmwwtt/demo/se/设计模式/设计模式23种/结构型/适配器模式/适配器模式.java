package com.mmwwtt.demo.se.设计模式.设计模式23种.结构型.适配器模式;

import lombok.extern.slf4j.Slf4j;

public class 适配器模式 {
    public static void main(String[] args) {
        Power220V power220V = new Power220V();
        power220V.usePower5V();
    }
}

@Slf4j
class Power5V {
    public void usePower5V() {
        log.info("正在使用5V的电压");
    }
}

@Slf4j
class Power220V extends Power5V {
    public void usePower220V() {
        log.info("220v转5v中");
        super.usePower5V();
    }
}