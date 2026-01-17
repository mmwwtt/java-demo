package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StockCalcService {

    /**
     * 开始计算   通过上/下影线占比  和涨跌百分比区间 计算预测胜率
     */

    void startCalc1() throws ExecutionException, InterruptedException;


    /**
     * 根据策略计算
     */
    void startCalc2() throws ExecutionException, InterruptedException;

    /**
     * 根据单个策略 生成蜡烛图
     */
    void startCalc3() throws ExecutionException, InterruptedException;


    /**
     * 用于计算单个策略
     */
    void startCalc4() throws ExecutionException, InterruptedException;

    /**
     * 获得所有股票列表
     */
    List<Stock> getAllStock();

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
}
