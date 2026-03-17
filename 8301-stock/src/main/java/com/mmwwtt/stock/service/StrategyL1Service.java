package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.strategy.StrategyL1;

import java.util.List;


public interface StrategyL1Service {

    /**
     * 根据sql条件查询
     */
    List<StrategyL1> getBySql(String sql);

}
