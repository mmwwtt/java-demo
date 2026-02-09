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


    Map<String, Set<Integer>> getStockCodeToDateMap(String strategyCode);

    /**
     * 策略-股票代码-日期Set
     */
    Map<String, Map<String, Set<Integer>>> getLevel1StrategyToStockAndDateSetMap();
}
