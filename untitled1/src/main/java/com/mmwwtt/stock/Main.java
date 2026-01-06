package com.mmwwtt.stock;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.mmwwtt.stock.service.StockStartService;
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

import static com.mmwwtt.stock.common.Constants.*;


/**
 * 必盈url   https://www.biyingapi.com/
 */
@Slf4j
@SpringBootTest
public class Main {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockStartService stockStartService;

    @Autowired
    private StockDetailDao stockDetailDao;

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();


    private RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDao stockCalcResDao;

    @Test
    @DisplayName("调接口获取每日的股票数据")
    public void dataDownLoad() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<StockVO>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<StockVO> stockVOList = stocksListResponse.getBody();
        List<Stock> stockList = StockConverter.INSTANCE.convertToStock(stockVOList);
        stockList = stockList.stream().filter(stock -> !stock.getCode().startsWith("30")
                && !stock.getCode().startsWith("68")
                && !stock.getName().contains("ST")).toList();
        stockDao.delete(new QueryWrapper<>());
        stockDao.insert(stockList);
        log.info("end");
    }

    @Test
    @DisplayName("调接口获取每日的股票详细数据-全量")
    public void dataDetailDownLoad() throws InterruptedException, ExecutionException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String nowDate = LocalDate.now().format(formatter);
        List<Stock> stockList = stockStartService.getAllStock();
        int cnt = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            cnt++;
            log.info("第{}个", cnt);
            if (cnt % 200 == 0) {
                Thread.sleep(10000);
            }
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20250101");
            map1.put(END_DATA, nowDate);
            map1.put(MAX_SIZE, "350");
            ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                    null, new ParameterizedTypeReference<>() {
                    }, map1);
            List<StockDetailVO> stockDetailVOs = stockDetailResponse.getBody().stream()
                    .filter(item -> item.getSf() == 0)
                    .peek(item -> item.setStockCode(stock.getCode()))
                    .collect(Collectors.toList());
            List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
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
            });
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        dataDetailCalc();
    }


    @Test
    @DisplayName("调接口获取每日的股票详细数据-增量")
    public void dataDetailDownLoadAdd() throws InterruptedException, ExecutionException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String nowDate = LocalDate.now().format(formatter);
        List<Stock> stockList = stockStartService.getAllStock();
        int cnt = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            cnt++;
            log.info("第{}个", cnt);
            if (cnt % 200 == 0) {
                Thread.sleep(5000);
            }
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251220");
            map1.put(END_DATA, nowDate);
            map1.put(MAX_SIZE, "30");
            try {
                ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
                List<StockDetailVO> stockDetailVOs = stockDetailResponse.getBody().stream()
                        .filter(item -> item.getSf() == 0)
                        .peek(item -> item.setStockCode(stock.getCode()))
                        .collect(Collectors.toList());
                List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stock.getCode());
                    Map<String, StockDetail> dateToMap = stockDetailDao.selectList(detailWapper).stream()
                            .collect(Collectors.toMap(StockDetail::getDealDate, Function.identity()));
                    for (StockDetail stockDetail : stockDetails) {
                        if (dateToMap.containsKey(stockDetail.getDealDate())) {
                            stockDetail.setStockDetailId(dateToMap.get(stockDetail.getDealDate()).getStockDetailId());
                        }
                    }
                    stockDetails.forEach(item -> item.calc());
                    stockDetailDao.insertOrUpdate(stockDetails);
                });
                futures.add(future);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }


    @Test
    @DisplayName("计算股票的衍生数据")
    public void dataDetailCalc() throws InterruptedException, ExecutionException {
        List<Stock> stockList = stockStartService.getAllStock();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode());
                detailWapper.orderByDesc("deal_date");
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                stockDetails.forEach(item -> item.calc());
                StockDetail.calc(stockDetails);
                try {
                    stockDetailDao.updateById(stockDetails);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }

    @Test
    @DisplayName("根据百分比区间预测明日会上涨的股票")
    public void getStock() throws InterruptedException, ExecutionException {
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
                String nowDate = LocalDate.now().format(formatter);
                fos.write(String.format("\n\n%s    win_rate:%3f  - %3f\n", nowDate, 0.6 + i * 0.05, 0.65 + i * 0.05).getBytes());
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    fos.write(str.getBytes());
                }
            } catch (IOException e) {
            }
        }
    }

    @Test
    @DisplayName("根据上升缺口_三日不回补预测股票")
    public void getStock1() throws InterruptedException, ExecutionException {
        List<Stock> stockList = stockStartService.getAllStock();
        List<Stock> resList = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 100);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = stockStartService.getAllStockDetail(stock.getCode());
                    if (StockStrategyUtils.strategy16(stockDetails.get(0))) {
                        resList.add(stock);
                    }
                }
            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String nowDate = LocalDate.now().format(formatter);
            fos.write(String.format("\n\n%s\n", nowDate).getBytes());
            for (Stock item : resList) {
                String str = String.format("%s_%s\n", item.getCode(), item.getName());
                fos.write(str.getBytes());
            }
        } catch (IOException e) {
        }

    }
}