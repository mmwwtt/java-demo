package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockCalcRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import com.mmwwtt.stock.service.StockGuiUitls;
import com.mmwwtt.stock.service.StockStrategyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.divide;
import static com.mmwwtt.stock.common.CommonUtils.moreThan;

@Slf4j
@SpringBootTest
public class CalcTest {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockCalcService stockCalcService;

    @Autowired
    private StockDetailDao stockDetailDao;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDao stockCalcResDao;

    /**
     * 今天的日期
     */
    private static final String NOW_DATA;

    private final VoConvert voConvert = VoConvert.INSTANCE;

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        NOW_DATA = LocalDate.now().format(formatter);
    }


    @Test
    @DisplayName("根据策略预测")
    public void getStockByStoregy() throws InterruptedException, ExecutionException {
        boolean isOnTime = false;
        Map<String, List<String>> strategyToStockMap = new HashMap<>();
        List<Stock> stockList = stockCalcService.getAllStock();
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap();
        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        QueryWrapper<StockCalcRes> detailWapper = new QueryWrapper<>();
        detailWapper.last("where type = 1" +
                " and create_date = (select max(create_date) from stock_calculation_result_t where type = '1')" +
                " and win_rate > 0.6" +
                " order by win_rate desc;");
        List<String> strategyNameList = stockCalcResDao.selectList(detailWapper).stream().map(StockCalcRes::getStrategyDesc).toList();
        for (String strategyName : strategyNameList) {
            Function<StockDetail, Boolean> runFunc = StockStrategyUtils.getStrategy(strategyName).getRunFunc();
            for (List<Stock> part : parts) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (Stock stock : part) {
                        List<StockDetail> stockDetails = codeToDetailMap.get(stock.getCode());
                        if (isOnTime) {
                            stockDetails.forEach(item -> item.setDealQuantity(divide(item.getDealQuantity(), "0.85")));
                        }
                        if (CollectionUtils.isEmpty(stockDetails)
                                || moreThan(stockDetails.get(0).getPricePert(), "0.097")) {
                            continue;
                        }
                        if (runFunc.apply(stockDetails.get(0))) {
                            strategyToStockMap.computeIfAbsent(strategyName, k -> new ArrayList<>())
                                    .add(stock.getCode() + "_" + stock.getName() + "_" + stockDetails.get(0).getPricePert().doubleValue());
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
            fos.write(String.format("\n\n%s\n", NOW_DATA).getBytes());
            for (Map.Entry<String, List<String>> entry : strategyToStockMap.entrySet()) {
                fos.write(("\n\n" + entry.getKey() + "\n").getBytes());
                for (String s : entry.getValue()) {
                    fos.write((s + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }

    }


    @Test
    @DisplayName("根据策略计算胜率")
    public void startCalc2() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        LocalDateTime dataTime = LocalDateTime.now();
        List<Stock> stockList = stockCalcService.getAllStock();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = stockCalcService.getStockDetail(stock.getCode(), null);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StockStrategy strategy : StockStrategyUtils.STRATEGY_LIST) {
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
            stockCalcService.saveCalcRes(list, strategyName, dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());
        });
        log.info("结束计算");
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap();
        LocalDateTime dataTime = LocalDateTime.now();
        String strategyName = "放量绿";
        for (StockStrategy strategy : StockStrategyUtils.STRATEGY_LIST) {
            if (!Objects.equals(strategy.getStrategyName(), strategyName)) {
                continue;
            }
            List<StockDetail> allAfterList = new ArrayList<>();
            codeToDetailMap.forEach((stockCode, detailList) -> {
                if (detailList.size() < 60) {
                    return;
                }
                List<StockDetail> afterList = detailList.subList(0, detailList.size() - 60).stream()
                        .filter(item -> item.getPricePert().compareTo(new BigDecimal("0.097")) < 0)
                        .filter(item -> Objects.nonNull(item.getNext1()))
                        .filter(item -> strategy.getRunFunc().apply(item)).toList();
                allAfterList.addAll(afterList);
            });
            stockCalcService.saveCalcRes(allAfterList, strategy.getStrategyName(), dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());

            allAfterList.stream().filter(item -> item.getNext1().getIsDown()).limit(1000).forEach(item -> {
                try {
                    StockGuiUitls.genDetailImage(item, strategy.getStrategyName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    @Test
    @DisplayName("测试策略")
    public void startCalc4() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap();
        LocalDateTime dataTime = LocalDateTime.now();

        StockStrategy strategy = new StockStrategy("test", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            return moreThan(t1.getPertDivisionQuantity(), t2.getPertDivisionQuantity())
                    &&t1.getIsDown() &&t2.getIsDown() &&t0.getIsTenStar();
        });
        List<StockDetail> allAfterList = new ArrayList<>();
        codeToDetailMap.forEach((stockCode, detailList) -> {
            if (detailList.size() < 60) {
                return;
            }
            List<StockDetail> afterList = detailList.subList(0, detailList.size() - 60).stream()
                    .filter(item -> item.getPricePert().compareTo(new BigDecimal("0.097")) < 0)
                    .filter(item -> Objects.nonNull(item.getNext1()))
                    .filter(item -> strategy.getRunFunc().apply(item)).toList();
            allAfterList.addAll(afterList);
        });
        stockCalcService.saveCalcRes(allAfterList, strategy.getStrategyName(), dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());

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
                fos.write(String.format("\n\n%s    win_rate:%3f  - %3f\n", NOW_DATA, 0.6 + i * 0.05, 0.65 + i * 0.05).getBytes());
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    fos.write(str.getBytes());
                }
            } catch (IOException ignored) {
            }
        }
    }
}
