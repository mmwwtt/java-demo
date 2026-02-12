package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDAO;
import com.mmwwtt.stock.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private StockDetailServiceImpl stockDetailService;

    @Resource
    private StrategyResultServiceImpl strategyResultService;


    @Resource
    private StrategyWinServiceImpl strategyWinService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor middleThreadPool2 = GlobalThreadPool.getMiddleThreadPool2();
    private final ThreadPoolExecutor middleThreadPool3 = GlobalThreadPool.getMiddleThreadPool3();
    private final ThreadPoolExecutor middleThreadPool4 = GlobalThreadPool.getMiddleThreadPool4();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDAO stockCalcResDao;

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
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), quantityMult));
                        }
                        if (moreThan(stockDetail.getPricePert(), "0.097")
                                || !Objects.equals(stockDetail.getDealDate(), curDate)
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
            List<StrategyWin> list = strategyToStockMap.keySet().stream().sorted(Comparator.comparing(StrategyWin::getOnePercRate).reversed()).toList();
            for (StrategyWin strategyWin : list) {
                List<String> resStockList = strategyToStockMap.get(strategyWin);
                String str = String.format("\n\n策略id:%d \n胜率:%4f \n历史总数：%d\n明天平均涨幅:%4f  \n十日最高平均涨幅：%4f \n策略：%s \n",
                        strategyWin.getStrategyWinId(),strategyWin.getWinRate(), strategyWin.getCnt(),strategyWin.getOnePercRate(),
                        strategyWin.getTenMaxPercRate(), strategyWin.getStrategyName());
                fos.write(str.getBytes());
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



    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        List<StrategyEnum> values =StrategyEnum.strategyList;
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
        getResult(1, now, StrategyEnum.codeToEnumMap.keySet().stream().toList());
        log.info("策略层级 1 计算胜率 - 结束");
    }

    public void getResult(Integer level, LocalDateTime now, List<String> strategyCodeList) throws ExecutionException, InterruptedException {
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
                .filter(item -> item.getCnt() > 100)
                .filter(item -> moreThan(item.getWinRate(), "0.35")).toList();
        strategyWinService.saveBatch(resList);
        log.info("保存win结果 完成");
    }
}
