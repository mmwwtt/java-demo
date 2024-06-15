package com.mmwwtt.java.demo.se.设计模式.设计模式23种.结构型.适配器模式;

public class 适配器模式 {
    public static void main(String[] args) {
        Power220V power220V = new Power220V();
        power220V.usePower5V();
    }
}

class Power5V {
    public void usePower5V() {
        System.out.println("正在使用5V的电压");
    }
}

class Power220V extends Power5V {
    public void usePower220V() {
        System.out.println("220v转5v中");
        super.usePower5V();
    }
}