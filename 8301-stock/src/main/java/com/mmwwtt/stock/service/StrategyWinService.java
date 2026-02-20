package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.vo.StrategyWinVO;

import java.util.List;

public interface StrategyWinService {

    /**
     * 获得所有股票列表
     */
    List<StrategyWin> getStrategyWin(StrategyWinVO strategyWin);

    /**
     * 获取1层策略运算结果
     */
    List<StrategyWin> getL1StrategyWin();

}
