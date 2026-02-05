package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.Constants;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUitls;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDAO;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.impl.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Slf4j
@SpringBootTest
public class CalcTest {

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

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDAO stockCalcResDao;

    private final VoConvert voConvert = VoConvert.INSTANCE;


    @Test
    @DisplayName("根据所有策略计算胜率")
    public void startCalc2() throws ExecutionException, InterruptedException {
        calcByStrategy(Constants.STRATEGY_LIST);
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        StockStrategy strategy = new StockStrategy("test_" + getTimeStr(), (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return isInRange(t0.getLowPrice(), subtract(t1.getLowPrice(), "0.02"), add(t1.getLowPrice(), "0.02"))
                    && isInRange(t0.getLowPrice(), subtract(t2.getLowPrice(), "0.02"), add(t2.getLowPrice(), "0.02"))
                    && lessThan(t0.getPosition20(), "0.2")
                    && moreThan(t0.getPricePert().abs(), "0.01")
                    && moreThan(t1.getPricePert().abs(), "0.01")
                    && moreThan(t2.getPricePert().abs(), "0.01");
        });
        Map<String, List<StockDetail>> resMap = calcByStrategy(List.of(strategy));
        resMap.forEach((strategyName, resList) -> {
            resList.stream().filter(item -> item.getNext1().getIsDown()).limit(200).forEach(item -> {
                try {
                    StockGuiUitls.genDetailImage(item, strategyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        log.info("绘制完成");
    }


    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        calcByStrategy(List.of(

                new StockStrategy("test ", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getLowPrice(), subtract(t1.getLowPrice(), "0.02"), add(t1.getLowPrice(), "0.02"))
                            && isInRange(t0.getLowPrice(), subtract(t2.getLowPrice(), "0.02"), add(t2.getLowPrice(), "0.02"))
                            && lessThan(t0.getPosition20(), "0.2");
                })
        ));
    }


    @Test
    @DisplayName("测试单个策略")
    public void startCalc5() throws ExecutionException, InterruptedException {
        StockStrategy strategy = Constants.getStrategy("");
        calcByStrategy(List.of(strategy));
    }


    @Test
    @DisplayName("测试策略-大类")
    public void startCalc6() throws ExecutionException, InterruptedException {
        List<StockStrategy> strategyList = Constants.getStrategyList("上升缺口");
        calcByStrategy(strategyList);
    }


    @Test
    @DisplayName("根据策略预测")
    public void getStockByStrategy() throws InterruptedException, ExecutionException {
        calcByStrategy(Constants.STRATEGY_LIST);

        String curDate = "20260205";
        boolean isOnTime = true;
        Map<String, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        List<Stock> stockList = stockService.getAllStock();
        Map<String, StockDetail> codeToDetailMap = stockDetailService.getCodeToTodayDetailMap();

        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        QueryWrapper<StockCalcRes> detailWapper = new QueryWrapper<>();
        detailWapper.last("where type = 1" +
                " and create_date = (select max(create_date) from stock_calculation_result_t where type = '1')" +
                " and win_rate > 0.6" +
                " order by win_rate desc;");
        Map<String, String> strategyMap =
                stockCalcResDao.selectList(detailWapper).stream()
                        .collect(Collectors.toMap(StockCalcRes::getStrategyDesc, item -> item.getWinRate().toString(), (key1, key2) -> key2));
        for (String strategyName : strategyMap.keySet()) {
            Function<StockDetail, Boolean> runFunc = Constants.getStrategy(strategyName).getRunFunc();
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
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), "2"));
                        }
                        if (moreThan(stockDetail.getPricePert(), "0.097") || !Objects.equals(stockDetail.getDealDate(), curDate)
                        || Objects.isNull(stockDetail.getSixtyDayLine())) {
                            continue;
                        }
                        if (runFunc.apply(stockDetail)) {
                            strategyToStockMap.computeIfAbsent(strategyName, k -> new ArrayList<>())
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
            for (Map.Entry<String, List<String>> entry : strategyToStockMap.entrySet()) {
                fos.write(("\n\n" + entry.getKey() + "   " + strategyMap.get(entry.getKey()) + "\n").getBytes());
                for (String s : entry.getValue()) {
                    fos.write((s + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }

    }

    public Map<String, List<StockDetail>> calcByStrategy(List<StockStrategy> strategyList) throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        LocalDateTime dataTime = LocalDateTime.now();
        List<List<Stock>> parts = stockService.getStockPart();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = stockDetailService.getStockDetail(stock.getCode(), null);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StockStrategy strategy : strategyList) {
                        for (int i = 0; i < stockDetails.size() - 60; i++) {
                            StockDetail stockDetail = stockDetails.get(i);
                            if (moreThan(stockDetail.getPricePert(), "0.097")
                                    || Objects.isNull(stockDetail.getNext1())
                                    || !strategy.getRunFunc().apply(stockDetail)) {
                                continue;
                            }
                            strategyToCalcMap.computeIfAbsent(strategy.getStrategyName(), v -> new ArrayList<>()).add(stockDetail);
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        strategyToCalcMap.forEach((strategyName, list) -> {
            stockCalcResService.saveCalcRes(list, strategyName, dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());
        });
        log.info("结束计算");
        return strategyToCalcMap;
    }


    @Test
    @DisplayName("生成符合单条枚举策略的数据")
    public void buildStrateResultLevel1() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        strategyResultService.remove(new QueryWrapper<>());
        StrategyEnum[] values = StrategyEnum.values();

        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            for (Stock stock : part) {
                List<StockDetail> stockDetails = stockDetailService.getStockDetail(stock.getCode(), null);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    List<StrategyResult> list = new ArrayList<>();
                    for (StrategyEnum strategy : values) {
                        List<Long> dateList = new ArrayList<>();
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
                    if (CollectionUtils.isEmpty(list)) {
                        return;
                    }
                    strategyResultService.saveBatch(list);
                }, ioThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        getResult(1, now);
    }

    @Test
    @DisplayName("生成符合单条枚举策略的数据，组合策略")
    public void buildStrateResultData() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        StrategyEnum[] values = StrategyEnum.values();

        buildDataByUnion(now, 2);
        //buildData3(now, 3);
    }

    public void buildDataByUnion(LocalDateTime now, Integer level) throws ExecutionException, InterruptedException {
        QueryWrapper<StrategyWin> delWapper = new QueryWrapper<>();
        delWapper.eq("level", level);
        strategyWinService.remove(delWapper);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<StrategyWin> level1WinList = strategyWinService.getStrategyWin(StrategyWin.builder().level(1).build());
        List<StrategyWin> winList = strategyWinService.getStrategyWin(StrategyWin.builder().level(level - 1).build());
        for (StrategyWin win : winList) {
            if (win.getCnt() < 100) {
                continue;
            }
 //           CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (StrategyWin level1Win : level1WinList) {
                    if (win.getStrategyCode().contains(level1Win.getStrategyCode())) {
                        continue;
                    }
                    String strategCode = win.getStrategyCode() + " " + level1Win.getStrategyCode();
                    List<StrategyResult> strategyResultList = new ArrayList<>();
                    Map<String, Set<String>> winStockCodeToDateMap = strategyResultService.getStockCodeToDateMap(win.getStrategyCode());
                    Map<String, Set<String>> level1WinStockCodeToDateMap = strategyResultService.getStockCodeToDateMap(level1Win.getStrategyCode());
                    int count = 0;
                    StrategyWin nowWin = new StrategyWin(strategCode, now);
                    //筛选多条件都符合的数据
                    for (String stockCode : winStockCodeToDateMap.keySet()) {
                        if (!level1WinStockCodeToDateMap.containsKey(stockCode)) {
                            continue;
                        }
                        Set<String> dateSet = SetUtils.intersection(winStockCodeToDateMap.get(stockCode), level1WinStockCodeToDateMap.get(stockCode));

                        if (dateSet.isEmpty()) {
                            continue;
                        }
                        count = count + dateSet.size();
                        StrategyResult strategyResult = new StrategyResult(level, strategCode, stockCode, String.join(" ", dateSet), now);
                        strategyResultList.add(strategyResult);
                    }
                    if (count >= 100) {
                        strategyResultService.saveBatch(strategyResultList);
                    }
                }
//            }, ioThreadPool);
//            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        getResult(level, now);
    }


    private void getResult(Integer level, LocalDateTime now) throws ExecutionException, InterruptedException {
        QueryWrapper<StrategyWin> delWapper = new QueryWrapper<>();
        delWapper.eq("level", level);
        strategyWinService.remove(delWapper);


        List<String> strategyCodeList = strategyResultService.getStrategyCodeByLevel(level);
        Map<String, StrategyWin> strategyCodeToWinMap = new ConcurrentHashMap<>();
        strategyCodeList.forEach(strategyCode -> strategyCodeToWinMap.put(strategyCode, new StrategyWin(strategyCode, now)));
        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<Long, StockDetail> idToDetailMap = stockDetailService.getStockDetail(stock.getCode(), null)
                            .stream().collect(Collectors.toMap(StockDetail::getStockDetailId, item -> item));

                    List<StrategyResult> results =
                            strategyResultService.getStrategyResult(StrategyResult.builder().stockCode(stock.getCode()).build());
                    for (StrategyResult result : results) {
                        StrategyWin strategyWin = strategyCodeToWinMap.get(result.getStrategyCode());
                        Arrays.stream(result.getStockDetailIdList().split(" "))
                                .forEach(item -> strategyWin.addToResult(idToDetailMap.get(Long.valueOf(item))));
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        strategyCodeToWinMap.values().forEach(StrategyWin::fillData);
        strategyWinService.saveBatch(strategyCodeToWinMap.values().stream().toList());
    }
}