package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyTmpDAO;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.service.StrategyTmpService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyTmpServiceImpl extends ServiceImpl<StrategyTmpDAO, StrategyTmp> implements StrategyTmpService {

    @Override
    public List<StrategyTmp> getBySql(String sql) {
        QueryWrapper<StrategyTmp> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        return list(wrapper);
    }
}
