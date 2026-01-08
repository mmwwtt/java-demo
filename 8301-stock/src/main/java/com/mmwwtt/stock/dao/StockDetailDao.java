package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockDetailDao extends BaseMapper<StockDetail> {
}
