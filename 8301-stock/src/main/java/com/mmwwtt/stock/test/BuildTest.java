package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUitls;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.*;
import com.mmwwtt.stock.vo.StrategyWinVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.predictDateList;

@SpringBootTest
@Slf4j
public class BuildTest {

    @Resource
    private StrategyWinServiceImpl strategyWinService;

    @Resource
    private CommonService commonService;

    @Resource
    private CalcCommonService calcCommonService;

    private static List<StrategyWin> winList;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    @PostConstruct
    public void init() {
        StrategyWinVO strategyWin = new StrategyWinVO();
        strategyWin.setFiveMaxPercRateStart(new BigDecimal("0.14"));
   //     strategyWin.setFiveMaxPercRateEnd(new BigDecimal("0.13"));
  //      strategyWin.setFivePercRateStart(new BigDecimal("0.05"));
    //    strategyWin.setTenMaxPercRateStart(new BigDecimal("0.19"));
        winList = strategyWinService.getStrategyWin(strategyWin);

        winList.sort(Comparator.comparing(StrategyWin::getWinRate).reversed());
    }

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        commonService.buildStrateResultLevel1();
    }

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260224", winList, false, 1.2);
    }

    @Test
    @DisplayName("验证策略预测-5max")
    public void verifyPredictResByFiveMax() throws ExecutionException, InterruptedException {
        verifyPredictResByFiveMaxDetail();
    }


    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        buildImg("11034 11078 01035", false);
    }


    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        List<StrategyEnum> strategyEnums = List.of(
                new StrategyEnum("testCode", "testName", (StockDetail t0) -> {
                    return t0.getSixtyIsUp()
                            && isInRangeNotEquals(t0.getPosition40(), "0.2", "0.3")
                            && isInRangeNotEquals(t0.getLowShadowLen(), "0.08", "0.09");
                })
        );

        calcByStrategy(strategyEnums);
    }

    public Map<String, List<StockDetail>> calcByStrategy(List<StrategyEnum> strategyList) throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<String> part : CommonService.stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (String stockCode : part) {
                    List<StockDetail> stockDetails = CommonService.codeToDetailMap.get(stockCode);
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
                            strategyToCalcMap.computeIfAbsent(strategy.getName(), v -> new ArrayList<>()).add(stockDetail);
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        strategyToCalcMap.forEach((strategyName, list) -> {
            StrategyWin strategyWin = StrategyWin.createByStrategyName(strategyName);
            strategyWin.setLevel(0);
            list.forEach(strategyWin::addToResult);
            strategyWin.fillData();
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
        Map<String, List<StockDetail>> resMap = calcByStrategy(List.of(strategy));
        log.info("开始绘制");
        resMap.forEach((strategyName, resList) -> {
            List<StockDetail> curList = onlyNext1IsUp
                    ? resList.stream().filter(item -> item.getNext1().getIsDown()).limit(200).toList()
                    : resList.stream().limit(200).toList();
            for (StockDetail detail : curList) {
                try {
                    StockGuiUitls.genDetailImage(detail, strategyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("绘制完成");
    }

    public void verifyPredictResByFiveMaxDetail() throws InterruptedException, ExecutionException {
        Map<String, Map<StrategyWin, List<StockDetail>>> dateResMap = new HashMap<>();
        BigDecimal cnt = BigDecimal.ZERO;
        int dayCnt=0;
        for (String date : predictDateList) {
            log.info("\n\n日期：" + date);
            Map<StrategyWin, List<StockDetail>> resMap = calcCommonService.verifyPredictRes(date, winList);
            dateResMap.put(date, resMap);
            int all = 0;
            AtomicReference<BigDecimal> count = new AtomicReference<>(BigDecimal.ZERO);
            for (Map.Entry<StrategyWin, List<StockDetail>> entry : resMap.entrySet()) {
                List<StockDetail> details = entry.getValue();
                all += details.size();
                details.stream().filter(item -> Objects.nonNull(item.getNext5MaxPricePert()))
                        .forEach(item -> count.set(add(count.get(), item.getNext5MaxPricePert())));
            }
            BigDecimal res = divide(count.get(), all);
            if (res.compareTo(BigDecimal.ZERO)==0) {
                continue;
            }
            dayCnt++;
            log.info(res.toString());
            cnt = add(cnt, res);
        }
        log.info("平均5日最高涨幅 {}", divide(cnt, dayCnt).toString());
    }
}
