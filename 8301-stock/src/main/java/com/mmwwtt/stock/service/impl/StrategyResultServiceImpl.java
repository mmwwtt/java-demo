package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyResultDAO;
import com.mmwwtt.stock.dao.StrategyWinDAO;
import com.mmwwtt.stock.entity.StrategyResult;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.StrategyResultService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyResultServiceImpl extends ServiceImpl<StrategyResultDAO, StrategyResult> implements StrategyResultService {

    @Resource
    private StrategyResultDAO strategyResultDAO;

    @Resource
    private StrategyWinDAO strategyWinDAO;

    @Override
    public List<StrategyResult> getStrategyResult(StrategyResult strategyResult) {
        QueryWrapper<StrategyResult> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(strategyResult.getStrategyCode())) {
            wrapper.eq("strategy_code", strategyResult.getStrategyCode());
        }
        if (Objects.nonNull(strategyResult.getLevel())) {
            wrapper.eq("level", strategyResult.getLevel());
        }
        return list(wrapper);
    }

}
