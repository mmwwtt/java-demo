package com.mmwwtt.java.demo.se.common;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    Long userId;
    String userName;
    int sex;
    String sexName;
    int age;

    public User(Long userId) {
        this.userId = userId;
    }

    public User(Long userId, String userName, int sex, int age) {
        this.userId = userId;
        this.userName = userName;
        this.sex = sex;
        this.age = age;
    }

    public static List<User> getUserList() {
        List<User> list = new ArrayList();
        list.add(new User(Long.valueOf(1), "小明", 1, 1));
        list.add(new User(Long.valueOf(2), "张华", 1, 2));
        list.add(new User(Long.valueOf(3), "王二", 2, 3));
        list.add(new User(Long.valueOf(4), "赵九", 2, 2));
        return list;
    }

    public static User getUser1() {
        User user = new User(Long.valueOf(1), "小明", 1, 1);
        return user;
    }

    public static User getUser2() {
        User user = new User(Long.valueOf(2), "小华", 2, 2);
        return user;
    }

    public static void format(User user) {
        user.setSexName(user.getSex() == 1 ? "男" : "女");
    }

    public void sayHello() {
        System.out.println(userName + " hello");
    }
}
