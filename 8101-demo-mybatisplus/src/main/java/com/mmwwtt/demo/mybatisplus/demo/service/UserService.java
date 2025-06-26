package com.mmwwtt.demo.mybatisplus.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmwwtt.demo.mybatisplus.demo.User;
import com.mmwwtt.demo.mybatisplus.demo.UserQuery;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> queryList(UserQuery query);
    Page<User> queryPage(UserQuery query);


}
