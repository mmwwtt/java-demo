package com.mmwwtt.java.demo.se.设计模式.SOLID五大设计原则.单一职责原则.反例;

/**
 * 用户类中包含了用户操作和订单操作
 * 职责不够单一
 */
public class UserInfo1 {

    //用户相关操作
    public void getUser() {

    }

    //订单相关操作
    public void getOrder() {

    }

}
