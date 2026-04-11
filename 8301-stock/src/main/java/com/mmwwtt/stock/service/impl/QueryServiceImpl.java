package com.mmwwtt.stock.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.QueryDAO;
import com.mmwwtt.stock.entity.strategy.Query;
import com.mmwwtt.stock.service.interfaces.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QueryServiceImpl extends ServiceImpl<QueryDAO, Query> implements QueryService {

    @Override
    public List<Query> getBySql(String sql) {
        QueryWrapper<Query> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        return list(wrapper);
    }
}
