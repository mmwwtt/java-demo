package com.mmwwtt.stock.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.*;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

@Service
@Slf4j
public class StockStartService {

    @Resource
    private StockDao stockDao;

    @Resource
    private StockDetailDao stockDetailDao;

    @Resource
    private StockCalcResDao stockCalcResDao;

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    private RestTemplate restTemplate = new RestTemplate();


    /**
     * 开始计算   通过上/下影线占比  和涨跌百分比区间 计算预测胜率
     */
    public void startCalc1() throws ExecutionException, InterruptedException {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        Map<String, List<StockDetail>> codeToDetailMap = new HashMap<>();
        log.info("开始查询股票数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //30开头是创业板  68开头是科创版
                if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68") || stock.getName().contains("ST")) {
                    return;
                }
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode());
                detailWapper.orderByDesc("deal_date");
                //最新的日期在最前面
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                codeToDetailMap.put(stock.getCode(), stockDetails);

            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        final BigDecimal tenPert = new BigDecimal("0.1");
        log.info("开始计算");
        futures = new ArrayList<>();
        LocalDateTime dataTime = LocalDateTime.now();
        final List<StockCalcRes> stockCalcResList = new ArrayList<>();
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
                                    List<OneRes> allRes = new ArrayList<>();
                                    codeToDetailMap.forEach((stockCode, detailList) -> {
                                        List<OneRes> oneResList = new ArrayList<>();
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
                                            oneResList.add(new OneRes(detailList.get(i - 1)));
                                        }
                                        allRes.addAll(oneResList);
                                    });
                                    if (CollectionUtils.isEmpty(allRes)) {
                                        return;
                                    }
                                    long correctCount = allRes.stream().filter(OneRes::getIsCorrect).count();
                                    BigDecimal percRate = allRes.stream().filter(OneRes::getIsCorrect).map(OneRes::getPricePert)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                                            .divide(new BigDecimal(5), 4, RoundingMode.HALF_UP);
                                    BigDecimal winRate = new BigDecimal(correctCount).divide(new BigDecimal(allRes.size()), 4, RoundingMode.HALF_UP);
                                    if (winRate.compareTo(new BigDecimal("0.5")) < 0) {
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
                                    StockCalcRes calcRes = new StockCalcRes();
                                    String strategyDesc = JSON.toJSONString(stockStrategy);
                                    calcRes.setStrategyDesc(strategyDesc);
                                    calcRes.setStockStrategy(stockStrategy);
                                    calcRes.setWinRate(winRate);
                                    calcRes.setPercRate(percRate.divide(new BigDecimal(allRes.size()), 4, RoundingMode.HALF_UP));
                                    calcRes.setCreateDate(dataTime);
                                    calcRes.setAllCnt(allRes.size());
                                    calcRes.setType(StockCalcRes.TypeEnum.INTERVAL.getCode());
                                    log.info(strategyDesc);
                                    stockCalcResList.add(calcRes);
                                }
                            }
                        });
                        futures.add(future);
                    }
                }
            }
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        stockCalcResDao.insert(stockCalcResList);
        List<StockCalcRes> needDelList = getDelListBySmallInterval(stockCalcResList);
        stockCalcResDao.deleteByIds(needDelList);
        log.info("结束计算");
    }

    /**
     * 缩小区间  [0.5,0.6]   和[0.5,0.8] 的胜率都是0.6        则[0.5，0.8] 是无效的
     */
    public List<StockCalcRes> getDelListBySmallInterval(List<StockCalcRes> list) {
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
     *
     * @return
     */
    public List<StockCalcRes> getNewCalcRes() {
        QueryWrapper<StockCalcRes> wrapper = new QueryWrapper<>();
        wrapper.apply("create_date = (SELECT MAX(create_date) FROM stock_calculation_result_t)")   // 子查询
                .orderByDesc("win_rate")                   // 第一排序字段
                .orderByDesc("all_cnt");                   // 第二排序字段
        List<StockCalcRes> list = stockCalcResDao.selectList(wrapper);
        return list;
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
     * @param list
     * @param curIdx       当前日期
     * @param continueDays 持续天数
     * @return
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
     * @param list
     * @param curIdx       当前日期
     * @param continueDays 持续天数
     * @return
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
    public void startCalc2() throws ExecutionException, InterruptedException {
        List<Stock> stockList = getAllStock();
        Map<String, List<StockDetail>> codeToDetailMap = new HashMap<>();
        log.info("开始查询股票数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 100);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    List<StockDetail> stockDetails = getAllStockDetail(stock.getCode());
                    codeToDetailMap.put(stock.getCode(), stockDetails);
                }
            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();


        log.info("开始计算");
        futures = new ArrayList<>();
        LocalDateTime dataTime = LocalDateTime.now();
        List<Pair<String, Function<StockDetail, Boolean>>> strategyList = StockStrategyUtils.STRATEGY_LIST;

        for (Pair<String, Function<StockDetail, Boolean>> strategy : strategyList) {
            String desc = strategy.getLeft();
            Function<StockDetail, Boolean> func = strategy.getRight();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<StockDetail> allAfterList = new ArrayList<>();
                codeToDetailMap.forEach((stockCode, detailList) -> {
                    List<StockDetail> afterList = detailList.stream()
                            .filter(item -> Objects.nonNull(item.getNext()))
                            .filter(func::apply).toList();
                    allAfterList.addAll(afterList);
                });
                saveCalcRes(allAfterList, desc, dataTime);
            }, pool);
            futures.add(future);
        }
        allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("结束计算");
    }

    public List<Stock> getAllStock() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        //30开头是创业板  68开头是科创版  不参与
        stockList = stockList.stream()
                .filter(stock -> !stock.getCode().startsWith("30")
                        && !stock.getCode().startsWith("68")
                        && !stock.getName().contains("ST"))
                .toList();
        return stockList;
    }

    public List<StockDetail> getAllStockDetail(String stockCode) {
        QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
        detailWapper.eq("stock_code", stockCode);
        detailWapper.orderByDesc("deal_date");
        List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
        for (int i = 0; i < stockDetails.size(); i++) {
            StockDetail t0 = stockDetails.get(i);
            if (i - 1 > 0) {
                t0.setNext(stockDetails.get(i - 1));
            }
            if (stockDetails.size() > i + 1) {
                t0.setT1(stockDetails.get(i + 1));
            }
            if (stockDetails.size() > i + 2) {
                t0.setT2(stockDetails.get(i + 2));
            }
            if (stockDetails.size() > i + 3) {
                t0.setT3(stockDetails.get(i + 3));
            }
            if (stockDetails.size() > i + 4) {
                t0.setT4(stockDetails.get(i + 4));
            }
            if (stockDetails.size() > i + 5) {
                t0.setT5(stockDetails.get(i + 5));
            }
        }
        stockDetails.removeIf(item -> Objects.isNull(item.getSixtyDayDealQuantity()));
        return stockDetails;
    }

    public void saveCalcRes(List<StockDetail> allAfterList, String strategyDesc, LocalDateTime dataTime) {
        if (CollectionUtils.isEmpty(allAfterList)) {
            return;
        }
        long correctCount = allAfterList.stream().filter(detail -> detail.getNext().getIsUp()).count();
        BigDecimal percRate = allAfterList.stream().map(item -> item.getNext().getPricePert())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(allAfterList.size()), 4, RoundingMode.HALF_UP);
        BigDecimal winRate = new BigDecimal(correctCount).divide(new BigDecimal(allAfterList.size()), 4, RoundingMode.HALF_UP);
        StockCalcRes calcRes = new StockCalcRes();
        calcRes.setStrategyDesc(strategyDesc);
        calcRes.setWinRate(winRate);
        calcRes.setPercRate(percRate);
        calcRes.setCreateDate(dataTime);
        calcRes.setAllCnt(allAfterList.size());
        calcRes.setType(StockCalcRes.TypeEnum.DETAIL.getCode());
        stockCalcResDao.insert(calcRes);
    }
}
