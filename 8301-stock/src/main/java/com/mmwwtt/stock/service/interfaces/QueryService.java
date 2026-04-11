package com.mmwwtt.stock.service.interfaces;

import com.mmwwtt.stock.entity.strategy.Query;

import java.util.List;

public interface QueryService {
    /**
     * 根据sql条件查询
     */
    List<Query> getBySql(String sql);
}
