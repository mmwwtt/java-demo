package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.strategy.Strategy;

import java.util.List;

public interface StrategyService {

    /**
     * 根据sql条件查询
     */
    List<Strategy> getBySql(String sql);
}
