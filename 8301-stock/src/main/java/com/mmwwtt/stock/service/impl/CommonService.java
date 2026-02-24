package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.moreThan;

@Service
@Slf4j
public class CommonService {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockDetailServiceImpl stockDetailService;

    @Autowired
    private StrategyResultServiceImpl strategyResultService;


    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    public static Map<String, Map<String, Set<Integer>>> l1StrategyToStockToDetailIdSetMap;
    public static Map<Integer, StockDetail> idToDetailMap;
    public static Map<String, List<StockDetail>> codeToDetailMap;
    public static Map<String, String> stockCodeToNameMap;
    public static List<StrategyWin> l1StrategyList;
    public static List<List<String>> stockCodePartList;
    public static String calcEndDate;
    public static List<String> predictDateList;

    @PostConstruct
    public void init() {
        log.info("初始化开始");

        l1StrategyToStockToDetailIdSetMap = new HashMap<>();
        Map<String, List<StrategyResult>> strategyToResList = strategyResultService.list()
                .stream().filter(item -> Objects.equals(item.getLevel(), 1))
                .collect(Collectors.groupingBy(StrategyResult::getStrategyCode));
        strategyToResList.forEach((strategy, list) -> {
            Map<String, Set<Integer>> codeToDetailMap = new HashMap<>();
            list.forEach(item -> {
                Set<Integer> set = new HashSet<>(item.getStockDetailIdList().size());
                for (int i = 0; i < item.getStockDetailIdList().size(); i++) {
                    set.add(item.getStockDetailIdList().getInteger(i));
                }
                codeToDetailMap.put(item.getStockCode(), set);
            });
            l1StrategyToStockToDetailIdSetMap.put(strategy, codeToDetailMap);
        });

        l1StrategyList = strategyWinService.getL1StrategyWin().stream()
                .filter(item -> moreThan(item.getWinRate(), "0.40"))
                .sorted(Comparator.comparing(StrategyWin::getStrategyCode).reversed()).toList();

        List<StockDetail> allDetailList = stockDetailService.list();
        idToDetailMap = allDetailList.stream().collect(Collectors.toMap(StockDetail::getStockDetailId, item -> item));
        codeToDetailMap = allDetailList.stream().collect(Collectors.groupingBy(StockDetail::getStockCode));
        for (List<StockDetail> list : codeToDetailMap.values()) {
            list.sort(Comparator.comparing(StockDetail::getDealDate).reversed());
            stockDetailService.genAllStockDetail(list);
        }
        stockCodePartList = Lists.partition(codeToDetailMap.keySet().stream().toList(), 50);


        predictDateList = codeToDetailMap.get("000001.SZ")
                .stream().limit(20)
                .map(StockDetail::getDealDate).toList();
        calcEndDate = codeToDetailMap.get("000001.SZ")
                .stream().skip(20)
                .map(StockDetail::getDealDate).findFirst().orElse("20260201");
        stockCodeToNameMap = stockService.list().stream().collect(Collectors.toMap(Stock::getCode, Stock::getName));
        log.info("初始化结束");
    }


    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        List<StrategyEnum> values = StrategyEnum.strategy2_4DayList;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<String> part : stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (String stockCode : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.get(stockCode);
                    List<StrategyResult> list = new ArrayList<>();
                    for (StrategyEnum strategy : values) {
                        Set<Integer> dateList = new HashSet<>();
                        for (StockDetail detail : stockDetails) {
                            if (Objects.isNull(detail.getNext1())
                                    || Objects.isNull(detail.getT10())
                                    || Objects.isNull(detail.getT10().getSixtyDayLine())
                                    || moreThan(detail.getPricePert(), "0.097")
                                    || detail.getDealDate().compareTo("202505") < 0
                                    || detail.getDealDate().compareTo(calcEndDate) > 0) {
                                continue;
                            }
                            if (strategy.getRunFunc().apply(detail)) {
                                dateList.add(detail.getStockDetailId());
                            }
                        }
                        if (!CollectionUtils.isEmpty(dateList)) {
                            StrategyResult strategyResult = new StrategyResult(1, strategy.getCode(),
                                    stockCode, dateList);
                            list.add(strategyResult);
                        }
                    }
                    if (CollectionUtils.isEmpty(list) || list.size() < 10) {
                        continue;
                    }
                    strategyResultService.saveBatch(list);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        getResult(1, StrategyEnum.codeToEnumMap.keySet().stream().toList());
        log.info("策略层级 1 计算胜率 - 结束");
    }

    public void getResult(Integer level, List<String> strategyCodeList) throws ExecutionException, InterruptedException {
        Map<String, StrategyWin> strategyCodeToWinMap = new ConcurrentHashMap<>();
        strategyCodeList.forEach(strategyCode -> strategyCodeToWinMap.put(strategyCode, new StrategyWin(strategyCode)));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<String> part : stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (String stockCode : part) {
                    Map<Integer, StockDetail> idToDetailMap = codeToDetailMap.get(stockCode)
                            .stream().collect(Collectors.toMap(StockDetail::getStockDetailId, item -> item));

                    List<StrategyResult> results = strategyResultService.getStrategyResult(
                            StrategyResult.builder().stockCode(stockCode).level(level).build());
                    for (StrategyResult result : results) {
                        StrategyWin strategyWin = strategyCodeToWinMap.get(result.getStrategyCode());
                        if (Objects.isNull(strategyWin)) {
                            continue;
                        }
                        for (int i = 0; i < result.getStockDetailIdList().size(); i++) {
                            Integer detailId = result.getStockDetailIdList().getInteger(i);
                            strategyWin.addToResult(idToDetailMap.get(detailId));
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        log.info("获取win结果 开始填充数据");
        strategyCodeToWinMap.values().forEach(StrategyWin::fillData);
        List<StrategyWin> resList = strategyCodeToWinMap.values().stream()
                .filter(item -> item.getCnt() > 35)  //不能拉高阈值，很多都是小众的重要条件
                .filter(item -> moreThan(item.getWinRate(), "0.35")).toList();
        strategyWinService.saveBatch(resList);
        log.info("保存win结果 完成");
    }

}
