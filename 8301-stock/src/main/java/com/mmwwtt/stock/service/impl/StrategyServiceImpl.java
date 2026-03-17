package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyDAO;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.service.StrategyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StrategyServiceImpl extends ServiceImpl<StrategyDAO, Strategy> implements StrategyService {

    @Resource
    private StrategyDAO strategyL1DAO;


    @Override
    public List<Strategy> getBySql(String sql) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        return list(wrapper);
    }

}
