package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockDAO extends BaseMapper<Stock> {
//    List<User> queryList(@Param("query") UserQuery query);
//    Page<User> queryPage(@Param("query") UserQuery query);
}

