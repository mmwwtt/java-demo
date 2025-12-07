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
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.Constants.*;
import static com.mmwwtt.stock.common.Constants.BI_YING_LICENCE;
import static com.mmwwtt.stock.common.Constants.END_DATA;
import static com.mmwwtt.stock.common.Constants.EXCLUDE_RIGHT;
import static com.mmwwtt.stock.common.Constants.HISTORY_DATA_URL;
import static com.mmwwtt.stock.common.Constants.LICENCE;
import static com.mmwwtt.stock.common.Constants.MAX_SIZE;
import static com.mmwwtt.stock.common.Constants.START_DATA;
import static com.mmwwtt.stock.common.Constants.STOCK_CODE;
import static com.mmwwtt.stock.common.Constants.TIME_LEVEL;

@Service
public class StockService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockDetailDao stockDetailDao;

    private RestTemplate restTemplate = new RestTemplate();

    public List<String> getStockRecommendByRealTime() {
        List<String> result = new ArrayList<>();
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());

                ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(REAL_TIME_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {}, map1);


            List<Stock> resList = new ArrayList<>();
            //todo 修改
            if (quentityAndPerc(stockDetails)) {
                resList.add(stock);
                System.out.printf("%s_%s\n", stock.getCode(), stock.getName());
            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file,true)) {
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    result.add(str);
                    fos.write(str.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<String> getStockRecommendByHistory() {
        List<String> result = new ArrayList<>();
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);

            List<Stock> resList = new ArrayList<>();
            if (quentityAndPerc(stockDetails)) {
                resList.add(stock);
                System.out.printf("%s_%s\n", stock.getCode(), stock.getName());
            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file,true)) {
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    result.add(str);
                    fos.write(str.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(StockDetail stockDetail1, StockDetail stockDetail2) {
        float res2 = (stockDetail2.getPricePert() / stockDetail2.getAllDealQuantity());
        float res1 = (stockDetail1.getPricePert() / stockDetail1.getAllDealQuantity());
        return res2 - res1 > 0 + Math.abs(res1 * 1.5);
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
        if (stockDetail1.getPricePert() > 0.05 || stockDetail1.getPricePert() < 0
                ||stockDetail1.getEndPrice() < stockDetail1.getStartPrice()) {
            return false;
        }
        int cnt = 0;
        if (quentityAndPerc(stockDetail5, stockDetail4)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail4, stockDetail3)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail3, stockDetail2)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail2, stockDetail1)) {
            cnt++;
        }

        return cnt >= 3;
    }

    /**
     * 更新股票信息
     */
    @Test
    public void dataDownLoad() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<StockVO>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {}, map);
        List<StockVO> stockVOList = stocksListResponse.getBody();
        List<Stock> stockList = StockConverter.INSTANCE.convertToStock(stockVOList);
        stockDao.insert(stockList);
    }

    /**
     * 更新每日股票涨跌信息
     * @throws InterruptedException
     */
    @PostConstruct
    @Scheduled(cron = "0 30 3 * * ?")
    public void dataDetailDownLoad() throws InterruptedException {
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
            map1.put(START_DATA, "20251101");
            map1.put(END_DATA, "20251113");
            map1.put(MAX_SIZE, "30");
            try {
                ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {}, map1);
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
