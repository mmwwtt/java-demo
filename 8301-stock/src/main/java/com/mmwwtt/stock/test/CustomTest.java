package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUtils;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.codeToDetailMap;

/**
 * 自定义策略测试
 */
@SpringBootTest
@Slf4j
public class CustomTest {

    @Resource
    private StrategyWinServiceImpl strategyWinService;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();


    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        List<StrategyEnum> strategyEnums = List.of(
                new StrategyEnum("riseStrategy", "上涨预测_低吸型", (StockDetail d) -> {
                    return true;
                }),
                new StrategyEnum("pullback5ma", "五日线上方回调至五日线下", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3(), t4 = t0.getT4();
                    if (t1 == null || t2 == null || t3 == null || t4 == null) return false;
                    if (t0.getFiveDayLine() == null || t1.getFiveDayLine() == null) return false;
                    // 前4日收盘价都在各自五日线之上
                    boolean prevAbove = moreThan(t1.getEndPrice(), t1.getFiveDayLine())
                            && moreThan(t2.getEndPrice(), t2.getFiveDayLine())
                            && moreThan(t3.getEndPrice(), t3.getFiveDayLine())
                            && moreThan(t4.getEndPrice(), t4.getFiveDayLine());
                    // 今日收盘回调到五日线之下
                    boolean todayBelow = lessThan(t0.getEndPrice(), t0.getFiveDayLine());
                    return prevAbove && todayBelow;
                })
        );

        calcByStrategy(strategyEnums);
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图-自定义")
    public void startCalc5() throws ExecutionException, InterruptedException {
        StrategyEnum strategyEnumDemo = new StrategyEnum("testCode", "testName", (StockDetail t0) -> {
            return t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed() && t0.getT3().getIsRed() && t0.getT4().getIsRed()
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && lessThan(t0.getT1().getDealQuantity(), t0.getT2().getDealQuantity())
                    && lessThan(t0.getT2().getDealQuantity(), t0.getT3().getDealQuantity())
                    && lessThan(t0.getT3().getDealQuantity(), t0.getT4().getDealQuantity());
        });

        log.info("开始查找符合条件的数据");
        Map<StrategyEnum, List<StockDetail>> resMap = calcByStrategy(List.of(strategyEnumDemo));
        log.info("开始绘制");
        resMap.forEach((strategyEnum, resList) -> {
            List<StockDetail> curList = resList.stream().limit(200).toList();
            for (StockDetail detail : curList) {
                try {
                    StockGuiUtils.genDetailImage(detail, strategyEnum.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("绘制完成");
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        buildImg("11034 11078 01035", false);
    }


    public Map<StrategyEnum, List<StockDetail>> calcByStrategy(List<StrategyEnum> strategyList) throws ExecutionException, InterruptedException {
        Map<StrategyEnum, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<String> part : CommonService.stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (String stockCode : part) {
                    List<StockDetail> stockDetails = codeToDetailMap.get(stockCode);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StrategyEnum strategy : strategyList) {
                        for (int i = 0; i < stockDetails.size() - 60; i++) {
                            StockDetail stockDetail = stockDetails.get(i);
                            if (moreThan(stockDetail.getPricePert(), "0.097")
                                    || Objects.isNull(stockDetail.getNext1())
                                    || Objects.isNull(stockDetail.getT10())
                                    || Objects.isNull(stockDetail.getT10().getSixtyDayLine())
                                    || !strategy.getRunFunc().apply(stockDetail)) {
                                continue;
                            }
                            strategyToCalcMap.computeIfAbsent(strategy, v -> Collections.synchronizedList(new ArrayList<>())).add(stockDetail);
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        strategyToCalcMap.forEach((strategyEnum, list) -> {
            StrategyWin strategyWin = StrategyWin.createByStrategyName(strategyEnum);
            strategyWin.setLevel(0);
            list.forEach(strategyWin::addToResult);
            strategyWin.fillData1();
            strategyWin.fillData2();
            strategyWinService.save(strategyWin);
        });
        log.info("结束计算");
        return strategyToCalcMap;
    }

    private void buildImg(String strategyStr, Boolean onlyNext1IsUp) throws ExecutionException, InterruptedException {
        log.info("开始查找符合条件的数据");
        List<StrategyEnum> list = Arrays.stream(strategyStr.split(" ")).map(StrategyEnum.codeToEnumMap::get).toList();
        StrategyEnum strategy = new StrategyEnum("testCode", "test_" + getTimeStr(),
                (StockDetail t0) -> list.stream().allMatch(item -> item.getRunFunc().apply(t0)));
        Map<StrategyEnum, List<StockDetail>> resMap = calcByStrategy(List.of(strategy));
        log.info("开始绘制");
        resMap.forEach((strategyEnum, resList) -> {
            List<StockDetail> curList = onlyNext1IsUp
                    ? resList.stream().filter(item -> item.getNext1().getIsDown()).limit(200).toList()
                    : resList.stream().limit(200).toList();
            for (StockDetail detail : curList) {
                try {
                    StockGuiUtils.genDetailImage(detail, strategyEnum.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("绘制完成");
    }
}
