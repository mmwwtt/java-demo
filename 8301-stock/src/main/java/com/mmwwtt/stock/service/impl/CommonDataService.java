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
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

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
    public static List<StrategyL1> strategyL1s = Collections.synchronizedList(new ArrayList<>(1000));
    public static Map<String, StrategyL1> codeToL1Map = new ConcurrentHashMap<>(1000);
    public static List<List<String>> stockCodePartList = new ArrayList<>();
    public static List<String> stockCodeList = new ArrayList<>();
    public static String calcEndDate;
    public static List<String> predictDateList = new ArrayList<>();

    public static int INIT_DATE_SIZE = 500;


    public static final List<StrategyEnum> strategyL1Enums = Collections.synchronizedList(new ArrayList<>(1000));
    public static final Map<String, StrategyEnum> l1CodeToEnumMap = new ConcurrentHashMap<>(1000);


    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("初始化开始");

        idToDetailMap = new ConcurrentHashMap<>(1048576);
        codeToDetailMap = new ConcurrentHashMap<>(4096);
        strategyL1s = Collections.synchronizedList(new ArrayList<>(1000));
        codeToL1Map = new ConcurrentHashMap<>(1000);
        stockCodePartList = new ArrayList<>();
        stockCodeList = new ArrayList<>();
        predictDateList = new ArrayList<>();

        log.info("开始补充L1策略枚举");
        String basePrefix = "320";
        List<Triple<String, Function<Detail,Double>,String>> triples = new ArrayList<>();
        triples.add(Triple.of("dif",Detail::getDif, ""));
        triples.add(Triple.of("dea",Detail::getDea, ""));
        triples.add(Triple.of("macd",Detail::getMacd, ""));
        triples.add(Triple.of("wr", Detail::getWr, ""));
        triples.add(Triple.of("上影线占比",Detail::getUpShadowPert, ""));
        triples.add(Triple.of("上影线长度",Detail::getUpShadowLen, ""));
        triples.add(Triple.of("下影线百分比",Detail::getDownShadowPert, ""));
        triples.add(Triple.of("下引线长度",Detail::getDownShadowLen, ""));
        triples.add(Triple.of("总长",Detail::getAllLen, ""));

        triples.add(Triple.of("rsi",Detail::getRsi, ""));
        triples.add(Triple.of("bias5",Detail::getBias5, ""));
        triples.add(Triple.of("bias20",Detail::getBias20, ""));
        triples.add(Triple.of("布林带",Detail::getBollPosition, ""));
        triples.add(Triple.of("20均线波动率",Detail::getMa20Slope, ""));
        triples.add(Triple.of("20均线斜率",Detail::getVolatility20, ""));
        triples.add(Triple.of("ATR14波动率",Detail::getLastPrice, ""));

        //demo
        //new StrategyEnum("10000", "区间60_0_20", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.0, 0.2)),
        for (Triple<String, Function<Detail, Double>, String> triple : triples) {
            String name = triple.getLeft();
            Function<Detail, Double> getter = triple.getMiddle();

        }
        List<Integer> tList = List.of(0, 1, 2, 3);
        tList.forEach(t -> {
            List<StrategyEnum> enums = baseStrategys.stream().map(item -> {
                StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
                cur.setCode(t + item.getCode());
                cur.setDesc(String.format("T%s-%s", t, item.getDesc()));
                cur.setFilterFunc(item.getFilterFunc());
                if (Objects.nonNull(item.getType())) {
                    cur.setType(String.format("T%s-%s", t, item.getType()));
                }
                return cur;
            }).toList();
            strategyL1Enums.addAll(enums);
        });
        for (StrategyEnum strategyEnum : strategyL1Enums) {
            l1CodeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }

        log.info("开始加载L1层策略");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Integer> l1IdList = strategyL1Service.getIdList();
        for (Integer l1Id : l1IdList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                StrategyL1 strategyL1 = strategyL1Service.getById(l1Id);
                int[] ids = new int[strategyL1.getDetailIds().size()];
                for (int i = 0; i < strategyL1.getDetailIds().size(); i++) {
                    ids[i] = strategyL1.getDetailIds().getIntValue(i);
                }
                ids = Arrays.stream(ids).sorted().toArray();
                strategyL1.setDetailIdArr(ids);
                strategyL1.fillCodeSet();
                strategyL1s.add(strategyL1);
                codeToL1Map.put(strategyL1.getStrategyCode(), strategyL1);
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();


        log.info("开始查询股票列表");
        stockCodeList = stockService.list().stream().map(Stock::getCode).toList();
        stockCodePartList = Lists.partition(stockCodeList, 50);

        futures = new ArrayList<>();
        log.info("开始查询详情列表");
        for (List<String> part : stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> part.forEach(stockCode -> {
                List<Detail> details = detailService.getBySql(String.format("stock_code='%s'", stockCode));
                details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                detailService.genAllDetail(details);
                details.forEach(item -> idToDetailMap.put(item.getDetailId(), item));
                codeToDetailMap.put(stockCode, details);
                details.removeIf(detail -> Objects.isNull(detail) || Objects.isNull(detail.getT5())
                        || Objects.isNull(detail.getT5().getSixtyDayLine())
                        || Objects.isNull(detail.getNext1())
                        || moreThan(detail.getRise0(), 0.097));
            }), ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        log.info("开始设置Dfs截止日期");
        predictDateList = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().limit(15)
                .map(Detail::getDealDate).toList();
        calcEndDate = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().skip(15)
                .map(Detail::getDealDate).findFirst().orElse("20260201");
        log.info("初始化结束");
    }
}
