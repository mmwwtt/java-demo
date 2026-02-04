package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface StrategyWinService {

    /**
     * 根据符合条件的股票详情列表保存预测数据
     */
    void saveByDetails(List<StockDetail> allAfterList, String strategyCodes, LocalDateTime now);
}
