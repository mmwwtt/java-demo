package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyResultDAO;
import com.mmwwtt.stock.dao.StrategyWinDAO;
import com.mmwwtt.stock.entity.StrategyResult;
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

    @Autowired
    private StrategyWinDAO strategyWinDAO;

    @Override
    public List<StrategyResult> getStrategyResult(StrategyResult strategyResult) {
        QueryWrapper<StrategyResult> wapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(strategyResult.getStockCode())) {
            wapper.eq("stock_code", strategyResult.getStockCode());
        }
        if (StringUtils.isNotEmpty(strategyResult.getStrategyCode())) {
            wapper.eq("strategy_code", strategyResult.getStrategyCode());
        }
        if (Objects.nonNull(strategyResult.getLevel())) {
            wapper.eq("level", strategyResult.getLevel());
        }
        return list(wapper);
    }

    @Override
    public Map<String, Set<Integer>> getStockCodeToDateMap(String strategyCode) {
        try {
            StrategyResult strategyResult = new StrategyResult();
            strategyResult.setStrategyCode(strategyCode);

            return getStrategyResult(strategyResult)
                    .stream().collect(Collectors.toMap(StrategyResult::getStockCode,
                            item -> {
                                Set<Integer> set = new HashSet<>(item.getStockDetailIdList().size());
                                for (int i = 0; i < item.getStockDetailIdList().size(); i++) {
                                    set.add(item.getStockDetailIdList().getInteger(i));
                                }
                                return set;
                            },(a,b) -> b
                    ));
        } catch (Exception e) {
            log.info("getStockCodeToDateMap error , stragetyCode : {}", strategyCode);
            throw e;
        }
    }


    @Override
    public Map<String, Map<String, Set<Integer>>> getLevel1StrategyToStockAndDateSetMap() {
        Map<String, Map<String, Set<Integer>>> res = new HashMap<>();

        List<String> level1CodeList = strategyWinDAO.getStrategyCodeByLevel(1);
        level1CodeList.forEach(strategyCode -> {
            Map<String, Set<Integer>> stockCodeToDateMap = getStockCodeToDateMap(strategyCode);
            res.put(strategyCode, stockCodeToDateMap);
        });
        return res;
    }
}
