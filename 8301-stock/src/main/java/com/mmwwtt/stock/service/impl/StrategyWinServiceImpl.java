package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyWinDAO;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.StrategyWinService;
import com.mmwwtt.stock.vo.StrategyWinVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StrategyWinServiceImpl extends ServiceImpl<StrategyWinDAO, StrategyWin> implements StrategyWinService {

    @Resource
    private StrategyWinDAO strategyWinDAO;

    @Override
    public List<StrategyWin> getStrategyWin(String sql) {
        QueryWrapper<StrategyWin> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        return list(wrapper);
    }


    @Override
    public List<StrategyWin> getL1StrategyWin() {
        QueryWrapper<StrategyWin> wrapper = new QueryWrapper<>();
        wrapper.eq("level", 1);
        return strategyWinDAO.selectList(wrapper);
    }
}
