package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyWinDAO;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.StrategyWinService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StrategyWinServiceImpl extends ServiceImpl<StrategyWinDAO, StrategyWin> implements StrategyWinService {

    @Resource
    private StrategyWinDAO strategyWinDAO;

    @Override
    public List<StrategyWin> getStrategyWin(StrategyWin strategyWin) {
        QueryWrapper<StrategyWin> wapper = new QueryWrapper<>();
        if(Objects.nonNull(strategyWin.getLevel())) {
            wapper.eq("level", strategyWin.getLevel());
        }
        if(Objects.nonNull(strategyWin.getWinRate())) {
            wapper.ge("win_rate", strategyWin.getWinRate());
        }
        if(Objects.nonNull(strategyWin.getFiveMaxPercRate())) {
            wapper.ge("five_max_perc_rate", strategyWin.getFiveMaxPercRate());
        }
        return list(wapper);
    }


    @Override
    public List<StrategyWin> getL1StrategyWin() {
        QueryWrapper<StrategyWin> wapper = new QueryWrapper<>();
        wapper.eq("level",1);
        return strategyWinDAO.selectList(wapper);
    }
}
