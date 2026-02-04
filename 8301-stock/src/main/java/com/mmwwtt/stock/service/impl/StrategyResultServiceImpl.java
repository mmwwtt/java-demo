package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyResultDAO;
import com.mmwwtt.stock.entity.StrategyResult;
import com.mmwwtt.stock.service.StrategyResultService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyResultServiceImpl extends ServiceImpl<StrategyResultDAO, StrategyResult> implements StrategyResultService {

    @Resource
    private StrategyResultDAO strategyResultDAO;

    @Override
    public List<StrategyResult> getStrategyResult(StrategyResult strategyResult) {
        QueryWrapper<StrategyResult> wapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(strategyResult.getStockCode())) {
            wapper.eq("strategy_code", strategyResult.getStockCode());
        }
        if(Objects.nonNull(strategyResult.getLevel())) {
            wapper.eq("level", strategyResult.getLevel());
        }
        if(StringUtils.isNotEmpty(strategyResult.getStockCode())) {
            wapper.eq("stock_code", strategyResult.getStockCode());
        }
        return list(wapper);
    }

    @Override
    public Map<String, Set<String>> getStockCodeToDateMap(String stockCode, Integer level) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setLevel(1);
        strategyResult.setStockCode(stockCode);
        return getStrategyResult(strategyResult)
                .stream().collect(Collectors.toMap(StrategyResult::getStockCode, item ->
                        Arrays.stream(item.getStockDetailIdList().split(" ")).collect(Collectors.toSet()))
                );
    }

    @Override
    public List<String> getStrategyCodeByLevel(Integer level) {
        return strategyResultDAO.getStrategyCodeByLevel(level);
    }

    @Override
    public List<String> getStrategyCode() {
        return strategyResultDAO.getStrategyCode();
    }
}
