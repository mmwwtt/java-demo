package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StrategyResult;

import java.util.List;


public interface StrategyResultService {

    /**
     * 获得所有股票列表
     */
    List<StrategyResult> getStrategyResult(StrategyResult strategyResult);

}
