package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.enums.StrategyEnum;
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

import static com.mmwwtt.stock.common.CommonUtils.moreThan;
import static com.mmwwtt.stock.enums.StrategyEnum.baseStrategys;

@Service
@Slf4j
public class CommonDataService {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private StrategyL1ServiceImpl strategyL1Service;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    public static Map<Integer, Detail> idToDetailMap = new ConcurrentHashMap<>(1048576);
    public static Map<String, List<Detail>> codeToDetailMap = new ConcurrentHashMap<>(4096);
    public static Map<String, String> stockCodeToNameMap;
    public static List<StrategyL1> strategyL1s = Collections.synchronizedList(new ArrayList<>(1000));
    public static List<List<String>> stockCodePartList;
    public static List<String> stockCodeList;
    public static String calcEndDate;
    public static List<String> predictDateList;

    public static int INIT_DATE_SIZE = 500;


    public static final List<StrategyEnum> strategyL1Enums = Collections.synchronizedList(new ArrayList<>(1000));
    public static final Map<String, StrategyEnum> l1CodeToEnumMap = new ConcurrentHashMap<>(1000);


    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("初始化开始");

        log.info("开始补充L1策略枚举");
        List<Integer> tList = List.of(0, 1, 2, 3);
        tList.forEach(t -> {
            List<StrategyEnum> enums = baseStrategys.stream().map(item -> {
                StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
                cur.setCode(t + item.getCode());
                cur.setDesc(String.format("T%s-%s", t, item.getDesc()));
                cur.setType(String.format("T%s-%s", t, item.getType()));
                cur.setFilterFunc(item.getFilterFunc());
                return cur;
            }).toList();
            strategyL1Enums.addAll(enums);
        });
        for (StrategyEnum strategyEnum : strategyL1Enums) {
            l1CodeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }

        log.info("开始加载L1层策略");
        List<Integer> l1IdList = strategyL1Service.getIdList();
        l1IdList.parallelStream().forEach(l1Id -> {
            StrategyL1 strategyL1 = strategyL1Service.getById(l1Id);
            int[] ids = new int[strategyL1.getDetailIds().size()];
            for (int i = 0; i < strategyL1.getDetailIds().size(); i++) {
                ids[i] = strategyL1.getDetailIds().getIntValue(i);
            }
            ids= Arrays.stream(ids).sorted().toArray();
            strategyL1.setDetailIdArr(ids);
            strategyL1.fillCodeSet();
            strategyL1s.add(strategyL1);
        });


        log.info("开始查询股票列表");
        stockCodeList = stockService.list().stream().map(Stock::getCode).toList();
        stockCodePartList = Lists.partition(stockCodeList, 50);

        log.info("开始查询详情列表");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        stockCodePartList.forEach(part -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> part.forEach(stockCode -> {
                List<Detail> details = detailService.getBySql(String.format("stock_code='%s'", stockCode));
                details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                detailService.genAllDetail(details);
                details.forEach(item -> idToDetailMap.put(item.getDetailId(), item));
                codeToDetailMap.put(stockCode, details);
                details.removeIf(detail -> Objects.isNull(detail) || Objects.isNull(detail.getT5())
                        || Objects.isNull(detail.getT5().getSixtyDayLine())
                        || Objects.isNull(detail.getNext1())
                        || moreThan(detail.getPricePert(), 0.097));
            }), ioThreadPool);
            futures.add(future);
        });
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        log.info("开始设置Dfs截止日期");
        predictDateList = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().limit(15)
                .map(Detail::getDealDate).toList();
        calcEndDate = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().skip(15)
                .map(Detail::getDealDate).findFirst().orElse("20260201");
        stockCodeToNameMap = stockService.list().stream().collect(Collectors.toMap(Stock::getCode, Stock::getName));
        log.info("初始化结束");
    }
}
