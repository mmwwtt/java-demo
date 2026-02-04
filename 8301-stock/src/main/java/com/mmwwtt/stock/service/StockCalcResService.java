package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface StockCalcResService {
    /**
     * 保存策略
     */
    void saveCalcRes(List<StockDetail> allAfterList, String strategyDesc, LocalDateTime dataTime, String type);

}
