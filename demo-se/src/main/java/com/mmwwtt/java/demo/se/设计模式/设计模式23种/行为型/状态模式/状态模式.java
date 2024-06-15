package com.mmwwtt.java.demo.se.设计模式.设计模式23种.行为型.状态模式;

import lombok.Data;

public class 状态模式 {
    public static void main(String[] args) {
        Account account = new Account();

        account.setState(new NormalState());
        account.say();

        account.setState(new BadState());
        account.say();
    }
}

@Data
class Account {
    State state;
    public void say() {
        state.say();
    }
}
/**
 * 抽象状态类
 */
abstract class State {
    Account account;
    public abstract void say();
}

/**
 * 具体状态类：正常状态
 */
@Data
class NormalState extends State{

    @Override
    public void say() {
        System.out.println("钱很充足");
    }

}

/**
 * 具体状态类：坏状态
 */
@Data
class BadState extends State {

    @Override
    public void say() {
        System.out.println("钱不够了");
    }

}