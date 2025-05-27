package com.mmwwtt.demo.se.设计模式23种.结构型;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 结构_代理模式_静态 {

    @Test
    @DisplayName("测试静态代理模式")
    public void test() {
        IUserDao userDao = new UserDao();
        UserDaoProxy userDaoProxy = new UserDaoProxy(userDao);
        userDaoProxy.save();
    }


    interface IUserDao {
        void save();
    }

    class UserDao implements IUserDao {

        @Override
        public void save() {
            log.info("保存操作");
        }
    }

    @AllArgsConstructor
    class UserDaoProxy implements IUserDao {
        IUserDao userDao;

        @Override
        public void save() {
            log.info("额外操作");
            userDao.save();
            log.info("额外操作");
        }
    }
}