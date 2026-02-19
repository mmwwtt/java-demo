package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StrategyResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StrategyResultService {

    /**
     * 获得所有股票列表
     */
    List<StrategyResult> getStrategyResult(StrategyResult strategyResult);

}
