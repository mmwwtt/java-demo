package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmwwtt.stock.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockDao extends BaseMapper<Stock> {
//    List<User> queryList(@Param("query") UserQuery query);
//    Page<User> queryPage(@Param("query") UserQuery query);
}

