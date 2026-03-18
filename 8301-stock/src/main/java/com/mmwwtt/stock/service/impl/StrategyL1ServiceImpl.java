package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyL1DAO;
import com.mmwwtt.stock.entity.strategy.BaseStrategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.service.StrategyL1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StrategyL1ServiceImpl extends ServiceImpl<StrategyL1DAO, StrategyL1> implements StrategyL1Service {

    @Override
    public List<StrategyL1> getBySql(String sql) {
        QueryWrapper<StrategyL1> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        return list(wrapper);
    }

    @Override
    public List<Integer> getIdList() {
        QueryWrapper<StrategyL1> wrapper = new QueryWrapper<>();
        wrapper.select("strategy_id");
        return list(wrapper).stream().map(BaseStrategy::getStrategyId).toList();
    }

}
