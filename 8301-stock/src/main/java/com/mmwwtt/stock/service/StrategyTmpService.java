package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.strategy.StrategyTmp;

import java.util.List;

public interface StrategyTmpService {

    /**
     * 根据sql条件查询
     */
    List<StrategyTmp> getBySql(String sql);
}
