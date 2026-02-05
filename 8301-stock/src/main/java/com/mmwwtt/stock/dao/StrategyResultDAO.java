package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.StrategyResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StrategyResultDAO extends BaseMapper<StrategyResult> {

    @Select("select distinct strategy_code from stock_strategy_result_t")
    List<String> getStrategyCode();

    @Select("select distinct strategy_code from stock_strategy_result_t where level = #{level}")
    List<String> getStrategyCodeByLevel(Integer level);
}
