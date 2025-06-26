package com.mmwwtt.demo.mybatisplus.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmwwtt.demo.mybatisplus.demo.User;
import com.mmwwtt.demo.mybatisplus.demo.UserQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao extends BaseMapper<User> {
    List<User> queryList(@Param("query") UserQuery query);
    Page<User> queryPage(@Param("query") UserQuery query);
}
