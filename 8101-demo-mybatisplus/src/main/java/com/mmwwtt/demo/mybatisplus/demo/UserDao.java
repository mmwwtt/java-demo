package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {
    List<User> queryList(@Param("query") UserQuery query);
    Page<User> queryPage(@Param("query") UserQuery query);
}
