package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StockDAO;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockDAO, Stock>  implements StockService {
}
