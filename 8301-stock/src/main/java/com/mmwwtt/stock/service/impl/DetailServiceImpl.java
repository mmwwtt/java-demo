package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.dao.DetailDAO;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.service.DetailService;
import com.mmwwtt.stock.service.StockService;
import com.mmwwtt.stock.vo.DetailQueryVO;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class DetailServiceImpl extends ServiceImpl<DetailDAO, Detail> implements DetailService {

    @Resource
    private StockService stockService;

    @Resource
    private DetailDAO detailDAO;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();


    @Override
    public List<Detail> getStockDetail(DetailQueryVO queryVO) {
        QueryWrapper<Detail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("stock_code", queryVO.getStockCode());
        if (StringUtils.isNotBlank(queryVO.getDealDate())) {
            detailWrapper.eq("deal_date", queryVO.getDealDate());
        }
        detailWrapper.orderByDesc("deal_date");
        if (Objects.nonNull(queryVO.getLimit())) {
            detailWrapper.last("LIMIT " + queryVO.getLimit());
        }
        List<Detail> details = list(detailWrapper);
        return genAllStockDetail(details);
    }


    public List<Detail> genAllStockDetail(List<Detail> details) {
        for (int i = 0; i < details.size(); i++) {
            Detail t0 = details.get(i);
            List<Pair<Integer, Consumer<Detail>>> pairList = new ArrayList<>();
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
            for (Pair<Integer, Consumer<Detail>> pair : pairList) {
                Integer idx = pair.getLeft();
                if (0 <= idx && idx < details.size()) {
                    Detail tmp = details.get(idx);
                    pair.getRight().accept(tmp);
                }
            }
        }
        return details;
    }


    @Override
    public Map<String, List<Detail>> getCodeToDetailMap() throws ExecutionException, InterruptedException {
        return getCodeToDetailMap(null);
    }

    @Override
    public Map<String, List<Detail>> getCodeToDetailMap(Integer limit) throws ExecutionException, InterruptedException {
        List<Stock> stockList = stockService.getAllStock();
        Map<String, List<Detail>> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<Detail> details = getStockDetail(DetailQueryVO.builder().stockCode(stock.getCode()).build());
                    codeToDetailMap.put(stock.getCode(), details);
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
    public Map<String, Detail> getCodeToCurDetailMap(String curDate) {
        Map<String, Detail> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        for (List<String> part : CommonService.stockCodePartList) {
            for (String stockCode : part) {
                Detail detail = CommonService.codeToDetailMap.get(stockCode).stream()
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
