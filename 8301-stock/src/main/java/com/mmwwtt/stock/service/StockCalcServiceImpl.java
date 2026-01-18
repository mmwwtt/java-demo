package com.mmwwtt.stock.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockCalcRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
@Slf4j
public class StockCalcServiceImpl implements StockCalcService {

    @Resource
    private StockDao stockDao;

    @Resource
    private StockDetailDao stockDetailDao;

    @Resource
    private StockCalcResDao stockCalcResDao;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * 开始计算   通过上/下影线占比  和涨跌百分比区间 计算预测胜率
     */
    @Override
    public void startCalc1() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> codeToDetailMap = getCodeToDetailMap();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        final BigDecimal tenPert = new BigDecimal("0.1");
        log.info("开始计算");
        LocalDateTime dataTime = LocalDateTime.now();
        for (BigDecimal upShadowLowLimit = BigDecimal.ZERO; upShadowLowLimit.compareTo(BigDecimal.ONE) < 0; upShadowLowLimit = upShadowLowLimit.add(tenPert)) {
            for (BigDecimal upShadowUpLimit = BigDecimal.ZERO; upShadowUpLimit.compareTo(BigDecimal.ONE) < 0; upShadowUpLimit = upShadowUpLimit.add(tenPert)) {
                if (upShadowLowLimit.compareTo(upShadowUpLimit) >= 0) {
                    continue;
                }
                for (BigDecimal lowShadowLowLimit = BigDecimal.ZERO; lowShadowLowLimit.compareTo(BigDecimal.ONE) < 0; lowShadowLowLimit =
                        lowShadowLowLimit.add(tenPert)) {
                    for (BigDecimal lowShadowUpLimit = BigDecimal.ZERO; lowShadowUpLimit.compareTo(BigDecimal.ONE) < 0; lowShadowUpLimit =
                            lowShadowUpLimit.add(tenPert)) {
                        if (lowShadowLowLimit.compareTo(lowShadowUpLimit) >= 0) {
                            continue;
                        }
                        BigDecimal finalUpShadowLowLimit = upShadowLowLimit;
                        BigDecimal finalUpShadowUpLimit = upShadowUpLimit;
                        BigDecimal finalLowShadowLowLimit = lowShadowLowLimit;
                        BigDecimal finalLowShadowUpLimit = lowShadowUpLimit;
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            for (BigDecimal pricePertLowLimit = new BigDecimal("-0.1"); pricePertLowLimit.compareTo(tenPert) < 0; pricePertLowLimit =
                                    pricePertLowLimit.add(new BigDecimal("0.01"))) {
                                for (BigDecimal pricePertUpLimit = new BigDecimal("-0.1"); pricePertUpLimit.compareTo(tenPert) < 0; pricePertUpLimit =
                                        pricePertUpLimit.add(new BigDecimal("0.01"))) {
                                    if (pricePertLowLimit.compareTo(pricePertUpLimit) >= 0) {
                                        continue;
                                    }
                                    BigDecimal finalPricePertLowLimit = pricePertLowLimit;
                                    BigDecimal finalPricePertUpLimit = pricePertUpLimit;
                                    List<StockDetail> allRes = new ArrayList<>();
                                    codeToDetailMap.forEach((stockCode, detailList) -> {
                                        for (int i = 0; i < detailList.size(); i++) {
                                            if (!filterForMoreUpShadowPert(detailList.get(i), finalUpShadowLowLimit, ModeEnum.MORE)) {
                                                continue;
                                            }
                                            if (!filterForMoreUpShadowPert(detailList.get(i), finalUpShadowUpLimit, ModeEnum.LESS)) {
                                                continue;
                                            }
                                            if (!filterForMoreLowShadowPert(detailList.get(i), finalLowShadowLowLimit, ModeEnum.MORE)) {
                                                continue;
                                            }
                                            if (!filterForMoreLowShadowPert(detailList.get(i), finalLowShadowUpLimit, ModeEnum.LESS)) {
                                                continue;
                                            }
                                            if (!filterForMorePricePert(detailList.get(i), finalPricePertLowLimit, ModeEnum.MORE)) {
                                                continue;
                                            }
                                            if (!filterForMorePricePert(detailList.get(i), finalPricePertUpLimit, ModeEnum.LESS)) {
                                                continue;
                                            }

                                            //没有第二天的结论数据 则跳过
                                            if (i - 1 < 0) {
                                                continue;
                                            }
                                            allRes.add(detailList.get(i - 1));
                                        }
                                    });
                                    if (CollectionUtils.isEmpty(allRes)) {
                                        return;
                                    }
                                    StockStrategy stockStrategy = StockStrategy.builder()
                                            .upShadowLowLimit(finalUpShadowLowLimit)
                                            .upShadowUpLimit(finalUpShadowUpLimit)
                                            .lowShadowLowLimit(finalLowShadowLowLimit)
                                            .lowShadowUpLimit(finalLowShadowUpLimit)
                                            .pricePertLowLimit(finalPricePertLowLimit)
                                            .pricePertUpLimit(finalPricePertUpLimit)
                                            .build();
                                    String strategyDesc = JSON.toJSONString(stockStrategy);
                                    saveCalcRes(allRes, strategyDesc, dataTime, StockCalcRes.TypeEnum.INTERVAL.getCode());
                                }
                            }
                        });
                        futures.add(future);
                    }
                }
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        List<StockCalcRes> needDelList = getDelListBySmallInterval();
        stockCalcResDao.deleteByIds(needDelList);
        log.info("结束计算");
    }

    /**
     * 缩小区间  [0.5,0.6]   和[0.5,0.8] 的胜率都是0.6        则[0.5，0.8] 是无效的
     */
    public List<StockCalcRes> getDelListBySmallInterval() {
        QueryWrapper<StockCalcRes> wrapper = new QueryWrapper<>();
        wrapper.apply("create_date = (SELECT MAX(create_date) FROM stock_calculation_result_t)")
                .orderByDesc("win_rate")
                .orderByDesc("all_cnt");
        List<StockCalcRes> list = stockCalcResDao.selectList(wrapper);

        List<StockCalcRes> resList = new ArrayList<>();
        list.forEach(item -> item.setStockStrategy(JSON.toJavaObject(JSON.parseObject(item.getStrategyDesc()), StockStrategy.class)));
        list.forEach(res -> {
            StockCalcRes stockCalcRes = list.stream()
                    .filter(item -> !Objects.equals(item.getCalcResId(), res.getCalcResId()))
                    .filter(item -> item.getWinRate().compareTo(res.getWinRate()) == 0)
                    .filter(item -> Objects.equals(item.getAllCnt(), res.getAllCnt()))
                    .filter(item -> item.getStockStrategy().getLowShadowLowLimit().compareTo(res.getStockStrategy().getLowShadowLowLimit()) >= 0)
                    .filter(item -> item.getStockStrategy().getLowShadowUpLimit().compareTo(res.getStockStrategy().getLowShadowUpLimit()) <= 0)
                    .filter(item -> item.getStockStrategy().getUpShadowLowLimit().compareTo(res.getStockStrategy().getUpShadowLowLimit()) >= 0)
                    .filter(item -> item.getStockStrategy().getUpShadowUpLimit().compareTo(res.getStockStrategy().getUpShadowUpLimit()) <= 0)
                    .filter(item -> item.getStockStrategy().getPricePertLowLimit().compareTo(res.getStockStrategy().getPricePertLowLimit()) >= 0)
                    .filter(item -> item.getStockStrategy().getPricePertUpLimit().compareTo(res.getStockStrategy().getPricePertUpLimit()) <= 0)
                    .findFirst().orElse(null);
            if (Objects.nonNull(stockCalcRes)) {
                resList.add(res);
            }
        });
        return resList;
    }


    /**
     * 查询最新的计算结果信息
     */
    public List<StockCalcRes> getNewCalcRes() {
        QueryWrapper<StockCalcRes> wrapper = new QueryWrapper<>();
        wrapper.apply("create_date = (SELECT MAX(create_date) FROM stock_calculation_result_t)")   // 子查询
                .orderByDesc("win_rate")                   // 第一排序字段
                .orderByDesc("all_cnt");                   // 第二排序字段
        return stockCalcResDao.selectList(wrapper);
    }

    @Getter
    @AllArgsConstructor
    public enum ModeEnum implements BaseEnum {
        MORE("MORE", "多余阈值模式"),
        LESS("LESS", "低于阈值模式");
        private final String code;
        private final String desc;

    }

    //MORE: 上影线大于阈值的返回true
    private boolean filterForMoreUpShadowPert(StockDetail stockDetail, BigDecimal limit, ModeEnum enumm) {
        return Objects.equals(ModeEnum.MORE, enumm)
                ? stockDetail.getUpShadowPert().compareTo(limit) > 0
                : stockDetail.getUpShadowPert().compareTo(limit) < 0;
    }


    //MORE:  下影线大于阈值的返回true
    private boolean filterForMoreLowShadowPert(StockDetail stockDetail, BigDecimal limit, ModeEnum enumm) {
        return Objects.equals(ModeEnum.MORE, enumm)
                ? stockDetail.getLowShadowPert().compareTo(limit) > 0
                : stockDetail.getLowShadowPert().compareTo(limit) < 0;
    }

    //MORE:  实体长度大于阈值的返回true
    private boolean filterForMoreEntityPert(StockDetail stockDetail, BigDecimal limit, ModeEnum enumm) {
        return Objects.equals(ModeEnum.MORE, enumm)
                ? stockDetail.getEntityPert().compareTo(limit) > 0
                : stockDetail.getEntityPert().compareTo(limit) < 0;
    }

    //MORE: 涨跌幅大于阈值的返回true
    private boolean filterForMorePricePert(StockDetail stockDetail, BigDecimal limit, ModeEnum enumm) {
        return Objects.equals(ModeEnum.MORE, enumm)
                ? stockDetail.getPricePert().compareTo(limit) > 0
                : stockDetail.getPricePert().compareTo(limit) < 0;
    }

    /**
     * MORE: 判断 成交量 持续放大则返回true
     *
     * @param curIdx       当前日期
     * @param continueDays 持续天数
     */
    private boolean filterForMoreQuentity(List<StockDetail> list, int curIdx, int continueDays, ModeEnum enumm) {
        if (curIdx < 0 || list.size() <= curIdx + continueDays) {
            return false;
        }
        for (int i = 0; i < continueDays; i++) {
            if (Objects.equals(enumm, ModeEnum.MORE)) {
                //比前一天小则返回false
                if (list.get(curIdx + i).getDealQuantity().compareTo(list.get(curIdx + i + 1).getDealQuantity()) < 0) {
                    return false;
                }
            } else {
                //比前一天大则返回false
                if (list.get(curIdx + i).getDealQuantity().compareTo(list.get(curIdx + i + 1).getDealQuantity()) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断 涨跌成交比(涨幅/成交量)  持续放大则返回true
     *
     * @param curIdx       当前日期
     * @param continueDays 持续天数
     */
    private boolean filterForMorePertDivisionQuentity(List<StockDetail> list, int curIdx, int continueDays) {
        if (curIdx < 0 || list.size() <= curIdx + continueDays) {
            return false;
        }
        for (int i = 0; i < continueDays; i++) {
            //比前一天小则直接  返回false
            if (list.get(curIdx + i).getPertDivisionQuantity().compareTo(list.get(curIdx + i + 1).getPertDivisionQuantity()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 开始计算
     */
    @Override
    public void startCalc2() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        LocalDateTime dataTime = LocalDateTime.now();
        List<Stock> stockList = getAllStock();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = getStockDetail(stock.getCode(), null);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StockStrategy strategy : StockStrategyUtils.STRATEGY_LIST) {
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

        strategyToCalcMap.forEach((strategyName, list) -> {
            saveCalcRes(list, strategyName, dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());
        });
        log.info("结束计算");
    }


    @Override
    public void startCalc3() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> codeToDetailMap = getCodeToDetailMap();
        LocalDateTime dataTime = LocalDateTime.now();
        String strategyName = "底部阳包阴 放量";
        for (StockStrategy strategy : StockStrategyUtils.STRATEGY_LIST) {
            if (!Objects.equals(strategy.getStrategyName(), strategyName)) {
                continue;
            }
            List<StockDetail> allAfterList = new ArrayList<>();
            codeToDetailMap.forEach((stockCode, detailList) -> {
                if (detailList.size() < 60) {
                    return;
                }
                List<StockDetail> afterList = detailList.subList(0, detailList.size() - 60).stream()
                        .filter(item -> item.getPricePert().compareTo(new BigDecimal("0.097")) < 0)
                        .filter(item -> Objects.nonNull(item.getNext1()))
                        .filter(item -> strategy.getRunFunc().apply(item)).toList();
                allAfterList.addAll(afterList);
            });
            saveCalcRes(allAfterList, strategy.getStrategyName(), dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());

            allAfterList.stream().filter(item -> item.getNext1().getIsDown()).limit(1000).forEach(item -> {
                try {
                    StockGuiUitls.genDetailImage(item, strategy.getStrategyName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    @Override
    public void startCalc4() throws ExecutionException, InterruptedException {
        Map<String, List<StockDetail>> codeToDetailMap = getCodeToDetailMap();
        LocalDateTime dataTime = LocalDateTime.now();

        StockStrategy strategy = new StockStrategy("test", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            return t0.getIsUp() && t1.getIsUp() && t2.getIsUp()
                    && moreThan(t1.getEndPrice(), t2.getEndPrice()) && moreThan(t1.getStartPrice(), t2.getStartPrice())
                    && moreThan(t0.getEndPrice(), t1.getEndPrice()) && moreThan(t0.getStartPrice(), t1.getStartPrice())
                    && moreThan(t1.getDealQuantity(), t2.getDealQuantity())
                    && moreThan(t0.getDealQuantity(), t1.getDealQuantity());
        });
        List<StockDetail> allAfterList = new ArrayList<>();
        codeToDetailMap.forEach((stockCode, detailList) -> {
            if (detailList.size() < 60) {
                return;
            }
            List<StockDetail> afterList = detailList.subList(0, detailList.size() - 60).stream()
                    .filter(item -> item.getPricePert().compareTo(new BigDecimal("0.097")) < 0)
                    .filter(item -> Objects.nonNull(item.getNext1()))
                    .filter(item -> strategy.getRunFunc().apply(item)).toList();
            allAfterList.addAll(afterList);
        });
        saveCalcRes(allAfterList, strategy.getStrategyName(), dataTime, StockCalcRes.TypeEnum.DETAIL.getCode());

    }

    @Override
    public List<Stock> getAllStock() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("tt");
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);

        //30开头是创业板  68开头是科创版  不参与
        stockList = stockList.stream()
                .filter(stock -> !stock.getCode().startsWith("30")
                        && !stock.getCode().startsWith("68")
                        && !stock.getName().contains("ST"))
                .toList();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return stockList;
    }

    public List<StockDetail> genAllStockDetail(List<StockDetail> stockDetails) {
        for (int i = 0; i < stockDetails.size(); i++) {
            StockDetail t0 = stockDetails.get(i);
            List<Pair<Integer, Consumer<StockDetail>>> pairList = new ArrayList<>();
            pairList.add(Pair.of(i - 1, t0::setNext1));
            pairList.add(Pair.of(i - 2, t0::setNext2));
            pairList.add(Pair.of(i - 3, t0::setNext3));
            pairList.add(Pair.of(i - 4, t0::setNext4));
            pairList.add(Pair.of(i - 5, t0::setNext5));
            pairList.add(Pair.of(i - 10, t0::setNext10));
            pairList.add(Pair.of(i + 1, t0::setT1));
            pairList.add(Pair.of(i + 2, t0::setT2));
            pairList.add(Pair.of(i + 3, t0::setT3));
            pairList.add(Pair.of(i + 4, t0::setT4));
            pairList.add(Pair.of(i + 5, t0::setT5));
            for (Pair<Integer, Consumer<StockDetail>> pair : pairList) {
                Integer idx = pair.getLeft();
                if (0 <= idx && idx < stockDetails.size()) {
                    StockDetail tmp = StockConverter.INSTANCE.convertToStockDetail(stockDetails.get(idx));
                    pair.getRight().accept(tmp);
                }
            }
        }
        return stockDetails;
    }

    @Override
    public List<StockDetail> getStockDetail(String stockCode, Integer limit) {
        QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
        detailWapper.eq("stock_code", stockCode);
        detailWapper.orderByDesc("deal_date");
        if (Objects.nonNull(limit)) {
            detailWapper.last("LIMIT " + limit);
        }
        List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
        return genAllStockDetail(stockDetails);
    }

    public void saveCalcRes(List<StockDetail> allAfterList, String strategyDesc, LocalDateTime dataTime, String type) {
        if (CollectionUtils.isEmpty(allAfterList)) {
            return;
        }

        BigDecimal winPriceRateSum = BigDecimal.ZERO;
        int winCnt = 0;
        BigDecimal onePriceRateSum = BigDecimal.ZERO;
        int oneCnt = 0;
        BigDecimal twoPriceRateSum = BigDecimal.ZERO;
        int twoCnt = 0;
        BigDecimal threePriceRateSum = BigDecimal.ZERO;
        int threeCnt = 0;
        BigDecimal fourPriceRateSum = BigDecimal.ZERO;
        int fourCnt = 0;
        BigDecimal fivePriceRateSum = BigDecimal.ZERO;
        BigDecimal fiveMaxPriceRateSum = BigDecimal.ZERO;
        int fiveCnt = 0;
        BigDecimal tenPriceRateSum = BigDecimal.ZERO;
        BigDecimal tenMaxPriceRateSum = BigDecimal.ZERO;
        int tenCnt = 0;

        for (StockDetail stockDetail : allAfterList) {
            if (Objects.nonNull(stockDetail.getNext1())) {
                onePriceRateSum = add(onePriceRateSum, stockDetail.getNext1().getPricePert());
                oneCnt++;
                if (stockDetail.getNext1().getIsUp()) {
                    winPriceRateSum = add(winPriceRateSum, stockDetail.getNext1().getPricePert());
                    winCnt++;
                }
            }
            if (Objects.nonNull(stockDetail.getNext2())) {
                twoPriceRateSum = add(twoPriceRateSum, stockDetail.getNext2().getPricePert());
                twoCnt++;
            }

            if (Objects.nonNull(stockDetail.getNext3())) {
                threePriceRateSum = add(threePriceRateSum, stockDetail.getNext3().getPricePert());
                threeCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext4())) {
                fourPriceRateSum = add(fourPriceRateSum, stockDetail.getNext4().getPricePert());
                fourCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext5())) {
                fivePriceRateSum = add(fivePriceRateSum, stockDetail.getNext5().getPricePert());
                fiveMaxPriceRateSum = add(fiveMaxPriceRateSum, stockDetail.getNext5().getNext5MaxPricePert());
                fiveCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext10())) {
                tenPriceRateSum = add(tenPriceRateSum, stockDetail.getNext10().getPricePert());
                tenMaxPriceRateSum = add(tenMaxPriceRateSum, stockDetail.getNext10().getNext10MaxPricePert());
                tenCnt++;
            }
        }

        StockCalcRes calcRes = new StockCalcRes();
        calcRes.setStrategyDesc(strategyDesc);
        calcRes.setWinRate(divide(winCnt, oneCnt));
        calcRes.setWinPercRate(divide(winPriceRateSum, winCnt));
        calcRes.setPercRate(divide(onePriceRateSum, oneCnt));
        calcRes.setTwoPercRate(divide(twoPriceRateSum, twoCnt));
        calcRes.setThreePercRate(divide(threePriceRateSum, threeCnt));
        calcRes.setFourPercRate(divide(fourPriceRateSum, fourCnt));
        calcRes.setFivePercRate(divide(fivePriceRateSum, fiveCnt));
        calcRes.setTenPercRate(divide(tenPriceRateSum, tenCnt));
        calcRes.setTenMaxPercRate(divide(tenMaxPriceRateSum, tenCnt));
        calcRes.setFiveMaxPercRate(divide(fiveMaxPriceRateSum, fiveCnt));
        calcRes.setCreateDate(dataTime);
        calcRes.setAllCnt(allAfterList.size());
        calcRes.setType(type);

        stockCalcResDao.insert(calcRes);
    }

    @Override
    public Map<String, List<StockDetail>> getCodeToDetailMap() throws ExecutionException, InterruptedException {
        return getCodeToDetailMap(null);
    }

    @Override
    public Map<String, List<StockDetail>> getCodeToDetailMap(Integer limit) throws ExecutionException, InterruptedException {
        List<Stock> stockList = getAllStock();
        Map<String, List<StockDetail>> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = getStockDetail(stock.getCode(), limit);
                    codeToDetailMap.put(stock.getCode(), stockDetails);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("开始查询数据-结束");
        return codeToDetailMap;
    }
}
