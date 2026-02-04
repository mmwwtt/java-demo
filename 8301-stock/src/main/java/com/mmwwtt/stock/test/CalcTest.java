package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.Constants;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.dao.StrategyResultDao;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockDetailDao stockDetailDao;

    @Autowired
    private StrategyResultDao strategyResultDao;

    @Resource
    private StockService stockService;

    @Resource
    private StockDetailService stockDetailService;

    @Resource
    private StrategyResultService strategyResultService;

    @Resource
    private StockCalcResService stockCalcResService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDao stockCalcResDao;

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
        Map<String, List<StockDetail>> resMap =calcByStrategy(List.of(strategy));
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

        String curDate = "20260130";
        boolean isOnTime = false;
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
                        if (moreThan(stockDetail.getPricePert(), "0.097") || !Objects.equals(stockDetail.getDealDate(), curDate)) {
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


    @Test
    @DisplayName("根据百分比区间预测明日会上涨")
    public void getStockByInterval() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        //百分比策略
        for (int i = 0; i < 4; i++) {
            List<Stock> resList = new ArrayList<>();
            QueryWrapper<StockCalcRes> calcWapper = new QueryWrapper<>();
            calcWapper.apply(" create_date = (select max(create_date) from stock_calculation_result_t where type = '0')")
                    .eq("type", StockCalcRes.TypeEnum.INTERVAL.getCode())
                    .ge("all_cnt", 450)
                    .ge("win_rate", 0.6 + i * 0.05)
                    .le("win_rate", 0.65 + i * 0.05);
            List<StockCalcRes> stockCalcResList = stockCalcResDao.selectList(calcWapper);
            stockCalcResList.forEach(item -> item.setStockStrategy(
                    JSON.toJavaObject(JSON.parseObject(item.getStrategyDesc()), StockStrategy.class)));


            for (Stock stock : stockList) {
                //30开头是创业板  68开头是科创版
                if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                    return;
                }
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode())
                        .last("limit 1")
                        .orderByDesc("deal_date");
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                StockDetail stockDetail = stockDetails.stream().findFirst().orElse(null);
                if (Objects.nonNull(stockDetail)) {
                    boolean isOk = stockCalcResList.stream().anyMatch(calc -> {
                        StockStrategy strategy = calc.getStockStrategy();
                        return strategy.getLowShadowLowLimit().compareTo(stockDetail.getLowShadowPert()) <= 0
                                && strategy.getLowShadowUpLimit().compareTo(stockDetail.getLowShadowPert()) >= 0
                                && strategy.getUpShadowLowLimit().compareTo(stockDetail.getUpShadowPert()) <= 0
                                && strategy.getUpShadowUpLimit().compareTo(stockDetail.getUpShadowPert()) >= 0
                                && strategy.getPricePertLowLimit().compareTo(stockDetail.getPricePert()) <= 0
                                && strategy.getPricePertUpLimit().compareTo(stockDetail.getPricePert()) >= 0;
                    });
                    if (isOk) {
                        resList.add(stock);
                    }
                }

            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);

            if (!file.getParentFile().exists()) {
                boolean res = file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write(String.format("\n\n%s    win_rate:%3f  - %3f\n", getDateStr(), 0.6 + i * 0.05, 0.65 + i * 0.05).getBytes());
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    fos.write(str.getBytes());
                }
            } catch (IOException ignored) {
            }
        }
    }


    @Test
    @DisplayName("生成符合单条枚举策略的数据")
    public void ttt() throws ExecutionException, InterruptedException {
        buildData();
        buildData1();
    }


    @Test
    @DisplayName("生成符合单条枚举策略的数据")
    public void buildData() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        strategyResultDao.delete(new QueryWrapper<>());
        StrategyEnum[] values = StrategyEnum.values();

        List<List<Stock>> parts = stockService.getStockPart();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            for (Stock stock : part) {
                List<StockDetail> stockDetail = stockDetailService.getStockDetail(stock.getCode(), null);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    List<StrategyResult> list = new ArrayList<>();
                    for (StrategyEnum strategy : values) {
                        List<String> dateList = new ArrayList<>();
                        for (StockDetail detail : stockDetail) {
                            if (Objects.isNull(detail.getNext1()) || Objects.isNull(detail.getSixtyDayLine())) {
                                continue;
                            }
                            if (strategy.getRunFunc().apply(detail)) {
                                dateList.add(detail.getDealDate());
                            }
                        }
                        if (!CollectionUtils.isEmpty(dateList)) {
                            StrategyResult strategyResult = new StrategyResult(1, strategy.name(),
                                    stock.getCode(), dateList, now);
                            list.add(strategyResult);
                        }
                    }
                    if (CollectionUtils.isEmpty(list)) {
                        return;
                    }
                    strategyResultDao.insert(list);
                }, ioThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    @Test
    @DisplayName("生成符合单条枚举策略的数据，组合策略")
    public void buildData1() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        StrategyEnum[] values = StrategyEnum.values();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                StrategyEnum strategy1 = values[finalI];
                Map<String, Set<String>> stockCodeToDateMap = strategyResultService.getStockCodeToDateMap(strategy1.name(), 1);
                buildData2(finalI + 1, stockCodeToDateMap, strategy1.name(), now, 2);
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    public void buildData2(int startIdx, Map<String, Set<String>> sourceMap, String sourceStrategyName, LocalDateTime now, Integer level) {
        if (level > 2) {
            return;
        }
        Map<String, Set<String>> resMap = new HashMap<>();
        StrategyEnum[] values = StrategyEnum.values();
        for (int i = startIdx; i < values.length; i++) {
            StrategyEnum strategy = values[i];
            Map<String, Set<String>> map = strategyResultService.getStockCodeToDateMap(strategy.name(), 1);
            String strategyName = sourceStrategyName + " " + strategy.name();
            List<StrategyResult> strategyResultList = new ArrayList<>();
            int count = 0;
            //筛选多条件都符合的数据
            for (String stockCode : sourceMap.keySet()) {
                if (!map.containsKey(stockCode)) {
                    continue;
                }
                Set<String> dateSet = SetUtils.intersection(sourceMap.get(stockCode), map.get(stockCode));

                if (dateSet.isEmpty()) {
                    continue;
                }
                count = count + dateSet.size();
                StrategyResult strategyResult = new StrategyResult(level, strategyName, stockCode,
                        String.join(" ", dateSet), now);
                strategyResultList.add(strategyResult);
                resMap.put(stockCode, dateSet);
            }
            if (count >= 100) {
                strategyResultDao.insert(strategyResultList);
                buildData2(i + 1, resMap, strategyName, now, level + 1);
            }
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
}