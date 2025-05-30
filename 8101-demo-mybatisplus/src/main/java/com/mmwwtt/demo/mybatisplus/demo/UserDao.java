package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao extends BaseMapper<User> {
    List<User> query(@Param("query") UserQuery query);
    Page<User> queryByPage(@Param("query") UserQuery query);
}
