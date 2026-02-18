package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
@Slf4j
public class CalcCommonService {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockDetailServiceImpl stockDetailService;

    @Autowired
    private StrategyResultServiceImpl strategyResultService;


    @Autowired
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor middleThreadPool2 = GlobalThreadPool.getMiddleThreadPool2();
    private final ThreadPoolExecutor middleThreadPool3 = GlobalThreadPool.getMiddleThreadPool3();
    private final ThreadPoolExecutor middleThreadPool4 = GlobalThreadPool.getMiddleThreadPool4();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();


    private final VoConvert voConvert = VoConvert.INSTANCE;


    /**
     * 预测明日股票
     */
    public void predict(String curDate, boolean isOnTime, double quantityMult) throws InterruptedException, ExecutionException {
        Map<String, StrategyEnum> codeToEnumMap = StrategyEnum.codeToEnumMap;
        List<StrategyWin> strategyWinList = getStrategyWinList();
        strategyWinList.sort(Comparator.comparing(StrategyWin::getWinRate).reversed());

        Map<StrategyWin, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        List<Stock> stockList = stockService.getAllStock();
        Map<String, StockDetail> codeToDetailMap = stockDetailService.getCodeToTodayDetailMap();

        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");

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
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), quantityMult));
                        }
                        if (moreThan(stockDetail.getPricePert(), "0.097")
                                || !Objects.equals(stockDetail.getDealDate(), curDate)
                                || Objects.isNull(stockDetail.getSixtyDayLine())) {
                            continue;
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(stockDetail));
                        if (res) {
                            try {
                                strategyToStockMap.computeIfAbsent(strategyWin, k -> new ArrayList<>())
                                        .add(stock.getCode() + "_" + stock.getName() + "_" + stockDetail.getPricePert().doubleValue());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, cpuThreadPool);
                futures.add(future);
            }
        }

        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        filterPriduct(strategyToStockMap);
        log.info("计算结束");
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<StrategyWin> list = strategyToStockMap.keySet().stream().sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
            for (StrategyWin strategyWin : list) {
                List<String> resStockList = strategyToStockMap.get(strategyWin);
                String str = String.format("\n\n策略id:%d \n胜率:%4f \n历史总数：%d\n明天平均涨幅:%4f  \n5日最高平均涨幅：%4f \n策略：%s \n",
                        strategyWin.getStrategyWinId(), strategyWin.getWinRate(), strategyWin.getCnt(), strategyWin.getOnePercRate(),
                        strategyWin.getFiveMaxPercRate(), strategyWin.getStrategyName());
                fos.write(str.getBytes());
                for (String s : resStockList) {
                    fos.write((s + "\n").getBytes());
                }
            }

        } catch (IOException ignored) {
        }

    }

    private void filterPriduct(Map<StrategyWin, List<String>> map) {
        Set<String> stockSet = new HashSet<>();
        map.forEach((k, v) -> {
            v.removeIf(stockSet::contains);
            stockSet.addAll(v);
        });
        map.keySet().forEach(k -> {
            if (CollectionUtils.isEmpty(map.get(k))
                    || lessThan(k.getFiveMaxPercRate(), "0.07")) {
                map.remove(k);
            }
        });
    }

    private List<StrategyWin> getStrategyWinList() {
        StrategyWin strategyWin = new StrategyWin();
        strategyWin.setFiveMaxPercRate(new BigDecimal("0.12"));
        return strategyWinService.getStrategyWin(strategyWin);
    }


    public Map<String, Map<StrategyWin, List<StockDetail>>> verifyPredictRes() throws ExecutionException, InterruptedException {
        Map<String, Map<StrategyWin, List<StockDetail>>> dateMap = new HashMap<>();
        List<StockDetail> stockDetails = CommonService.codeToDetailMap.get("000001.SZ").stream().limit(20).toList();
        List<String> dateList = stockDetails.stream().map(StockDetail::getDealDate).toList();
        for (String date : dateList) {
            Map<StrategyWin, List<StockDetail>> resMap = verifyPredictRes(date);
            dateMap.put(date, resMap);
        }
        return dateMap;
    }

    /**
     * 验证预测的股票结果
     */
    private Map<StrategyWin, List<StockDetail>> verifyPredictRes(String curDate) throws InterruptedException, ExecutionException {
        Map<String, StrategyEnum> codeToEnumMap = StrategyEnum.codeToEnumMap;
        List<StrategyWin> strategyWinList = getStrategyWinList();
        strategyWinList.sort(Comparator.comparing(StrategyWin::getWinRate).reversed());

        Map<StrategyWin, List<StockDetail>> strategyToStockMap = new ConcurrentHashMap<>();
        Map<String, StockDetail> codeToDetailMap = CommonService.idToDetailMap.values().stream()
                .filter(item -> Objects.equals(item.getDealDate(), curDate))
                .collect(Collectors.toMap(StockDetail::getStockCode, item -> item));


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");

        for (StrategyWin strategyWin : strategyWinList) {
            List<Function<StockDetail, Boolean>> functionList = Arrays.stream(strategyWin.getStrategyCode().split(" "))
                    .map(item -> codeToEnumMap.get(item).getRunFunc()).toList();
            for (List<String> part : CommonService.stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        StockDetail stockDetail = codeToDetailMap.get(stockCode);
                        if (Objects.isNull(stockDetail) || Objects.isNull(stockDetail.getT1())
                                || Objects.isNull(stockDetail.getNext1())
                                || Objects.isNull(stockDetail.getT2()) || Objects.isNull(stockDetail.getT3())
                                || Objects.isNull(stockDetail.getT4()) || Objects.isNull(stockDetail.getT5())
                                || Objects.isNull(stockDetail.getT5().getSixtyDayLine())) {
                            continue;
                        }
                        if (moreThan(stockDetail.getPricePert(), 0.097)
                                || !Objects.equals(stockDetail.getDealDate(), curDate)
                                || Objects.isNull(stockDetail.getSixtyDayLine())) {
                            continue;
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(stockDetail));
                        if (res) {
                            strategyToStockMap.computeIfAbsent(strategyWin, k -> new ArrayList<>())
                                    .add(stockDetail);
                        }
                    }
                }, ioThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        filterPriduct2(strategyToStockMap);
        return strategyToStockMap;
    }

    private void filterPriduct2(Map<StrategyWin, List<StockDetail>> map) {
        Set<String> stockSet = new HashSet<>();
        map.forEach((k, v) -> {
            v.removeIf(item -> stockSet.contains(item.getStockCode()));
            v.forEach(item -> stockSet.add(item.getStockCode()));
        });
        map.keySet().forEach(k -> {
            if (CollectionUtils.isEmpty(map.get(k))
                    || lessThan(k.getFiveMaxPercRate(), "0.07")) {
                map.remove(k);
            }
        });
    }

}
