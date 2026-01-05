package com.mmwwtt.stock.controller;

import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.service.StockStartService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Resource
    private StockStartService stockStartService;

    @Resource
    private StockDao stockDao;

    @GetMapping("/calc")
    public Boolean getCurStockList() throws ExecutionException, InterruptedException {
        stockStartService.startCalc();
        return true;
    }

    @GetMapping("/calc1")
    public Boolean getCur1StockList() throws ExecutionException, InterruptedException {
        stockStartService.startCalc1();
        return true;
    }


    @GetMapping("/calc2")
    public Boolean getCur2StockList() throws ExecutionException, InterruptedException {
        stockStartService.startCalc2();
        return true;
    }
}
