package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.strategy.Query;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueryDAO extends BaseMapper<Query> {
}
