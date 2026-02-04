package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StockDetailService {

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

}
