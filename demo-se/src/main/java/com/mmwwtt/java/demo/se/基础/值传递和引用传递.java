package com.mmwwtt.java.demo.se.基础;


import com.mmwwtt.java.demo.se.common.User;
import org.junit.jupiter.api.Test;

public class 值传递和引用传递 {

    /**
     * 值传递传的是值本身(基本数据类型)
     * 引用传递传的是地址值(其他类型)
     */
    public void fun(int num, User user1, User user2) {
        //值传递，不会改变原值
        num = 3;

        //引用传递，修改引用地址上的对象
        user1.setAge(18);

        //引用传递，point2指向了新的引用地址，不会对原地址上的对象造成影响
        user2 = User.getUser2();
    }

    @Test
    public void test() {
        int num = 1;
        User user1 = User.getUser1();
        User user2 = User.getUser2();

        fun(num, user1, user2);

        System.out.println(num);
        System.out.println(user1);
        System.out.println(user2);
    }
}
