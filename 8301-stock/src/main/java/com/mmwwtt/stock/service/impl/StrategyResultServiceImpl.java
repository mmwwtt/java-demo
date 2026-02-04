package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.dao.StrategyResultDao;
import com.mmwwtt.stock.entity.StrategyResult;
import com.mmwwtt.stock.service.StrategyResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyResultServiceImpl implements StrategyResultService {

    @Autowired
    private StrategyResultDao strategyResultDao;

    @Override
    public List<StrategyResult> getStrategyResultByName(String strategyName, Integer level) {
        QueryWrapper<StrategyResult> wapper = new QueryWrapper<>();
        wapper.eq("strategy_code", strategyName)
                .eq("level", level);
        return strategyResultDao.selectList(wapper);
    }

    @Override
    public Map<String, Set<String>> getStockCodeToDateMap(String strategy, Integer level) {
        return getStrategyResultByName(strategy, 1)
                .stream().collect(Collectors.toMap(StrategyResult::getStockCode, item ->
                        Arrays.stream(item.getDateList().split(" ")).collect(Collectors.toSet()))
                );
    }

    @Override
    public List<String> getStrategyNameByLevel(Integer level) {
        return List.of();
    }
}
