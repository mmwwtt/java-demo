package com.mmwwtt.java.demo.se.设计模式.SOLID五大设计原则.单一职责原则.反例;

/**
 * 虽然都是和用户相关的操作，但是业务功能越来越复杂，用户类也越来越庞大，职责也就显得不够单一了
 * 里面的职责可以继续细化分割，
 */
public class UserInfo2 {

    //用户相关操作
    public void getUser() {

    }

    //登入相关操作
    public void login() {

    }

    //身份认证相关操作
    public void verify() {

    }
}
