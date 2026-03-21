package com.mmwwtt.stock.service.interfaces;

import com.mmwwtt.stock.entity.strategy.StrategyL1;

import java.util.List;


public interface StrategyL1Service {

    /**
     * 根据sql条件查询
     */
    List<StrategyL1> getBySql(String sql);

    /**
     * 查询id列表
     */
    List<Integer> getIdList();


}
