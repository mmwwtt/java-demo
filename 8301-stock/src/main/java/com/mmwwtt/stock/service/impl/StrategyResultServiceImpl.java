package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyResultDAO;
import com.mmwwtt.stock.entity.StrategyResult;
import com.mmwwtt.stock.service.StrategyResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyResultServiceImpl  extends ServiceImpl<StrategyResultDAO, StrategyResult>  implements StrategyResultService {


    @Override
    public List<StrategyResult> getStrategyResultByName(String strategyName, Integer level) {
        QueryWrapper<StrategyResult> wapper = new QueryWrapper<>();
        wapper.eq("strategy_code", strategyName)
                .eq("level", level);
        return list(wapper);
    }

    @Override
    public Map<String, Set<String>> getStockCodeToDateMap(String strategy, Integer level) {
        return getStrategyResultByName(strategy, 1)
                .stream().collect(Collectors.toMap(StrategyResult::getStockCode, item ->
                        Arrays.stream(item.getStockDetailIdList().split(" ")).collect(Collectors.toSet()))
                );
    }

    @Override
    public List<String> getStrategyCodesByLevel(Integer level) {
        return List.of();
    }

    @Override
    public List<String> getStrategyCodes() {
        return List.of();
    }
}
