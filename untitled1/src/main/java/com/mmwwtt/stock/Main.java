package com.mmwwtt.stock;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.mmwwtt.stock.service.StockStartService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.Constants.*;


/**
 * 必盈url   https://www.biyingapi.com/
 */
@SpringBootTest
public class Main {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockDetailDao stockDetailDao;

    private RestTemplate restTemplate = new RestTemplate();


    @Test
    public void test1() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        List<Stock> resList = new ArrayList<>();
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);

            if (quentityAndPerc(stockDetails)) {
                resList.add(stock);
                System.out.printf("%s_%s\n", stock.getCode(), stock.getName());
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
            fos.write(String.format("\n\n%s\n", nowDate).getBytes());
            for (Stock item : resList) {
                String str = String.format("%s_%s\n", item.getCode(), item.getName());
                fos.write(str.getBytes());
            }
        } catch (IOException e) {
        }
    }


    @Test
    public void test2() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        List<Pair<Stock, StockStartService.ScoreResult>> resList = new ArrayList<>();
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            detailWapper.orderByAsc("deal_date");
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
            StockStartService.ScoreResult scoreResult = StockStartService.calculateUpProbability(stockDetails);
            if (scoreResult.getScore() < 5) {
                continue;
            }
            resList.add(Pair.of(stock, scoreResult));

        }
        resList = resList.stream()
                .sorted(Comparator.comparing(item -> item.getRight().getScore()))
                .toList();
        String filePath = "src/main/resources/file/test2.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String nowDate = LocalDate.now().format(formatter);
            fos.write(String.format("\n\n%s\n", nowDate).getBytes());
            for (Pair<Stock, StockStartService.ScoreResult> item : resList) {
                Stock stock = item.getLeft();
                StockStartService.ScoreResult scoreResult = item.getRight();
                String str = String.format("%s_%s\n", stock.getCode(), stock.getName());
                String str2 = String.format("%s_%s\n\n", scoreResult.getScore(), scoreResult.getDescription());
                fos.write(str.getBytes());
                fos.write(str2.getBytes());
            }
        } catch (IOException e) {
        }
    }

    @Test
    public void test2_1() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        Stock stock1 = new Stock();
        stock1.setCode("002670.SZ");
        List<Stock> stockList = Arrays.asList(stock1);
        List<Pair<Stock, StockStartService.ScoreResult>> resList = new ArrayList<>();
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            detailWapper.orderByAsc("deal_date");
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
            StockStartService.ScoreResult scoreResult = StockStartService.calculateUpProbability(stockDetails);
            resList.add(Pair.of(stock, scoreResult));

        }
        resList = resList.stream()
                .sorted(Comparator.comparing(item -> item.getRight().getScore()))
                .toList();
        String filePath = "src/main/resources/file/test2.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String nowDate = LocalDate.now().format(formatter);
            fos.write(String.format("\n\n%s\n", nowDate).getBytes());
            for (Pair<Stock, StockStartService.ScoreResult> item : resList) {
                Stock stock = item.getLeft();
                StockStartService.ScoreResult scoreResult = item.getRight();
                String str = String.format("%s_%s\n", stock.getCode(), stock.getName());
                String str2 = String.format("%s_%s\n\n", scoreResult.getScore(), scoreResult.getDescription());
                fos.write(str.getBytes());
                fos.write(str2.getBytes());
            }
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("在十日线下方，并比前5天的平均成交量 放量1.2倍以上 且为上涨的股票, 进两天长上影线的排除")
    public void test3() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        List<Stock> resList = new ArrayList<>();
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            detailWapper.orderByDesc("deal_date");
            detailWapper.last("limit 11");
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
            double tenPrice = stockDetails.stream().skip(1).mapToDouble(StockDetail::getEndPrice).sum() / 10;
            StockDetail stockDetail = stockDetails.get(0);
            StockDetail stockDetail1 = stockDetails.get(1);
            StockDetail stockDetail2 = stockDetails.get(2);
            StockDetail prevStockDetail = stockDetails.get(1);
            boolean flag = true;
            //比前5天的平均量*1.2倍  表示放量
            double fiveAverageDealQuentity = stockDetails.stream().skip(1).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            double fiveAverageDealQuentity1 = stockDetails.stream().skip(2).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            double fiveAverageDealQuentity2 = stockDetails.stream().skip(3).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            if (stockDetail.getAllDealQuantity() < fiveAverageDealQuentity * 1.1
                    || stockDetail1.getAllDealQuantity() < fiveAverageDealQuentity1 * 1.1
                    || stockDetail2.getAllDealQuantity() < fiveAverageDealQuentity2 * 1.1) {
                flag = false;
            }
            //比前一天量*2.5倍  放巨量 很危险
            if (stockDetail.getAllDealQuantity() > fiveAverageDealQuentity * 2.5) {
                flag = false;
            }
            //当天涨幅＜1%  / >6% /  前一天是跌的  排除不活跃和太活跃的
            if (stockDetail.getPricePert() < 0.015 || stockDetail.getPricePert() > 0.06 || prevStockDetail.getPricePert() < 0) {
                flag = false;
            }
            //上影线长度占实体1/3以上  表示抛压大
            if (stockDetail.getUpperShadowLength() / stockDetail.getAllLength() > 0.33
                    || prevStockDetail.getUpperShadowLength() / prevStockDetail.getAllLength() > 0.33) {
                flag = false;
            }
            if (flag) {
                resList.add(stock);
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
            fos.write(String.format("\n\n%s\n", nowDate).getBytes());
            for (Stock stock : resList) {
                String str = String.format("%s_%s\n", stock.getCode(), stock.getName());
                fos.write(str.getBytes());
            }
        } catch (IOException e) {
        }
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(StockDetail stockDetail1, StockDetail stockDetail2) {
        //当天是红的
        if (stockDetail1.getPricePert() <= 0 || stockDetail2.getPricePert() <= 0) {
            return false;
        }

        //涨幅大于5的 和小于1.5的排除
        if (stockDetail2.getPricePert() < 0.01 || stockDetail2.getPricePert() > 0.05) {
            return false;
        }


        //涨幅/成交量
        boolean isOk1 = (stockDetail1.getPricePert() / stockDetail1.getAllDealQuantity()) * 1.05
                < stockDetail2.getPricePert() / stockDetail2.getAllDealQuantity();
        //缩量
        return isOk1;
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(List<StockDetail> stockDetailList) {
        if (Objects.isNull(stockDetailList) || stockDetailList.size() < 5) {
            return false;
        }
        StockDetail stockDetail5 = stockDetailList.get(stockDetailList.size() - 5);
        StockDetail stockDetail4 = stockDetailList.get(stockDetailList.size() - 4);
        StockDetail stockDetail3 = stockDetailList.get(stockDetailList.size() - 3);
        StockDetail stockDetail2 = stockDetailList.get(stockDetailList.size() - 2);
        StockDetail stockDetail1 = stockDetailList.get(stockDetailList.size() - 1);
        return quentityAndPerc(stockDetail3, stockDetail2)
                && quentityAndPerc(stockDetail2, stockDetail1);
    }

    @Test
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
        stockDao.insert(stockList);
    }

    @Test
    public void dataDetailDownLoad() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String nowDate = LocalDate.now().format(formatter);
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        int cnt = 0;
        for (Stock stock : stockList) {
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            cnt++;
            if (cnt % 200 == 0) {
                Thread.sleep(10000);
            }
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251201");
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
                for (StockDetail stockDetail : stockDetails) {
                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stockDetail.getStockCode());
                    detailWapper.eq("deal_date", stockDetail.getDealDate());
                    List<StockDetail> stockDetails1 = stockDetailDao.selectList(detailWapper);
                    if (CollectionUtils.isNotEmpty(stockDetails1)) {
                        stockDetail.setStockDetailId(stockDetails1.get(0).getStockDetailId());
                    }
                    stockDetailDao.insertOrUpdate(stockDetail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}