package com.mmwwtt.stock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import com.mmwwtt.stock.service.StockCalcService;
import com.mmwwtt.stock.service.StockStrategyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.divide;
import static com.mmwwtt.stock.common.CommonUtils.moreThan;
import static com.mmwwtt.stock.common.Constants.*;


/**
 * 必盈url   <a href="https://www.biyingapi.com/">...</a>
 * 限流 每分钟200次接口调用
 */
@Slf4j
@SpringBootTest
public class Main {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockCalcService stockCalcService;

    @Autowired
    private StockDetailDao stockDetailDao;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDao stockCalcResDao;

    /**
     * 今天的日期
     */
    private static final String NOW_DATA;

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        NOW_DATA = LocalDate.now().format(formatter);
    }


    @Test
    @DisplayName("获得数据")
    public void getDataTest() {
        String stockCode = "605162.SH";
        Map<String, String> map1 = new HashMap<>();
        map1.put(LICENCE, BI_YING_LICENCE);
        map1.put(STOCK_CODE, stockCode);
        map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
        map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
        map1.put(START_DATA, "20251201");
        map1.put(END_DATA, NOW_DATA);
        map1.put(MAX_SIZE, "350");
        List<StockDetailVO> stockDetailVOs = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<StockDetailVO>>() {
        });

        Map<String, String> map2 = new HashMap<>();
        map2.put(LICENCE, BI_YING_LICENCE);
        map2.put(STOCK_CODE, stockCode.split("\\.")[0]);
        StockDetailOnTimeVO stockDetailOnTimeVO = getResponse(ON_TIME_DATA_URL, map2, new ParameterizedTypeReference<StockDetailOnTimeVO>() {
        });
        log.info("{}", JSONObject.toJSONString(stockDetailVOs));
    }

    @Test
    @DisplayName("从0开始构建数据")
    public void start() throws ExecutionException, InterruptedException {
        dataDownLoad();
        dataDetailDownLoad();
        dataDetailCalc();
    }

    @Test
    @DisplayName("3点前的增量数据")
    public void dounLoadAdd() throws ExecutionException, InterruptedException {
        dataDetailDownLoadAdd();
        dataDetailCalcAdd();
    }


    @Test
    @DisplayName("调接口获取每日数据")
    public void dataDownLoad() {
        log.info("下载数据");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        List<StockVO> stockVOList = getResponse(STOCK_LIST_URL, map, new ParameterizedTypeReference<List<StockVO>>() {
        });
        List<Stock> stockList = StockConverter.INSTANCE.convertToStock(stockVOList);
        stockList = stockList.stream().filter(stock -> !stock.getCode().startsWith("30")
                && !stock.getCode().startsWith("68")
                && !stock.getName().contains("ST")).toList();
        stockDao.delete(new QueryWrapper<>());
        stockDao.insert(stockList);
        log.info("下载数据 end\n\n\n");
    }

    @Test
    @DisplayName("调接口获取每日的股票详细数据-全量")
    public void dataDetailDownLoad() throws InterruptedException, ExecutionException {
        log.info("下载股票详细数据");
        List<Stock> stockList = stockCalcService.getAllStock();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(LICENCE, BI_YING_LICENCE);
                    map1.put(STOCK_CODE, stock.getCode());
                    map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
                    map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
                    map1.put(START_DATA, "20250101");
                    map1.put(END_DATA, NOW_DATA);
                    map1.put(MAX_SIZE, "350");
                    log.info(stock.getCode());
                    List<StockDetailVO> stockDetailVOs = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<StockDetailVO>>() {
                    });
                    if (Objects.isNull(stockDetailVOs)) {
                        continue;
                    }
                    stockDetailVOs = stockDetailVOs.stream()
                            .peek(item -> item.setStockCode(stock.getCode()))
                            .filter(item -> item.getSf() == 0)
                            .collect(Collectors.toList());
                    List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);

                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stock.getCode());
                    Map<String, StockDetail> dateToMap = stockDetailDao.selectList(detailWapper).stream()
                            .collect(Collectors.toMap(StockDetail::getDealDate, Function.identity()));
                    for (StockDetail stockDetail : stockDetails) {
                        if (dateToMap.containsKey(stockDetail.getDealDate())) {
                            stockDetail.setStockDetailId(dateToMap.get(stockDetail.getDealDate()).getStockDetailId());
                        }
                    }
                    stockDetailDao.insertOrUpdate(stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("下载股票详细数据  end \n\n\n");
    }


    @Test
    @DisplayName("调接口获取每日的详细数据-增量")
    public void dataDetailDownLoadAdd() throws InterruptedException, ExecutionException {
        log.info("下载详细增量数据");
        List<Stock> stockList = stockCalcService.getAllStock();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(LICENCE, BI_YING_LICENCE);
                    map1.put(STOCK_CODE, stock.getCode());
                    map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
                    map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
                    map1.put(START_DATA, "20251220");
                    map1.put(END_DATA, NOW_DATA);
                    map1.put(MAX_SIZE, "30");
                    log.info(stock.getCode());
                    List<StockDetailVO> stockDetailVOs = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<StockDetailVO>>() {
                    });
                    if (Objects.isNull(stockDetailVOs)) {
                        continue;
                    }
                    stockDetailVOs = stockDetailVOs.stream()
                            .filter(item -> item.getSf() == 0)
                            .peek(item -> item.setStockCode(stock.getCode()))
                            .collect(Collectors.toList());
                    List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);

                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stock.getCode());
                    Map<String, StockDetail> dateToMap = stockDetailDao.selectList(detailWapper).stream()
                            .collect(Collectors.toMap(StockDetail::getDealDate, Function.identity()));
                    for (StockDetail stockDetail : stockDetails) {
                        if (dateToMap.containsKey(stockDetail.getDealDate())) {
                            stockDetail.setStockDetailId(dateToMap.get(stockDetail.getDealDate()).getStockDetailId());
                        }
                    }
                    stockDetailDao.insertOrUpdate(stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("end");
    }

    @Test
    @DisplayName("计算衍生数据-全量")
    public void dataDetailCalc() throws InterruptedException, ExecutionException {
        log.info("计算衍生数据--开始");
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap();
        List<Stock> stockList = stockCalcService.getAllStock();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.getOrDefault(stock.getCode(), new ArrayList<>());
                    stockDetails.forEach(item -> item.calc());
                    StockDetail.calc(stockDetails);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算衍生数据--计算完成  开始保存");
        futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.getOrDefault(stock.getCode(), new ArrayList<>());
                    stockDetailDao.updateById(stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("衍生数据计算--完毕  \n\n\n");
    }

    @Test
    @DisplayName("计算衍生数据-增量")
    public void dataDetailCalcAdd() throws InterruptedException, ExecutionException {
        log.info("计算衍生数据--开始");
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap(80);
        List<Stock> stockList = stockCalcService.getAllStock();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.getOrDefault(stock.getCode(), new ArrayList<>());
                    stockDetails.forEach(item -> item.calc());
                    StockDetail.calc(stockDetails);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算衍生数据--计算完成  开始保存");
        futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.getOrDefault(stock.getCode(), new ArrayList<>());
                    stockDetailDao.updateById(stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("衍生数据计算--完毕  \n\n\n");
    }

    @Test
    @DisplayName("根据百分比区间预测明日会上涨")
    public void getStock() {
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
                file.getParentFile().mkdirs();
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

    @Test
    @DisplayName("上升缺口 成交量超过5日线")
    public void getStock1() throws InterruptedException, ExecutionException {
        boolean isOnTime = false;
        Map<String, List<String>> strategyToStockMap = new HashMap<>();
        List<Stock> stockList = stockCalcService.getAllStock();
        Map<String, List<StockDetail>> codeToDetailMap = stockCalcService.getCodeToDetailMap();
        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        List<String> strategyNameList = Arrays.asList(
                "十字星",
                "上升缺口",
                "上升缺口 且缩量",
                "上升缺口 且缩量 且4%<涨幅＜7%");
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
                                ||moreThan( stockDetails.get(0).getPricePert(), "0.097")) {
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

    private <T> T getResponse(String url, Map<String, String> paramMap, ParameterizedTypeReference reference) {
        while (true) {
            try {
                ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.GET, null, reference, paramMap);
                return res.getBody();
            } catch (Exception e) {
                //打印除限流外的错误
                if (e.getMessage().startsWith("429")) {
                    log.info("{}", e.getMessage());
                } else {
                    return null;
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception e1) {
                    break;
                }
            }
        }
        return null;
    }

}