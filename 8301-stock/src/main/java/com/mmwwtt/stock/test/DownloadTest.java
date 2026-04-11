package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.DetailDAO;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.impl.*;
import com.mmwwtt.stock.vo.DetailOnTimeVO;
import com.mmwwtt.stock.vo.DetailVO;
import com.mmwwtt.stock.vo.StockVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.moreThan;
import static com.mmwwtt.stock.common.Constants.*;
import static com.mmwwtt.stock.service.CommonDataService.*;

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

    @Resource
    private DetailDAO detailDAO;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CommonDataService commonDataService;

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
    @DisplayName("集成当日的数据")
    public void buildCurDateDetail() throws ExecutionException, InterruptedException {
        downDetailOnTime();
    }

    @Test
    @DisplayName("增量集成指定日期的数据 左闭右闭")
    public void buildDateDetail() throws ExecutionException, InterruptedException {
        //downDetail("20260409", "20260409");
        downDetail("20260406", NOW_DATA);
        commonDataService.init();
        buildStrategyL1();
    }


    @Test
    @DisplayName("从0开始构建数据")
    public void buildDetail() throws ExecutionException, InterruptedException {
            downLoadInit();
            downStock();
            downDetail();
            buildStrategyL1();
    }

    @DisplayName("重新生成L1曾策略")
    @Test
    public void buildStrategyL1() throws ExecutionException, InterruptedException {
        commonDataService.init();
        strategyL1Service.remove(new QueryWrapper<>());
        List<StrategyL1> allBaseL1 = CommonDataService.getAllBaseL1s();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyL1 strategyL1 : allBaseL1) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Detail> curDetails = new ArrayList<>(100000);
                for (String stockCode : stockCodeList) {
                    List<Detail> details = codeToDetailMap.get(stockCode);
                    for (Detail detail : details) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getRise0(), 0.097)
                                || detail.getDealDate().compareTo("202508") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategyL1.getFilterFunc().apply(detail)) {
                            curDetails.add(detail);
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(curDetails) && curDetails.size() > 100) {
                    strategyL1.setDetails(curDetails);
                    strategyL1.fillOtherData();
                    strategyL1Service.save(strategyL1);
                    allBaseL1.remove(strategyL1);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        log.info("策略层级 1 计算 - 结束");
    }

    public void downLoadInit() {
        stockService.remove(new QueryWrapper<>());
        detailService.remove(new QueryWrapper<>());
        detailDAO.resetAutoIncrement();
        log.info("清空表 end\n");
    }


    /**
     * 获取2025以来的数据
     */
    public void downDetail() throws ExecutionException, InterruptedException {
        downDetail("20250101", NOW_DATA);
    }

    /**
     * 获取日期区间的详情数据
     */
    public void downDetail(String startData, String endData) throws InterruptedException, ExecutionException {
        List<Stock> stockList = stockService.list();
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
                    map1.put(START_DATA, startData);
                    map1.put(END_DATA, endData);
                    map1.put(MAX_SIZE, "500");
                    log.info("获取详情数据-{}", stock.getCode());
                    List<DetailVO> detailVOS = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<DetailVO>>() {
                    });
                    if (Objects.isNull(detailVOS)) {
                        continue;
                    }
                    String stockCode = stock.getCode();
                    detailVOS = detailVOS.stream()
                            .peek(item -> item.setStockCode(stockCode))
                            .filter(item -> item.getSf() == 0)
                            .collect(Collectors.toList());
                    List<Detail> details = voConvert.convertToDetail(detailVOS);
                    Set<String> dateSet = details.stream().map(Detail::getDealDate).collect(Collectors.toSet());

                    QueryWrapper<Detail> wrapper = new QueryWrapper<>();
                    wrapper.eq("stock_code", stock.getCode());
                    List<Detail> source = detailService.list(wrapper);
                    List<Integer> removeIds = new ArrayList<>();
                    source.removeIf(item -> {
                        if (dateSet.contains(item.getDealDate())) {
                            removeIds.add(item.getDetailId());
                            return true;
                        }
                        return false;
                    });
                    detailService.removeByIds(removeIds);
                    source.addAll(details);

                    source.sort(Comparator.comparing(Detail::getDealDate).reversed());
                    source.forEach(item -> item.calc());
                    Detail.calc(source);

                    detailService.saveBatch(source.stream().filter(item -> Objects.isNull(item.getDetailId())).toList());
                    detailService.updateBatchById(source.stream().filter(item -> Objects.nonNull(item.getDetailId())).toList());
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        log.info("下载股票详细数据  end \n\n\n");
    }


    /**
     * 获取今天最新的详情数据
     */
    public void downDetailOnTime() throws InterruptedException, ExecutionException {
        Map<String, String> map1 = new HashMap<>();
        map1.put(LICENCE, BI_YING_LICENCE);
        List<DetailOnTimeVO> detailVOS = getResponse(ON_TIME_DATA_URL, map1, new ParameterizedTypeReference<List<DetailOnTimeVO>>() {
        });
        List<Detail> details = voConvert.convertToDetailList(detailVOS);
        List<List<Detail>> parts = Lists.partition(details, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Detail> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Detail detail : part) {
                    QueryWrapper<Detail> wrapper = new QueryWrapper<>();
                    wrapper.likeRight("stock_code", detail.getStockCode());
                    List<Detail> source = detailService.list(wrapper);
                    if(CollectionUtils.isEmpty(source)) {
                        continue;
                    }
                    List<Integer> removeIds = new ArrayList<>();
                    source.removeIf(item -> {
                        if (Objects.equals(detail.getDealDate().substring(0,8), item.getDealDate().substring(0,8))) {
                            removeIds.add(item.getDetailId());
                            return true;
                        }
                        return false;
                    });
                    detailService.removeByIds(removeIds);
                    source.add(detail);

                    source.sort(Comparator.comparing(Detail::getDealDate).reversed());
                    source.forEach(item -> item.calc());
                    Detail.calc(source);
                    source = source.stream().limit(65).collect(Collectors.toList());
                    detailService.saveBatch(source.stream().filter(item -> Objects.isNull(item.getDetailId())).toList());
                    detailService.updateBatchById(source.stream().filter(item -> Objects.nonNull(item.getDetailId())).toList());
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
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
                    Thread.sleep(5);
                    cnt++;
                    if (cnt > 3) {
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

    @Test
    @DisplayName("调接口获取数据")
    public void downStock() {
        stockService.remove(new QueryWrapper<>());
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
        stockService.saveBatch(stockList);
        log.info("下载股票列表数据 end");
    }
}
