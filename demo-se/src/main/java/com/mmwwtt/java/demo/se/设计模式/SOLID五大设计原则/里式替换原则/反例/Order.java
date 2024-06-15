package com.mmwwtt.java.demo.se.设计模式.SOLID五大设计原则.里式替换原则.反例;

/**
 * 子类重写父类方法，改变了父类的功能
 * 违反了里氏替换原则
 */
public class Order {

    public void getGoodsByPrice() {
        //获取商品列表，根据价格降序排序
    }
}

class FoodOrder {

    public void getGoodsByPrice() {
        //获取商品列表，根据价格升序排序
    }
}
