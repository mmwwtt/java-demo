package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import com.mmwwtt.stock.service.impl.*;
import com.mmwwtt.stock.vo.DetailOnTimeVO;
import com.mmwwtt.stock.vo.DetailVO;
import com.mmwwtt.stock.vo.StockVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.moreThan;
import static com.mmwwtt.stock.common.Constants.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;

/**
 * 必盈url   <a href="https://www.biyingapi.com/">...</a>
 * 限流 每分钟200次接口调用
 */
@SpringBootTest
@Slf4j
public class DownloadTest {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private StrategyServiceImpl strategyService;

    @Resource
    private StrategyTmpServiceImpl strategyTmpService;

    @Resource
    private StrategyL1ServiceImpl strategyL1Service;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final RestTemplate restTemplate = createRestTemplate();

    /**
     * 创建带超时配置的 RestTemplate
     * 连接超时 30 秒，读取超时 60 秒（可根据需要调整）
     */
    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);  // 连接超时 30 秒
        factory.setReadTimeout(60000);     // 读取超时 60 秒
        return new RestTemplate(factory);
    }

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
    public void buildDetail() throws ExecutionException, InterruptedException {
        try {
            downLoadInit();
            downStock();
            downDetail();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @DisplayName("重新生成L1曾策略")
    public void buildL1() throws ExecutionException, InterruptedException {
        try {
            buildStrategyL1();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Test
    @DisplayName("验证数据下载接口")
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
        List<DetailVO> detailVOS = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<DetailVO>>() {
        });

        Map<String, String> map2 = new HashMap<>();
        map2.put(LICENCE, BI_YING_LICENCE);
        map2.put(STOCK_CODE, stockCode.split("\\.")[0]);
        DetailOnTimeVO detailOnTimeVO = getResponse(ON_TIME_DATA_URL, map2, new ParameterizedTypeReference<DetailOnTimeVO>() {
        });
        log.info("获取单个代码的数据结束：{}", JSONObject.toJSONString(detailVOS));
    }

    public void downLoadInit() {
        log.info("开始清空表 start\n\n\n");
        stockService.remove(new QueryWrapper<>());
        detailService.remove(new QueryWrapper<>());
        strategyL1Service.remove(new QueryWrapper<>());
        strategyTmpService.remove(new QueryWrapper<>());
        strategyService.remove(new QueryWrapper<>());
        log.info("开始清空表 end\n\n\n");
    }


    @Test
    @DisplayName("调接口获取数据")
    public void downStock() {

        log.info("下载数据");
        RestTemplate restTemplate = createRestTemplate();
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
    public void downDetail() throws InterruptedException, ExecutionException {
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
                    List<DetailVO> detailVOS = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<DetailVO>>() {
                    });
                    if (Objects.isNull(detailVOS)) {
                        continue;
                    }
                    detailVOS = detailVOS.stream()
                            .peek(item -> item.setStockCode(stock.getCode()))
                            .filter(item -> item.getSf() == 0)
                            .collect(Collectors.toList());
                    List<Detail> details = voConvert.convertToDetail(detailVOS);

                    QueryWrapper<Detail> detailWrapper = new QueryWrapper<>();
                    detailWrapper.eq("stock_code", stock.getCode());
                    Map<String, Detail> dateToMap = detailService.list(detailWrapper).stream()
                            .collect(Collectors.toMap(Detail::getDealDate, Function.identity()));
                    for (Detail detail : details) {
                        if (dateToMap.containsKey(detail.getDealDate())) {
                            detail.setDetailId(dateToMap.get(detail.getDealDate()).getDetailId());
                        }
                    }
                    details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                    details.forEach(item -> item.calc());
                    Detail.calc(details);
                    detailService.saveOrUpdateBatch(details);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("下载股票详细数据  end \n\n\n");
    }


    private <T> T getResponse(String url, Map<String, String> paramMap, ParameterizedTypeReference<T> reference) {
        int cnt = 1;
        while (true) {
            try {
                ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.GET, null, reference, paramMap);
                return res.getBody();
            } catch (Exception e) {
                //打印除限流和连接错误外的错误
                if (!e.getMessage().startsWith("429") && !e.getMessage().startsWith("I/O error on GET")) {
                    log.info("调接口时发生错误{}", e.getMessage());
                    return null;
                }
                try {
                    Thread.sleep(5000);
                    cnt++;
                    if (cnt > 5) {
                        log.info("{}", e.getMessage());
                        break;
                    }
                } catch (Exception e1) {
                    break;
                }
            }
        }
        return null;
    }


    public void buildStrategyL1() throws ExecutionException, InterruptedException {
        strategyL1Service.remove(new QueryWrapper<>());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyEnum strategy : strategyL1Enums) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Integer> resDetailIds = new ArrayList<>();
                for (String stockCode : stockCodeList) {
                    List<Detail> details = codeToDetailMap.get(stockCode);
                    for (Detail detail : details) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getPricePert(), 0.097)
                                || detail.getDealDate().compareTo("202505") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategy.getFilterFunc().apply(detail)) {
                            resDetailIds.add(detail.getDetailId());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(resDetailIds) && resDetailIds.size() > 10) {
                    StrategyL1 strategyL1 = new StrategyL1(strategy.getCode(), resDetailIds);
                    strategyL1.fillOtherData();
                    strategyL1Service.save(strategyL1);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("策略层级 1 计算 - 结束");
    }
}
