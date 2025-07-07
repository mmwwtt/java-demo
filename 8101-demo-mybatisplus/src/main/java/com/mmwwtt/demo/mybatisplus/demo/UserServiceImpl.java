package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.demo.mybatisplus.demo.dao.UserDao;
import com.mmwwtt.demo.mybatisplus.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Resource
    private UserDao userDao;

    public List<User> queryList(UserQuery query) {
        return userDao.queryList(query);
    }

    public Page<User> queryPage(UserQuery query) {
        return userDao.queryPage(query);
    }

}
