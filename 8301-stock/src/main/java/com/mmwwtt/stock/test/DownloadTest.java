package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import com.mmwwtt.stock.service.impl.StockDetailServiceImpl;
import com.mmwwtt.stock.service.impl.StockServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyResultServiceImpl;
import com.mmwwtt.stock.vo.StockDetailOnTimeVO;
import com.mmwwtt.stock.vo.StockDetailVO;
import com.mmwwtt.stock.vo.StockVO;
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
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.Constants.*;

/**
 * 必盈url   <a href="https://www.biyingapi.com/">...</a>
 * 限流 每分钟200次接口调用
 */
@SpringBootTest
@Slf4j
public class DownloadTest {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockDetailServiceImpl stockDetailService;

    @Autowired
    private StrategyResultServiceImpl strategyResultService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

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
    @DisplayName("从0开始构建数据")
    public void start() throws ExecutionException, InterruptedException {
        dataDownLoad();
        dataDetailDownLoad();
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
        log.info("获取单个代码的数据结束：{}", JSONObject.toJSONString(stockDetailVOs));
    }


    @Test
    @DisplayName("调接口获取数据")
    public void dataDownLoad() {
        log.info("下载数据");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        List<StockVO> stockVOList = getResponse(STOCK_LIST_URL, map, new ParameterizedTypeReference<List<StockVO>>() {
        });
        List<Stock> stockList = voConvert.convertToStock(stockVOList);
        stockList = stockList.stream().filter(stock -> !stock.getCode().startsWith("30")
                && !stock.getCode().startsWith("68")
                && !stock.getName().contains("ST")).toList();
        stockService.remove(new QueryWrapper<>());
        stockService.saveBatch(stockList);
        log.info("下载数据 end\n\n\n");
    }

    @Test
    @DisplayName("调接口获取每日详细数据-全量")
    public void dataDetailDownLoad() throws InterruptedException, ExecutionException {
        log.info("下载股票详细数据");
        List<Stock> stockList = stockService.getAllStock();
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
                    log.info("获取详情数据-{}", stock.getCode());
                    List<StockDetailVO> stockDetailVOs = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<StockDetailVO>>() {
                    });
                    if (Objects.isNull(stockDetailVOs)) {
                        continue;
                    }
                    stockDetailVOs = stockDetailVOs.stream()
                            .peek(item -> item.setStockCode(stock.getCode()))
                            .filter(item -> item.getSf() == 0)
                            .collect(Collectors.toList());
                    List<StockDetail> stockDetails = voConvert.convertToStockDetail(stockDetailVOs);

                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stock.getCode());
                    Map<String, StockDetail> dateToMap = stockDetailService.list(detailWapper).stream()
                            .collect(Collectors.toMap(StockDetail::getDealDate, Function.identity()));
                    for (StockDetail stockDetail : stockDetails) {
                        if (dateToMap.containsKey(stockDetail.getDealDate())) {
                            stockDetail.setStockDetailId(dateToMap.get(stockDetail.getDealDate()).getStockDetailId());
                        }
                    }
                    stockDetails.sort(Comparator.comparing(StockDetail::getDealDate).reversed());
                    stockDetails.forEach(item -> item.calc());
                    StockDetail.calc(stockDetails);
                    stockDetailService.saveOrUpdateBatch(stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("下载股票详细数据  end \n\n\n");
    }


    private <T> T getResponse(String url, Map<String, String> paramMap, ParameterizedTypeReference<T> reference) {
        while (true) {
            try {
                ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.GET, null, reference, paramMap);
                return res.getBody();
            } catch (Exception e) {
                //打印除限流外的错误
                if (!e.getMessage().startsWith("429")) {
                    log.info("调接口时发生错误{}", e.getMessage());
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
