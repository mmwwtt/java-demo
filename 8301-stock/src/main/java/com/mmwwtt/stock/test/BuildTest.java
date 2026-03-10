package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUtils;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CalcCommonService;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
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
        String sql = "   five_max_perc_rate > 0.17";
        winList = strategyWinService.getStrategyWin(sql);
        winList.sort(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed());
    }

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        commonService.buildStrateResultLevel1();
    }

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260310", winList, false, 1.2);
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
        // 股票上涨预测策略 v2：低吸/刚启动型，避免追涨（原策略0.47胜率说明易追高）
        // 思路：超卖反弹 + 刚金叉 + 低位 + 缩量回调 或 温和放量突破
        List<StrategyEnum> strategyEnums = List.of(
                new StrategyEnum("riseStrategy", "上涨预测_低吸型", (StockDetail d) -> {
                    if (d.getT5() == null || d.getT10() == null || d.getSixtyDayLine() == null
                            || d.getT1() == null || d.getT2() == null || d.getT3() == null)
                        return false;

                    // 1. 位置偏低：20日区间 20%-70%，留出上涨空间，避免追高
                    boolean posLow = d.getPosition20() != null
                            && moreThan(d.getPosition20(), "0.2")
                            && lessThan(d.getPosition20(), "0.7");

                    // 2. 威廉指标超卖反弹：在超卖区(WR<-80) 或 刚脱离超卖(今日>-80且昨日<-80)
                    boolean wrRebound = d.getWr() != null
                            && (lessThan(d.getWr(), "-80")
                            || (d.getT1().getWr() != null && moreThan(d.getWr(), "-80") && lessThan(d.getT1().getWr(), "-80")));

                    // 3. MACD 刚金叉：今日DIF>DEA，前几日DIF<DEA
                    boolean macdGolden = d.getDif() != null && d.getDea() != null
                            && moreThan(d.getDif(), d.getDea())
                            && lessThan(d.getT1().getDif(), d.getT1().getDea())
                            && lessThan(d.getT2().getDif(), d.getT2().getDea());

                    // 4. 上穿20日线：刚突破均线压力
                    boolean cross20 = moreThan(d.getHighPrice(), d.getTwentyDayLine())
                            && lessThan(d.getLowPrice(), d.getTwentyDayLine())
                            && lessThan(d.getT1().getHighPrice(), d.getTwentyDayLine());

                    // 5. 均线有支撑：收盘在5日或10日线上方
                    boolean maSupport = moreThan(d.getEndPrice(), d.getFiveDayLine())
                            || moreThan(d.getEndPrice(), d.getTenDayLine());

                    // 组合：(超卖反弹 或 MACD金叉) + 位置偏低 + (上穿20日线 或 均线支撑)
                    // 优先缩量/阴线(低吸)，但不强制，避免样本过少
                    boolean signal = (wrRebound || macdGolden) && posLow;
                    boolean trend = cross20 || (maSupport && Boolean.TRUE.equals(d.getTenIsUp()));

                    return signal && trend;
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

    public Map<StrategyEnum, List<StockDetail>> calcByStrategy(List<StrategyEnum> strategyList) throws ExecutionException, InterruptedException {
        Map<StrategyEnum, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
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

    public void verifyPredictResByFiveMaxDetail() throws InterruptedException, ExecutionException {
        Map<String, Map<StrategyWin, List<StockDetail>>> dateResMap = new HashMap<>();
        BigDecimal cnt = BigDecimal.ZERO;
        int dayCnt = 0;
        for (String date : predictDateList) {
            log.info("\n\n日期：" + date);
            Map<StrategyWin, List<StockDetail>> resMap = calcCommonService.verifyPredictRes(date, winList);
            dateResMap.put(date, resMap);
            int all = 0;
            AtomicReference<BigDecimal> count = new AtomicReference<>(BigDecimal.ZERO);
            for (Map.Entry<StrategyWin, List<StockDetail>> entry : resMap.entrySet()) {
                List<StockDetail> details = entry.getValue();
                all += details.size();
                details.stream().filter(Objects::nonNull)
                        .filter(item -> Objects.nonNull(item.getNext5MaxPricePert()))
                        .forEach(item -> count.set(add(count.get(), item.getNext5MaxPricePert())));
            }
            BigDecimal res = divide(count.get(), all);
            if (res.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            dayCnt++;
            log.info(res.toString());
            cnt = add(cnt, res);
        }
        log.info("平均5日最高涨幅 {}", divide(cnt, dayCnt).toString());
    }
}
