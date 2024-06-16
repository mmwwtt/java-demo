package com.mmwwtt.demo.se.设计模式.设计模式23种.结构型.代理模式.静态代理;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

public class 静态代理 {

    @Test
    public void test() {
        IUserDao userDao = new UserDao();
        UserDaoProxy userDaoProxy = new UserDaoProxy(userDao);
        userDaoProxy.save();
    }
}

interface IUserDao {
    void save();
}

class UserDao implements IUserDao {

    @Override
    public void save() {
        System.out.println("保存操作");
    }
}

@AllArgsConstructor
class UserDaoProxy implements IUserDao {
    IUserDao userDao;

    @Override
    public void save() {
        System.out.println("额外操作");
        userDao.save();
        System.out.println("额外操作");
    }
}