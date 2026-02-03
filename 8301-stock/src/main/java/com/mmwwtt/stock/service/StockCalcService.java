package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.entity.StrategyResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StockCalcService {

    /**
     * 开始计算   通过上/下影线占比  和涨跌百分比区间 计算预测胜率
     */

    void startCalc1() throws ExecutionException, InterruptedException;

    /**
     * 获得所有股票列表
     */
    List<Stock> getAllStock();

    List<List<Stock>> getStockPart();

    /**
     * 获取指定代码的股票列表
     */
    List<StockDetail> getStockDetail(String stockCode, Integer limit);

    /**
     * 获得股票   交易列表
     */
    Map<String, List<StockDetail>> getCodeToDetailMap() throws ExecutionException, InterruptedException;

    /**
     * 获得股票 交易列表
     */
    Map<String, List<StockDetail>> getCodeToDetailMap(Integer limit) throws ExecutionException, InterruptedException;

    /**
     * 获得股票 当天交易数据
     */
    Map<String, StockDetail> getCodeToTodayDetailMap() throws ExecutionException, InterruptedException;


    /**
     * 保存策略
     */
    void saveCalcRes(List<StockDetail> allAfterList, String strategyDesc, LocalDateTime dataTime, String type);

    /**
     * 根据策略计算
     */
    Map<String, List<StockDetail>>  calcByStrategy(List<StockStrategy> strategyList) throws ExecutionException, InterruptedException;

    List<StockStrategy> STRATEGY_LIST = new ArrayList<>();

    static StockStrategy getStrategy(String name) {
        return STRATEGY_LIST.stream().filter(item -> item.getStrategyName().startsWith(name)).findFirst().orElse(null);
    }

    static List<StockStrategy> getStrategyList(String name) {
        return STRATEGY_LIST.stream().filter(item -> item.getStrategyName().startsWith(name)).toList();
    }

    /**
     * 获得所有股票列表
     */
    List<StrategyResult> getStrategyResultByName(String strategyName);

}
