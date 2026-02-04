package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.Stock;

import java.util.List;

public interface StockService {
    /**
     * 获得所有股票列表
     */
    List<Stock> getAllStock();

    /**
     * 获得所有股票列表
     * @return
     */
    List<List<Stock>> getStockPart();
}
