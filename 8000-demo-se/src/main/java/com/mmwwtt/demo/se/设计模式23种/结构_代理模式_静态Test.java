package com.mmwwtt.demo.se.设计模式23种;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 通过访问代理对象,限制被被代理对象的访问
 * 静态代理：代理和被代理对象在运行前就确定了下来
 */
@Slf4j
public class 结构_代理模式_静态Test {

    @Test
    @DisplayName("测试静态代理模式")
    public void test() {
        UserDaoProxy userDaoProxy = new UserDaoProxy();
        userDaoProxy.save();
    }

    class UserDao {

        public void save() {
            log.info("保存操作");
        }
    }

    @NoArgsConstructor
    class UserDaoProxy {

        UserDao userDao = new UserDao();

        public void save() {
            log.info("额外操作");
            userDao.save();
            log.info("额外操作");
        }
    }
}