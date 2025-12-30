package com.mmwwtt.stock.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.Constants.*;

@Service
@Slf4j
public class StockStartService {

    @Resource
    private StockDao stockDao;

    @Resource
    private StockDetailDao stockDetailDao;

    @Resource
    private List<StockService> stockServiceList;

    @Resource
    private StockCalcResDao stockCalcResDao;

    private final ThreadPoolExecutor pool = GlobalThreadPool.getInstance();

    private RestTemplate restTemplate = new RestTemplate();

    // 可配置参数（根据策略调整）
    // 均线周期
    private static final int MA5_DAYS = 5;
    private static final int MA10_DAYS = 10;
    private static final int MA60_DAYS = 60;
    private static final int MA20_DAYS = 20;
    // 成交量增长参数
    private static final int VOL_STEP_DAYS = 5;
    private static final Double VOL_GROWTH_RATIO = 0.05; // 单日成交量最低增长5%
    // 趋势参数：近20日涨幅≥5%视为上升趋势
    private static final Double TREND_UP_RATIO = 0.05;
    // 早晨之星：阳线深入阴线实体的比例≥50%
    private static final Double MORNING_STAR_PENETRATION = 0.5;
    // 金针探底：下影线长度≥实体长度的2倍
    private static final Double GOLDEN_PIN_RATIO = 2.0;


    public void dataDownLoad() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        ResponseEntity<List<StockVO>> stocksListResponse = restTemplate.exchange(STOCK_LIST_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                }, map);
        List<StockVO> stockVOList = stocksListResponse.getBody();
        List<Stock> stockList = StockConverter.INSTANCE.convertToStock(stockVOList);
        stockDao.insert(stockList);
    }

    /**
     * 更新每日股票涨跌信息
     *
     * @throws InterruptedException
     */
    //@PostConstruct
    @Scheduled(cron = "0 30 3 * * ?")
    public void dataDetailDownLoad() throws InterruptedException {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        int cnt = 0;
        for (Stock stock : stockList) {
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            cnt++;
            if (cnt % 200 == 0) {
                Thread.sleep(10000);
            }
            Thread.sleep(100);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());
            map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
            map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
            map1.put(START_DATA, "20251101");
            map1.put(END_DATA, "20251113");
            map1.put(MAX_SIZE, "30");
            try {
                ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(HISTORY_DATA_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<>() {
                        }, map1);
                List<StockDetailVO> stockDetailVOs = stockDetailResponse.getBody().stream()
                        .peek(item -> item.setStockCode(stock.getCode()))
                        .collect(Collectors.toList());
                List<StockDetail> stockDetails = StockConverter.INSTANCE.convertToStockDetail(stockDetailVOs);
                for (StockDetail stockDetail : stockDetails) {
                    QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                    detailWapper.eq("stock_code", stockDetail.getStockCode());
                    detailWapper.eq("deal_date", stockDetail.getDealDate());
                    List<StockDetail> stockDetails1 = stockDetailDao.selectList(detailWapper);
                    if (CollectionUtils.isNotEmpty(stockDetails1)) {
                        stockDetail.setStockDetailId(stockDetails1.get(0).getStockDetailId());
                    }
                    stockDetailDao.insertOrUpdate(stockDetail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 开始计算
     */
    public void startCalc() throws ExecutionException, InterruptedException {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        Map<String, List<StockDetail>> codeToDetailMap = new HashMap<>();
        log.info("开始查询股票数据");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Stock stock : stockList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //30开头是创业板  68开头是科创版
                if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                    return;
                }
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode());
                detailWapper.orderByDesc("deal_date");
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                codeToDetailMap.put(stock.getCode(), stockDetails);
            }, pool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        log.info("开始计算");
        LocalDateTime dataTime = LocalDateTime.now();
        for (StockService stockService : stockServiceList) {
            pool.submit(() -> {
                List<OneRes> allRes = new ArrayList<>();
                codeToDetailMap.forEach((k, v) -> {
                    List<OneRes> calcResList = stockService.run(v);
                    allRes.addAll(calcResList);
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
                StockCalcRes calcRes = new StockCalcRes();
                calcRes.setStrategy(stockService.getStrategy());
                calcRes.setWinRate(winRate);
                calcRes.setPercRate(percRate.divide(new BigDecimal(allRes.size()), 4, RoundingMode.HALF_UP));
                calcRes.setCreateDate(dataTime);
                calcRes.setAllCnt(allRes.size());
                stockCalcResDao.insert(calcRes);
            });
        }
        log.info("结束计算");
    }

    /**
     * 开始计算
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
        for (BigDecimal upShadowLowLimit = BigDecimal.ZERO; upShadowLowLimit.compareTo(BigDecimal.ONE) < 0; upShadowLowLimit = upShadowLowLimit.add(tenPert)) {
            for (BigDecimal upShadowUpLimit = BigDecimal.ZERO; upShadowUpLimit.compareTo(BigDecimal.ONE) < 0; upShadowUpLimit = upShadowUpLimit.add(tenPert)) {
                if (upShadowLowLimit.compareTo(upShadowUpLimit) >= 0) {
                    continue;
                }
                for (BigDecimal lowShadowLowLimit = BigDecimal.ZERO; lowShadowLowLimit.compareTo(BigDecimal.ONE) < 0; lowShadowLowLimit = lowShadowLowLimit.add(tenPert)) {
                    for (BigDecimal lowShadowUpLimit = BigDecimal.ZERO; lowShadowUpLimit.compareTo(BigDecimal.ONE) < 0; lowShadowUpLimit = lowShadowUpLimit.add(tenPert)) {
                        if (lowShadowLowLimit.compareTo(lowShadowUpLimit) >= 0) {
                            continue;
                        }
                        BigDecimal finalUpShadowLowLimit = upShadowLowLimit;
                        BigDecimal finalUpShadowUpLimit = upShadowUpLimit;
                        BigDecimal finalLowShadowLowLimit = lowShadowLowLimit;
                        BigDecimal finalLowShadowUpLimit = lowShadowUpLimit;
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            for (BigDecimal pricePertLowLimit = new BigDecimal("-0.1"); pricePertLowLimit.compareTo(tenPert) < 0; pricePertLowLimit = pricePertLowLimit.add(new BigDecimal("0.01"))) {
                                for (BigDecimal pricePertUpLimit = new BigDecimal("-0.1"); pricePertUpLimit.compareTo(tenPert) < 0; pricePertUpLimit = pricePertUpLimit.add(new BigDecimal("0.01"))) {
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
                                    calcRes.setStrategy(JSON.toJSONString(stockStrategy));
                                    calcRes.setWinRate(winRate);
                                    calcRes.setPercRate(percRate.divide(new BigDecimal(allRes.size()), 4, RoundingMode.HALF_UP));
                                    calcRes.setCreateDate(dataTime);
                                    calcRes.setAllCnt(allRes.size());
                                    log.info(JSON.toJSONString(stockStrategy));
                                    stockCalcResDao.insert(calcRes);
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
        log.info("结束计算");
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
                if (list.get(curIdx + i).getAllDealQuantity().compareTo(list.get(curIdx + i + 1).getAllDealQuantity()) < 0) {
                    return false;
                }
            } else {
                //比前一天大则返回false
                if (list.get(curIdx + i).getAllDealQuantity().compareTo(list.get(curIdx + i + 1).getAllDealQuantity()) > 0) {
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
            if (list.get(curIdx + i).getPertDivisionQuentity().compareTo(list.get(curIdx + i + 1).getPertDivisionQuentity()) < 0) {
                return false;
            }
        }
        return true;
    }
}
