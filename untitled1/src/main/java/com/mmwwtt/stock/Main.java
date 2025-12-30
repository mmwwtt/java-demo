package com.mmwwtt.stock;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockDetailVO;
import com.mmwwtt.stock.entity.StockVO;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
    private StockDetailDao stockDetailDao;

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();


    private RestTemplate restTemplate = new RestTemplate();

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
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        int cnt = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68") || stock.getName().contains("ST")) {
                continue;
            }
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
            map1.put(START_DATA, "20250301");
            map1.put(END_DATA, nowDate);
            map1.put(MAX_SIZE, "300");
            ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                    null, new ParameterizedTypeReference<>() {
                    }, map1);
            List<StockDetailVO> stockDetailVOs = stockDetailResponse.getBody().stream()
                    .peek(item -> item.setStockCode(stock.getCode()))
                    .collect(Collectors.toList());
            List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }


    @Test
    @DisplayName("调接口获取每日的股票详细数据-增量")
    public void dataDetailDownLoadAdd() throws InterruptedException, ExecutionException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String nowDate = LocalDate.now().format(formatter);
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        int cnt = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {

            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68") || stock.getName().contains("ST")) {
                continue;
            }
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
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //30开头是创业板  68开头是科创版
                if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68") || stock.getName().contains("ST")) {
                    return;
                }
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode());
                detailWapper.orderByDesc("deal_date");
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                stockDetails.forEach(item -> item.calc());
                StockDetail.calc(stockDetails);
                stockDetailDao.updateById(stockDetails);
            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
    }
}