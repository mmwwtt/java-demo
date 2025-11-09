package com.mmwwtt;

import com.mmwwtt.common.LoggingInterceptor;
import com.mmwwtt.entity.Stock;
import com.mmwwtt.entity.StockDetail;
import com.mmwwtt.enums.ExcludeRightEnum;
import com.mmwwtt.enums.TimeLevelEnum;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.mmwwtt.common.Constants.*;


/**
 * 必盈url   https://www.biyingapi.com/
 */
public class Main {
    @Test
    public void test() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<Stock>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<Stock> stockList = stocksListResponse.getBody();

        int cnt = 0;
        for (Stock stock : stockList) {
            cnt++;
            if (cnt > 200) {
                Thread.sleep(60000);
                cnt = 0;
            }
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getDm());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251001");
            map1.put(END_DATA, "20251025");
            map1.put(MAX_SIZE, "30");
            ResponseEntity<List<StockDetail>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                continue;
            }
            List<StockDetail> stockDetailList = stockDetailResponse.getBody();
            boolean isMoreQuantityRed = isMoreQuantityRed(stockDetailList);
            boolean isLessQuantityRed = isLessQuantityGreen(stockDetailList);
            if (isMoreQuantityRed || isLessQuantityRed) {
                System.out.printf("%s_%s\n", stock.getDm(), stock.getMc());
            }
        }
    }

    /**
     * 放量红 当天收盘价高于前一天收盘价，且成交量比前一天高
     *
     * @return
     */
    public boolean isMoreQuantityRed(List<StockDetail> stockDetailList) {
        if (Objects.isNull(stockDetailList) || stockDetailList.size() < 3) {
            return false;
        }
        StockDetail stockDetail3 = stockDetailList.get(stockDetailList.size() - 3);
        StockDetail stockDetail2 = stockDetailList.get(stockDetailList.size() - 2);
        StockDetail stockDetail1 = stockDetailList.get(stockDetailList.size() - 1);
        return quentityIsMoreThan(stockDetail2.getV(), stockDetail3.getV())
                && stockDetail2.getC() - stockDetail3.getC() > 0
                && quentityIsMoreThan(stockDetail1.getV(), stockDetail2.getV())
                && stockDetail1.getC() - stockDetail2.getC() > 0;
    }

    /**
     * 缩量绿 当天收盘价低于前一天收盘价，且成交量比前一天低， 但是前面两天都是放量红
     *
     * @return
     */
    public boolean isLessQuantityGreen(List<StockDetail> stockDetailList) {
        if (Objects.isNull(stockDetailList) || stockDetailList.size() < 4) {
            return false;
        }
        StockDetail stockDetail4 = stockDetailList.get(stockDetailList.size() - 4);
        StockDetail stockDetail3 = stockDetailList.get(stockDetailList.size() - 3);
        StockDetail stockDetail2 = stockDetailList.get(stockDetailList.size() - 2);
        StockDetail stockDetail1 = stockDetailList.get(stockDetailList.size() - 1);
        if (!isMoreQuantityRed(Arrays.asList(stockDetail4, stockDetail3, stockDetail2))) {
            return false;
        }
        return stockDetail1.getV() - stockDetail2.getV() < 0
                && stockDetail1.getC() - stockDetail2.getC() < 0;
    }

    /**
     * 比较成交量是否相同，运行5%的误差
     *
     * @param num1
     * @param num2
     * @return
     */
    private boolean quentityIsMoreThan(float num1, float num2) {
        return Math.abs(num1 - num2) / num1 < 0.05 || num1 > num2;
    }


    @Test
    public void test1() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<Stock>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<Stock> stockList = stocksListResponse.getBody();

        int cnt = 0;
        for (Stock stock : stockList) {
            cnt++;
            if(cnt%200==0) {
                Thread.sleep(10000);
            }
            //System.out.println(stock.getMc());
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getDm());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251101");
            map1.put(END_DATA, "20251107");
            map1.put(MAX_SIZE, "30");
            ResponseEntity<List<StockDetail>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                System.out.println("");
                e.printStackTrace();
                continue;
            }
            List<StockDetail> stockDetailList = stockDetailResponse.getBody();
            if (quentityAndPerc(stockDetailList)) {
                System.out.printf("-----------------------------------------------------------------------  %s_%s\n", stock.getDm(), stock.getMc());
            }
        }
    }

    @Test
    public void test1_1() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, "002836.SZ");
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251101");
            map1.put(END_DATA, "20251107");
            map1.put(MAX_SIZE, "30");
            ResponseEntity<List<StockDetail>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<StockDetail> stockDetailList = stockDetailResponse.getBody();
            quentityAndPerc(stockDetailList);

    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(StockDetail stockDetail1, StockDetail stockDetail2) {
        float res2 = (stockDetail2.getPert() / stockDetail2.getV());
        float res1 = (stockDetail1.getPert() / stockDetail1.getV());
        return res2 - res1 > 0 + Math.abs(res1 * 0.05);
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(List<StockDetail> stockDetailList) {
//        for(int i = 4; i >0; i--) {
//            StockDetail stockDetail =stockDetailList.get(stockDetailList.size() - i);
//            System.out.printf("%s   涨幅：%s:  成交量：%s:     结果：%s \n",i, stockDetail.getPert() ,stockDetail.getV(), stockDetail.getPert()/stockDetail.getV());
//        }
        if (Objects.isNull(stockDetailList) || stockDetailList.size() < 4) {
            return false;
        }
        StockDetail stockDetail4 = stockDetailList.get(stockDetailList.size() - 4);
        StockDetail stockDetail3 = stockDetailList.get(stockDetailList.size() - 3);
        StockDetail stockDetail2 = stockDetailList.get(stockDetailList.size() - 2);
        StockDetail stockDetail1 = stockDetailList.get(stockDetailList.size() - 1);
        if(stockDetail1.getPert() > 0.05) {
            return false;
        }
        return quentityAndPerc(stockDetail4,stockDetail3)
                &&quentityAndPerc(stockDetail3, stockDetail2)
                &&quentityAndPerc(stockDetail2, stockDetail1);
    }
}