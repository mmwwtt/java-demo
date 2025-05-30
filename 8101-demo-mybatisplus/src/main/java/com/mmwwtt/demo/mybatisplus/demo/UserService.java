package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserDao, User> {
    private UserDao userDao;

//    public User saveOrUpdate(User user) {
//        userDao.insertOrUpdate(user);
//        return user;
//    }
//
//    public List<User> save(List<User> userList) {
//        userDao.insertOrUpdate();
//        return userList;
//    }

}
