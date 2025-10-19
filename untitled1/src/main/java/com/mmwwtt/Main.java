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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mmwwtt.common.Constants.*;


/**
 * 必盈url   https://www.biyingapi.com/
 */
public class Main {
    @Test
   public void test(){
        RestTemplate restTemplate=new RestTemplate();
         restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<Stock>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {},map);
        List<Stock> stockList = stocksListResponse.getBody();


        Map<String, String> map1 = new HashMap<>();
        map1.put(LICENCE, BI_YING_LICENCE);
        map1.put(STOCK_CODE, "000001");
        map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
        map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
        map1.put(START_DATA, "20221020");
        map1.put(END_DATA, "20251020");
        map1.put(MAX_SIZE, "365");
        ResponseEntity<List<StockDetail>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {},map1);
        List<StockDetail> stockDetailList = stockDetailResponse.getBody();

        System.out.println();
    }
}