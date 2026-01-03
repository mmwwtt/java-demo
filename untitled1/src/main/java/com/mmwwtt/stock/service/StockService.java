package com.mmwwtt.stock.service;


import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 股票服务
 */
@Service
public interface StockService {

    List<OneRes> run(List<StockDetail> list);

    String getStrategy();

    int getDayNum();
}
