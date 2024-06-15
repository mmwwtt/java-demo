package com.mmwwtt.java.demo.se.设计模式.设计模式23种.结构型.代理模式.动态代理;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class 动态代理 {
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
                        System.out.println("额外操作");
                        Object returnValue = method.invoke(object, args);
                        System.out.println("额外操作");
                        return null;
                    }
                });
    }
}