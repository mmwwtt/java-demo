package com.mmwwtt.stock;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetailVO;
import com.mmwwtt.stock.entity.StockVO;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.*;

import static com.mmwwtt.stock.common.Constants.*;


/**
 * 必盈url   https://www.biyingapi.com/
 */
@SpringBootTest
public class Main {

    @Autowired
    private StockDao stockDao;

    @Test
    public void test() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<StockVO>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<StockVO> stockList = stocksListResponse.getBody();

        int cnt = 0;
        for (StockVO stock : stockList) {
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
            ResponseEntity<List<StockDetailVO>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                continue;
            }
            List<StockDetailVO> stockDetailVOList = stockDetailResponse.getBody();
            boolean isMoreQuantityRed = isMoreQuantityRed(stockDetailVOList);
            boolean isLessQuantityRed = isLessQuantityGreen(stockDetailVOList);
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
    public boolean isMoreQuantityRed(List<StockDetailVO> stockDetailVOList) {
        if (Objects.isNull(stockDetailVOList) || stockDetailVOList.size() < 3) {
            return false;
        }
        StockDetailVO stockDetailVO3 = stockDetailVOList.get(stockDetailVOList.size() - 3);
        StockDetailVO stockDetailVO2 = stockDetailVOList.get(stockDetailVOList.size() - 2);
        StockDetailVO stockDetailVO1 = stockDetailVOList.get(stockDetailVOList.size() - 1);
        return quentityIsMoreThan(stockDetailVO2.getV(), stockDetailVO3.getV())
                && stockDetailVO2.getC() - stockDetailVO3.getC() > 0
                && quentityIsMoreThan(stockDetailVO1.getV(), stockDetailVO2.getV())
                && stockDetailVO1.getC() - stockDetailVO2.getC() > 0;
    }

    /**
     * 缩量绿 当天收盘价低于前一天收盘价，且成交量比前一天低， 但是前面两天都是放量红
     *
     * @return
     */
    public boolean isLessQuantityGreen(List<StockDetailVO> stockDetailVOList) {
        if (Objects.isNull(stockDetailVOList) || stockDetailVOList.size() < 4) {
            return false;
        }
        StockDetailVO stockDetailVO4 = stockDetailVOList.get(stockDetailVOList.size() - 4);
        StockDetailVO stockDetailVO3 = stockDetailVOList.get(stockDetailVOList.size() - 3);
        StockDetailVO stockDetailVO2 = stockDetailVOList.get(stockDetailVOList.size() - 2);
        StockDetailVO stockDetailVO1 = stockDetailVOList.get(stockDetailVOList.size() - 1);
        if (!isMoreQuantityRed(Arrays.asList(stockDetailVO4, stockDetailVO3, stockDetailVO2))) {
            return false;
        }
        return stockDetailVO1.getV() - stockDetailVO2.getV() < 0
                && stockDetailVO1.getC() - stockDetailVO2.getC() < 0;
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
        ResponseEntity<List<StockVO>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<StockVO> stockList = stocksListResponse.getBody();

        int cnt = 0;
        for (StockVO stock : stockList) {
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
            ResponseEntity<List<StockDetailVO>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                System.out.println("");
                e.printStackTrace();
                continue;
            }
            List<StockDetailVO> stockDetailVOList = stockDetailResponse.getBody();
            if (quentityAndPerc(stockDetailVOList)) {
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
            ResponseEntity<List<StockDetailVO>> stockDetailResponse=null;
            try {
                stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<StockDetailVO> stockDetailVOList = stockDetailResponse.getBody();
            quentityAndPerc(stockDetailVOList);

    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(StockDetailVO stockDetailVO1, StockDetailVO stockDetailVO2) {
        float res2 = (stockDetailVO2.getPert() / stockDetailVO2.getV());
        float res1 = (stockDetailVO1.getPert() / stockDetailVO1.getV());
        return res2 - res1 > 0 + Math.abs(res1 * 0.05);
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(List<StockDetailVO> stockDetailVOList) {
//        for(int i = 4; i >0; i--) {
//            StockDetail stockDetail =stockDetailList.get(stockDetailList.size() - i);
//            System.out.printf("%s   涨幅：%s:  成交量：%s:     结果：%s \n",i, stockDetail.getPert() ,stockDetail.getV(), stockDetail.getPert()/stockDetail.getV());
//        }
        if (Objects.isNull(stockDetailVOList) || stockDetailVOList.size() < 4) {
            return false;
        }
        StockDetailVO stockDetailVO4 = stockDetailVOList.get(stockDetailVOList.size() - 4);
        StockDetailVO stockDetailVO3 = stockDetailVOList.get(stockDetailVOList.size() - 3);
        StockDetailVO stockDetailVO2 = stockDetailVOList.get(stockDetailVOList.size() - 2);
        StockDetailVO stockDetailVO1 = stockDetailVOList.get(stockDetailVOList.size() - 1);
        if(stockDetailVO1.getPert() > 0.05) {
            return false;
        }
        return quentityAndPerc(stockDetailVO4, stockDetailVO3)
                &&quentityAndPerc(stockDetailVO3, stockDetailVO2)
                &&quentityAndPerc(stockDetailVO2, stockDetailVO1);
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
    public void dataDetailDownLoad() {
       QueryWrapper<Stock>  queryWrapper = new QueryWrapper<>();
        String sqlSelect = queryWrapper.getSqlSelect();
        List<Stock> stockList =stockDao.selectList(queryWrapper);
        stockList.forEach(stock -> {

        });
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
//        Map<String, String> map = new HashMap<>();
//        map.put(LICENCE, BI_YING_LICENCE);
//        ResponseEntity<List<Stock>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
//                null, new ParameterizedTypeReference<>() {
//                }, map);
//        List<Stock> stockList = stocksListResponse.getBody();
    }
}