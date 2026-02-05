package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockDetailDAO;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockDetailService;
import com.mmwwtt.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class StockDetailServiceImpl extends ServiceImpl<StockDetailDAO, StockDetail> implements StockDetailService {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockDetailDAO detailDAO;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    @Override
    public List<StockDetail> getStockDetail(String stockCode, Integer limit) {
        QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
        detailWapper.eq("stock_code", stockCode);
        detailWapper.orderByDesc("deal_date");
        if (Objects.nonNull(limit)) {
            detailWapper.last("LIMIT " + limit);
        }
        List<StockDetail> stockDetails = list(detailWapper);
        return genAllStockDetail(stockDetails);
    }


    public List<StockDetail> genAllStockDetail(List<StockDetail> stockDetails) {
        for (int i = 0; i < stockDetails.size(); i++) {
            StockDetail t0 = stockDetails.get(i);
            List<Pair<Integer, Consumer<StockDetail>>> pairList = new ArrayList<>();
            pairList.add(Pair.of(i - 1, t0::setNext1));
            pairList.add(Pair.of(i - 2, t0::setNext2));
            pairList.add(Pair.of(i - 3, t0::setNext3));
            pairList.add(Pair.of(i - 4, t0::setNext4));
            pairList.add(Pair.of(i - 5, t0::setNext5));
            pairList.add(Pair.of(i - 10, t0::setNext10));
            pairList.add(Pair.of(i + 1, t0::setT1));
            pairList.add(Pair.of(i + 2, t0::setT2));
            pairList.add(Pair.of(i + 3, t0::setT3));
            pairList.add(Pair.of(i + 4, t0::setT4));
            pairList.add(Pair.of(i + 5, t0::setT5));
            for (Pair<Integer, Consumer<StockDetail>> pair : pairList) {
                Integer idx = pair.getLeft();
                if (0 <= idx && idx < stockDetails.size()) {
                    StockDetail tmp = StockConverter.INSTANCE.convertToStockDetail(stockDetails.get(idx));
                    pair.getRight().accept(tmp);
                }
            }
        }
        return stockDetails;
    }


    @Override
    public Map<String, List<StockDetail>> getCodeToDetailMap() throws ExecutionException, InterruptedException {
        return getCodeToDetailMap(null);
    }

    @Override
    public Map<String, List<StockDetail>> getCodeToDetailMap(Integer limit) throws ExecutionException, InterruptedException {
        List<Stock> stockList = stockService.getAllStock();
        Map<String, List<StockDetail>> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = getStockDetail(stock.getCode(), limit);
                    codeToDetailMap.put(stock.getCode(), stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("开始查询数据-结束");
        return codeToDetailMap;
    }

    @Override
    public Map<String, StockDetail> getCodeToTodayDetailMap() throws ExecutionException, InterruptedException {
        List<Stock> stockList = stockService.getAllStock();
        Map<String, StockDetail> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = getStockDetail(stock.getCode(), 10);
                    if (CollectionUtils.isEmpty(stockDetails)) {
                        continue;
                    }
                    codeToDetailMap.put(stock.getCode(), stockDetails.get(0));
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("开始查询数据-结束");
        return codeToDetailMap;
    }

}
