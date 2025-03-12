package com.mmwwtt.demo.se.设计模式.设计模式23种.结构型.代理模式.静态代理;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
class UserDao implements IUserDao {

    @Override
    public void save() {
        log.info("保存操作");
    }
}

@AllArgsConstructor
@Slf4j
class UserDaoProxy implements IUserDao {
    IUserDao userDao;

    @Override
    public void save() {
        log.info("额外操作");
        userDao.save();
        log.info("额外操作");
    }
}