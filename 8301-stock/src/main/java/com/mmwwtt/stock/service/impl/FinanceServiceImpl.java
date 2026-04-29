package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.FinanceDao;
import com.mmwwtt.stock.dao.StockDAO;
import com.mmwwtt.stock.entity.Finance;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.service.interfaces.FinanceService;
import com.mmwwtt.stock.service.interfaces.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FinanceServiceImpl extends ServiceImpl<FinanceDao, Finance>  implements FinanceService {
}
