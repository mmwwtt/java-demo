package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.moreThan;

@Service
@Slf4j
public class CommonService {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private StockDetailServiceImpl stockDetailService;

    @Resource
    private StrategyResultServiceImpl strategyResultService;


    @Resource
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    public static Map<Integer, StockDetail> idToDetailMap = new ConcurrentHashMap<>(1048576);
    public static Map<String, List<StockDetail>> codeToDetailMap = new ConcurrentHashMap<>(4096);
    public static Map<String, String> stockCodeToNameMap;
    public static List<StrategyWin> l1StrategyList;
    public static List<List<String>> stockCodePartList;
    public static List<String> stockCodeList;
    public static String calcEndDate;
    public static List<String> predictDateList;
    public static Map<String, int[]> strategyToDetailsMap = new ConcurrentHashMap<>(512);

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("初始化开始");
        List<StrategyResult> strategyResults = strategyResultService.getStrategyResult(StrategyResult.builder().level(1).build());
        strategyResults.forEach(item -> {
            int[] ids = new int[item.getStockDetailIdList().size()];
            for (int i = 0; i < item.getStockDetailIdList().size(); i++) {
                ids[i] = item.getStockDetailIdList().getIntValue(i);
            }
            strategyToDetailsMap.put(item.getStrategyCode(), ids);
        });

        l1StrategyList = strategyWinService.getL1StrategyWin();
        stockCodeList = stockService.list().stream().map(Stock::getCode).toList();
        stockCodePartList = Lists.partition(stockCodeList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        stockCodePartList.forEach(part -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> part.forEach(stockCode -> {
                List<StockDetail> stockDetails = stockDetailService.getStockDetail(new StockDetailQueryVO(stockCode));
                stockDetails.sort(Comparator.comparing(StockDetail::getDealDate).reversed());
                stockDetailService.genAllStockDetail(stockDetails);
                stockDetails.forEach(item -> idToDetailMap.put(item.getStockDetailId(), item));
                codeToDetailMap.put(stockCode, stockDetails);
            }), ioThreadPool);
            futures.add(future);
        });
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();


        predictDateList = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().limit(15)
                .map(StockDetail::getDealDate).toList();
        calcEndDate = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().skip(15)
                .map(StockDetail::getDealDate).findFirst().orElse("20260201");
        stockCodeToNameMap = stockService.list().stream().collect(Collectors.toMap(Stock::getCode, Stock::getName));
        log.info("初始化结束");
    }


    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        QueryWrapper<StrategyWin> winWrapper = new QueryWrapper<>();
        winWrapper.apply("level!=1");
        strategyWinService.remove(winWrapper);

        QueryWrapper<StrategyResult> resultWrapper = new QueryWrapper<>();
        resultWrapper.apply("level!=1");
        strategyResultService.remove(resultWrapper);

        List<StrategyEnum> values = StrategyEnum.dayForStrategyList;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyEnum strategy : values) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Integer> dateList = new ArrayList<>();
                for (String stockCode : stockCodeList) {
                    List<StockDetail> stockDetails = codeToDetailMap.get(stockCode);
                    for (StockDetail detail : stockDetails) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getPricePert(), 0.097)
                                || detail.getDealDate().compareTo("202505") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategy.getRunFunc().apply(detail)) {
                            dateList.add(detail.getStockDetailId());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(dateList) && dateList.size() > 10) {
                    StrategyResult strategyResult = new StrategyResult(1, strategy.getCode(), dateList);
                    strategyResultService.save(strategyResult);
                }

            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("策略层级 1 计算 - 结束");

        //计算第一层的策略的win结果
        List<StrategyResult> results = strategyResultService.getStrategyResult(
                StrategyResult.builder().level(1).build());
        futures = new ArrayList<>();
        for (StrategyResult result : results) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                StrategyWin strategyWin = new StrategyWin(result.getStrategyCode());
                result.getStockDetailIdList().stream()
                        .map(item -> (Integer) item)
                        .forEach(item -> strategyWin.addToResult(idToDetailMap.get(item)));
                strategyWin.fillData1();
                strategyWin.fillData2();
                strategyWinService.save(strategyWin);
            }, ioThreadPool);
            futures.add(future);
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算第一层的策略的win结果 结束");
    }
}
