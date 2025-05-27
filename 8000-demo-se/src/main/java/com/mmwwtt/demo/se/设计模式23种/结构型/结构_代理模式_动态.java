package com.mmwwtt.demo.se.设计模式23种.结构型;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class 结构_代理模式_动态 {

    @Test
    @DisplayName("测试动态代理模式")
    public void test() {

    }
    interface IUserDao {
        void save();
    }

    @AllArgsConstructor
    class UserDaoProxyFactory {
        private Object object;

        /**
         * 获得代理对象
         * Proxy.newProxyInstance(类加载器，代理类需要实现所有接口，处理类)
         * 类加载器
         *  当前类.class.getClassLoader()
         *  目标实例类.getClass().getClassLoader()
         * 代理类需要实现的所有接口
         *  目标实例类.getClass().getInterface();  只能获得自己的接口，不能获得父类的
         *  new Class[](userService.class)
         * @return
         */
        public Object getProxyInstance() {
            return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            log.info("额外操作");
                            Object returnValue = method.invoke(object, args);
                            log.info("额外操作");
                            return null;
                        }
                    });
        }
    }
}
