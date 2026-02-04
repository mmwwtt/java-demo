package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.dao.StockDAO;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockDAO, Stock>  implements StockService {

    @Override
    public List<Stock> getAllStock() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = list(queryWrapper);
        //30开头是创业板  68开头是科创版  不参与
        stockList = stockList.stream()
                .filter(stock -> !stock.getCode().startsWith("30")
                        && !stock.getCode().startsWith("68")
                        && !stock.getName().contains("ST"))
                .toList();
        return stockList;
    }

    @Override
    public List<List<Stock>> getStockPart() {
        return Lists.partition(getAllStock(), 50);
    }
}
