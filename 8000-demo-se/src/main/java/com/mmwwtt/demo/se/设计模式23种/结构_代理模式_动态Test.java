package com.mmwwtt.demo.se.设计模式23种;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 通过访问代理对象,限制被被代理对象的访问
 * 动态代理：运行时菜确定被代理类是什么类型
 */
@Slf4j
public class 结构_代理模式_动态Test {

    @Test
    @DisplayName("测试动态代理模式")
    public void test() {
        UserDao userDao = new UserDaoImpl();
        UserDao proxy = (UserDao) Proxy.newProxyInstance(
                UserDao.class.getClassLoader(),
                new Class[]{UserDao.class},
                new DynamicProxyHandler(userDao));
        proxy.save();
    }

    interface UserDao {
        void save();
    }

    class UserDaoImpl implements UserDao {
        public void save() {
            log.info("保存操作");
        }
    }

    @AllArgsConstructor
    class DynamicProxyHandler implements InvocationHandler {
        private Object target;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Before method: " + method.getName());
            Object result = method.invoke(target, args);
            System.out.println("After method: " + method.getName());
            return result;
        }
    }
}
