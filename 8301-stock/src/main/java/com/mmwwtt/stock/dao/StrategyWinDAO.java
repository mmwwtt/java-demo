package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.StrategyWin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StrategyWinDAO extends BaseMapper<StrategyWin> {

    @Select("select distinct strategy_code from stock_strategy_win_t where level = #{level}")
    List<String> getStrategyCodeByLevel(Integer level);
}
