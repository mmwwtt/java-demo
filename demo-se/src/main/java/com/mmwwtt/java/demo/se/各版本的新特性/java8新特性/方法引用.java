package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import com.mmwwtt.java.demo.se.common.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class 方法引用 {

    @Test
    public void 静态方法引用() {
        List<User> userList = User.getUserList();
        userList.forEach(User::format);
        assertNotNull(userList);
    }

    /**
     * 普通方法不能携带参数
     */
    @Test
    public void 普通方法引用() {
        List<User> userList = User.getUserList();
        userList.forEach(User::sayHello);
        assertNotNull(userList);
    }

    @Test
    public void 实例方法引用() {
        User user = User.getUser1();
        Runnable sayHello = user::sayHello;
    }
}
