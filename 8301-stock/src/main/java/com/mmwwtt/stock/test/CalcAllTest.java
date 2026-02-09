package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDAO;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.impl.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Slf4j
@SpringBootTest
public class CalcAllTest {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private StockDetailServiceImpl stockDetailService;

    @Resource
    private StrategyResultServiceImpl strategyResultService;

    @Resource
    private StockCalcResServiceImpl stockCalcResService;

    @Resource
    private StrategyWinServiceImpl strategyWinService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor middleThreadPool = GlobalThreadPool.getMiddleThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDAO stockCalcResDao;

    private final VoConvert voConvert = VoConvert.INSTANCE;

    @Test
    @DisplayName("根据策略预测")
    public void getStockByStrategy() throws InterruptedException, ExecutionException {
        Map<String, StrategyEnum> codeToEnumMap = StrategyEnum.codeToEnumMap;
        List<StrategyWin> strategyWinList = getStrategyWinList();
        strategyWinList.sort(Comparator.comparing(StrategyWin::getWinRate).reversed());

        String curDate = "20260209";
        boolean isOnTime = true;
        Map<StrategyWin, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        List<Stock> stockList = stockService.getAllStock();
        Map<String, StockDetail> codeToDetailMap = stockDetailService.getCodeToTodayDetailMap();

        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        QueryWrapper<StockCalcRes> detailWapper = new QueryWrapper<>();

        for (StrategyWin strategyWin : strategyWinList) {
            List<Function<StockDetail, Boolean>> functionList = Arrays.stream(strategyWin.getStrategyCode().split(" "))
                    .map(item -> codeToEnumMap.get(item).getRunFunc()).toList();
            for (List<Stock> part : parts) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (Stock stock : part) {
                        StockDetail stockDetail = codeToDetailMap.get(stock.getCode());
                        if (Objects.isNull(stockDetail) || Objects.isNull(stockDetail.getT1())
                                || Objects.isNull(stockDetail.getT2()) || Objects.isNull(stockDetail.getT3())
                                || Objects.isNull(stockDetail.getT4()) || Objects.isNull(stockDetail.getT5())) {
                            continue;
                        }
                        if (isOnTime) {
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), "1.3"));
                        }
                        if (moreThan(stockDetail.getPricePert(), "0.097") || !Objects.equals(stockDetail.getDealDate(), curDate)
                                || Objects.isNull(stockDetail.getSixtyDayLine())) {
                            continue;
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(stockDetail));
                        if (res) {
                            strategyToStockMap.computeIfAbsent(strategyWin, k -> new ArrayList<>())
                                    .add(stock.getCode() + "_" + stock.getName() + "_" + stockDetail.getPricePert().doubleValue());
                        }
                    }
                }, cpuThreadPool);
                futures.add(future);
            }
        }

        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算结束");
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<StrategyWin> list = strategyToStockMap.keySet().stream().sorted(Comparator.comparing(StrategyWin::getWinRate).reversed()).toList();
            for (StrategyWin strategyWin : list) {
                List<String> resStockList = strategyToStockMap.get(strategyWin);
                fos.write(("\n\n" + strategyWin.getWinRate().toString() + "    " + strategyWin.getStrategyName() + "\n").getBytes());
                for (String s : resStockList) {
                    fos.write((s + "\n").getBytes());
                }
            }

        } catch (IOException ignored) {
        }

    }

    private List<StrategyWin> getStrategyWinList() {
        StrategyWin strategyWin = new StrategyWin();
        strategyWin.setWinRate(new BigDecimal("0.85"));
        return strategyWinService.getStrategyWin(strategyWin);
    }

    @Test
    @DisplayName("生成符合单条枚举策略的数据")
    public void buildStrateResultAll() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        buildStrateResultLevel1();
        Map<String, Map<String, Set<Integer>>> level1StrategyToStockAndDateSetMap =
                strategyResultService.getLevel1StrategyToStockAndDateSetMap();
        buildDataByUnion(now, 2, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 3, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 4, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 5, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 6, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 7, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 8, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 9, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 10, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 11, level1StrategyToStockAndDateSetMap);
    }

    @Test
    @DisplayName("生成符合单条枚举策略的数据")
    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        StrategyEnum[] values = StrategyEnum.values();
        LocalDateTime now = LocalDateTime.now();
        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            for (Stock stock : part) {
                List<StockDetail> stockDetails = stockDetailService.getStockDetail(stock.getCode(), null);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    List<StrategyResult> list = new ArrayList<>();
                    for (StrategyEnum strategy : values) {
                        Set<Integer> dateList = new HashSet<>();
                        for (StockDetail detail : stockDetails) {
                            if (Objects.isNull(detail.getNext1()) || Objects.isNull(detail.getSixtyDayLine())
                                    || moreThan(detail.getPricePert(), "0.097")) {
                                continue;
                            }
                            if (strategy.getRunFunc().apply(detail)) {
                                dateList.add(detail.getStockDetailId());
                            }
                        }
                        if (!CollectionUtils.isEmpty(dateList)) {
                            StrategyResult strategyResult = new StrategyResult(1, strategy.getCode(),
                                    stock.getCode(), dateList, now);
                            list.add(strategyResult);
                        }
                    }
                    if (CollectionUtils.isEmpty(list) || list.size() < 10) {
                        return;
                    }
                    strategyResultService.saveBatch(list);
                }, ioThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        getResult(1, now, Arrays.stream(StrategyEnum.values()).map(StrategyEnum::getCode).toList());
        log.info("策略层级 1 计算胜率 - 结束");
    }

    @Test
    @DisplayName("生成符合单条枚举策略的数据，组合策略")
    public void buildStrateResultData1() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Map<String, Set<Integer>>> level1StrategyToStockAndDateSetMap =
                strategyResultService.getLevel1StrategyToStockAndDateSetMap();
        buildDataByUnion(now, 9, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 10, level1StrategyToStockAndDateSetMap);
        buildDataByUnion(now, 11, level1StrategyToStockAndDateSetMap);
    }


    public void buildDataByUnion(LocalDateTime now, Integer level,
                                 Map<String, Map<String, Set<Integer>>> level1StrategyToStockAndDateSetMap)
            throws ExecutionException, InterruptedException {

        boolean[] detailIdToNext1IsUpMap = getDetailIdToNext1IsUpArr();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<StrategyWin> level1WinList = strategyWinService.getStrategyWin(StrategyWin.builder().level(1).build());
        List<StrategyWin> winList = strategyWinService.getStrategyWin(StrategyWin.builder().level(level - 1).build());

        Set<String> strategyCodeSet = ConcurrentHashMap.newKeySet();
        Set<String> strategyCodeHaveDateSet = ConcurrentHashMap.newKeySet();
        for (StrategyWin win : winList) {
            if (win.getCnt() < 100 || moreThan(win.getWinRate(), "0.975")) {
                continue;
            }
            Map<String, Set<Integer>> winStockCodeToDateMap = strategyResultService.getStockCodeToDateMap(win.getStrategyCode());
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (StrategyWin level1Win : level1WinList) {
                    if (win.getStrategyCode().contains(level1Win.getStrategyCode())) {
                        continue;
                    }
                    String strategyCode = addStrategyCode(win.getStrategyCode(), level1Win.getStrategyCode());
                    if (strategyCodeSet.contains(strategyCode)) {
                        continue;
                    }
                    strategyCodeSet.add(strategyCode);
                    List<StrategyResult> strategyResultList = new ArrayList<>();
                    Map<String, Set<Integer>> level1WinStockCodeToDateMap =
                            level1StrategyToStockAndDateSetMap.getOrDefault(level1Win.getStrategyCode(), Collections.emptyMap());
                    AtomicInteger count = new AtomicInteger();
                    AtomicInteger winCount = new AtomicInteger();

                    //筛选符合条件的数据
                    winStockCodeToDateMap.forEach((stockCode, dateSet) -> {
                        Set<Integer> detailIdSet = Sets.intersection(dateSet, level1WinStockCodeToDateMap.getOrDefault(stockCode, Collections.emptySet()));
                        if (detailIdSet.isEmpty()) {
                            return;
                        }
                        for (Integer detailId : detailIdSet) {
                            if (detailIdToNext1IsUpMap[detailId]) {
                                winCount.getAndIncrement();
                            }
                        }
                        count.addAndGet(detailIdSet.size());
                        StrategyResult strategyResult = new StrategyResult(level, strategyCode, stockCode, detailIdSet, now);
                        strategyResultList.add(strategyResult);
                    });

                    //符合条件的数据过少/胜率比组合前的两个都低 则不保存
                    if (count.get() < 100) {
                        continue;
                    }

                    BigDecimal winRate = divide(winCount.get(), count.get());
                    if (lessThan(winRate, win.getWinRate()) || lessThan(winRate, level1Win.getWinRate())) {
                        continue;
                    }
                    strategyCodeHaveDateSet.add(strategyCode);
                    strategyResultService.saveBatch(strategyResultList, 100);
                }
//                log.info("层级{} 策略{} 计算结束", level, win.getStrategyCode());
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        getResult(level, now, new ArrayList<>(strategyCodeHaveDateSet));
        log.info("策略层级 {} 计算胜率 - 结束", level);
    }

    private void getResult(Integer level, LocalDateTime now, List<String> strategyCodeList) throws ExecutionException, InterruptedException {
        Map<String, StrategyWin> strategyCodeToWinMap = new ConcurrentHashMap<>();
        strategyCodeList.forEach(strategyCode -> strategyCodeToWinMap.put(strategyCode, new StrategyWin(strategyCode, now)));
        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<Integer, StockDetail> idToDetailMap = stockDetailService.getStockDetail(stock.getCode(), null)
                            .stream().collect(Collectors.toMap(StockDetail::getStockDetailId, item -> item));

                    List<StrategyResult> results =
                            strategyResultService.getStrategyResult(
                                    StrategyResult.builder().stockCode(stock.getCode()).level(level).build());
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
                .filter(item -> item.getCnt() > 50)
                .filter(item -> moreThan(item.getWinRate(), "0.35")).toList();
        strategyWinService.saveBatch(resList);
        log.info("保存win结果 完成");
    }

    public boolean[] getDetailIdToNext1IsUpArr() throws ExecutionException, InterruptedException {
        boolean[] detailIdToNext1IsUpArr = new boolean[1000000];
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = stockService.getStockPart();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    stockDetailService.getStockDetail(stock.getCode(), null).forEach(detail -> {
                        if (Objects.nonNull(detail.getNext1())) {
                            detailIdToNext1IsUpArr[detail.getStockDetailId()] = detail.getNext1().getIsUp();
                        }
                    });

                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allTask.get();
        return detailIdToNext1IsUpArr;
    }

    public String addStrategyCode(String strategyCode1, String strategyCode2) {
        List<String> list1 = new ArrayList<>(Arrays.stream(strategyCode1.split(" ")).toList());
        List<String> list2 = new ArrayList<>(Arrays.stream(strategyCode2.split(" ")).toList());
        list1.addAll(list2);
        return list1.stream().distinct().sorted().collect(Collectors.joining(" "));
    }
}

