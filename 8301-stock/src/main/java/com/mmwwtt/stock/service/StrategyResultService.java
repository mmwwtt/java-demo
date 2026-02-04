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


    Map<String, Set<String>> getStockCodeToDateMap(String strategy, Integer level);

    /**
     * 获取对应level的策略名列表
     * @param level
     * @return
     */
    List<String> getStrategyCodeByLevel(Integer level);

    /**
     * 获取策略名列表
     */
    List<String> getStrategyCode();
}
