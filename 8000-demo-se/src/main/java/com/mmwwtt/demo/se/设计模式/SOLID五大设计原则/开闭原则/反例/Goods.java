package com.mmwwtt.demo.se.设计模式.SOLID五大设计原则.开闭原则.反例;

import java.util.Date;

/**
 * 直接在原有代码上进行改动, 增加一个入参
 * 违背了开闭原则
 */
public class Goods {
/*
    // 改动前
    public void getPrice() {

    }

*/

    //改动后
    // 获得商品价格
    public void getPrice(Date date) {

    }
}
