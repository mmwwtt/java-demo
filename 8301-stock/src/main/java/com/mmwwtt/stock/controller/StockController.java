package com.mmwwtt.stock.controller;

import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.service.StockCalcServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Resource
    private StockCalcServiceImpl stockCalcServiceImpl;

    @Resource
    private StockDao stockDao;

    @GetMapping("/calc1")
    public Boolean getCur1StockList() throws ExecutionException, InterruptedException {
        stockCalcServiceImpl.startCalc1();
        return true;
    }
}
