package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.vo.DetailQueryVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.enums.StrategyEnum.baseStrategys;

@Service
@Slf4j
public class CommonService {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private StrategyL1ServiceImpl strategyL1Service;


    @Resource
    private StrategyTmpServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    public static Map<Integer, Detail> idToDetailMap = new ConcurrentHashMap<>(1048576);
    public static Map<String, List<Detail>> codeToDetailMap = new ConcurrentHashMap<>(4096);
    public static Map<String, String> stockCodeToNameMap;
    public static List<StrategyL1> strategyL1s;
    public static List<List<String>> stockCodePartList;
    public static List<String> stockCodeList;
    public static String calcEndDate;
    public static List<String> predictDateList;

    public static int INIT_DATE_SIZE = 500;


    public static final List<StrategyEnum> strategyL1Enums = new ArrayList<>();
    public static final Map<String, StrategyEnum> l1CodeToEnumMap = new HashMap<>();


    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("初始化开始");
        strategyL1s = strategyL1Service.list();
        strategyL1s.forEach(item -> {
            int[] ids = new int[item.getDetailIds().size()];
            for (int i = 0; i < item.getDetailIds().size(); i++) {
                ids[i] = item.getDetailIds().getIntValue(i);
            }
            item.setDetailIdArr(ids);
            item.fillCodeSet();
        });

        stockCodeList = stockService.list().stream().map(Stock::getCode).toList();
        stockCodePartList = Lists.partition(stockCodeList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        stockCodePartList.forEach(part -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> part.forEach(stockCode -> {
                List<Detail> details = detailService.getStockDetail(new DetailQueryVO(stockCode));
                details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                detailService.genAllStockDetail(details);
                details.forEach(item -> idToDetailMap.put(item.getDetailId(), item));
                codeToDetailMap.put(stockCode, details);
            }), ioThreadPool);
            futures.add(future);
        });
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();


        predictDateList = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().limit(15)
                .map(Detail::getDealDate).toList();
        calcEndDate = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().skip(15)
                .map(Detail::getDealDate).findFirst().orElse("20260201");
        stockCodeToNameMap = stockService.list().stream().collect(Collectors.toMap(Stock::getCode, Stock::getName));


        //填充L1层的基础策略枚举
        List<Integer> tList = List.of(0, 1, 2, 3);
        tList.forEach(t -> {
            List<StrategyEnum> enums = baseStrategys.stream().map(item -> {
                StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
                cur.setCode(0 + item.getCode());
                cur.setDesc("T0-" + item.getDesc());
                cur.setFilterFunc(item.getFilterFunc());
                return cur;
            }).toList();
            strategyL1Enums.addAll(enums);
        });
        for (StrategyEnum strategyEnum : strategyL1Enums) {
            l1CodeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }
        log.info("初始化结束");
    }
}
