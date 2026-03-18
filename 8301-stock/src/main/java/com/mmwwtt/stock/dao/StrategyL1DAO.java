package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StrategyL1DAO extends BaseMapper<StrategyL1> {
    /**
     * 查询id列表
     */
    @Select("select strategy_id from strategy_l1_t")
    List<Integer> getIdList();
}
