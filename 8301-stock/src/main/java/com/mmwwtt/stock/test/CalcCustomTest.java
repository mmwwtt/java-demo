package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUitls;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDAO;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockCalcRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.impl.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Slf4j
@SpringBootTest
public class CalcCustomTest {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private StockDetailServiceImpl stockDetailService;

    @Resource
    private StrategyResultServiceImpl strategyResultService;

    @Resource
    private StockCalcResServiceImpl stockCalcResService;

    @Resource
    private StrategyWinServiceImpl strategyWinService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor middleThreadPool = GlobalThreadPool.getMiddleThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDAO stockCalcResDao;

    private final VoConvert voConvert = VoConvert.INSTANCE;

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        StockStrategy strategy = new StockStrategy("test_" + getTimeStr(), (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return isInRange(t0.getLowPrice(), subtract(t1.getLowPrice(), "0.02"), add(t1.getLowPrice(), "0.02"))
                    && isInRange(t0.getLowPrice(), subtract(t2.getLowPrice(), "0.02"), add(t2.getLowPrice(), "0.02"))
                    && lessThan(t0.getPosition20(), "0.2")
                    && moreThan(t0.getPricePert().abs(), "0.01")
                    && moreThan(t1.getPricePert().abs(), "0.01")
                    && moreThan(t2.getPricePert().abs(), "0.01");
        });
        Map<String, List<StockDetail>> resMap = calcByStrategy(List.of(strategy));
        resMap.forEach((strategyName, resList) -> resList.stream().filter(item -> item.getNext1().getIsDown()).limit(200).forEach(item -> {
            try {
                StockGuiUitls.genDetailImage(item, strategyName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        log.info("绘制完成");
    }


    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        calcByStrategy(List.of(

                new StockStrategy("test ", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getLowPrice(), subtract(t1.getLowPrice(), "0.02"), add(t1.getLowPrice(), "0.02"))
                            && isInRange(t0.getLowPrice(), subtract(t2.getLowPrice(), "0.02"), add(t2.getLowPrice(), "0.02"))
                            && lessThan(t0.getPosition20(), "0.2");
                })
        ));
    }

    public Map<String, List<StockDetail>> calcByStrategy(List<StockStrategy> strategyList) throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        LocalDateTime dataTime = LocalDateTime.now();
        List<List<Stock>> parts = stockService.getStockPart();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = stockDetailService.getStockDetail(stock.getCode(), null);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StockStrategy strategy : strategyList) {
                        for (int i = 0; i < stockDetails.size() - 60; i++) {
                            StockDetail stockDetail = stockDetails.get(i);
                            if (moreThan(stockDetail.getPricePert(), "0.097")
                                    || Objects.isNull(stockDetail.getNext1())
                                    || !strategy.getRunFunc().apply(stockDetail)) {
                                continue;
                            }
                            strategyToCalcMap.computeIfAbsent(strategy.getStrategyName(), v -> new ArrayList<>()).add(stockDetail);
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        strategyToCalcMap.forEach((strategyName, list) ->
                stockCalcResService.saveCalcRes(list, strategyName, dataTime, StockCalcRes.TypeEnum.DETAIL.getCode()));
        log.info("结束计算");
        return strategyToCalcMap;
    }
}

