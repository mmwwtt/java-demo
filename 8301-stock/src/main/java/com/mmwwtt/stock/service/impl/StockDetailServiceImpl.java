package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.dao.StockDetailDAO;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockDetailService;
import com.mmwwtt.stock.service.StockService;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
import com.mmwwtt.stock.vo.StockDetailVO;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
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

    @Resource
    private StockService stockService;

    @Resource
    private StockDetailDAO detailDAO;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();


    @Override
    public List<StockDetail> getStockDetail(StockDetailQueryVO queryVO) {
        QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
        detailWapper.eq("stock_code", queryVO.getStockCode());
        if (StringUtils.isNotBlank(queryVO.getDealDate())) {
            detailWapper.eq("deal_date", queryVO.getDealDate());
        }
        detailWapper.orderByDesc("deal_date");
        if (Objects.nonNull(queryVO.getLimit())) {
            detailWapper.last("LIMIT " + queryVO.getLimit());
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
            pairList.add(Pair.of(i + 6, t0::setT6));
            pairList.add(Pair.of(i + 7, t0::setT7));
            pairList.add(Pair.of(i + 8, t0::setT8));
            pairList.add(Pair.of(i + 9, t0::setT9));
            pairList.add(Pair.of(i + 10, t0::setT10));
            for (Pair<Integer, Consumer<StockDetail>> pair : pairList) {
                Integer idx = pair.getLeft();
                if (0 <= idx && idx < stockDetails.size()) {
                    StockDetail tmp = stockDetails.get(idx);
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
                    List<StockDetail> stockDetails = getStockDetail(StockDetailQueryVO.builder().stockCode(stock.getCode()).build());
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
    public Map<String, StockDetail> getCodeToCurDetailMap(String curDate) throws ExecutionException, InterruptedException {
        Map<String, StockDetail> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        for (List<String> part : CommonService.stockCodePartList) {
            for (String stockCode : part) {
                StockDetail detail = CommonService.codeToDetailMap.get(stockCode).stream()
                        .filter(item -> Objects.equals(curDate, item.getDealDate()))
                        .findFirst().orElse(null);
                if (Objects.nonNull(detail)) {
                    codeToDetailMap.put(stockCode, detail);
                }
            }
        }
        log.info("开始查询数据-结束");
        return codeToDetailMap;
    }


}
