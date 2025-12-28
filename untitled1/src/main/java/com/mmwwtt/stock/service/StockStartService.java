package com.mmwwtt.stock.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.isEquals;
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


    /**
     * 计算均线（收盘价均线/成交量均线）
     *
     * @param dataList K线数据列表（按日期升序）
     * @param window   窗口天数
     * @param isVolume 是否计算成交量均线（false则计算收盘价均线）
     * @return 均线列表（前window-1个为null，与原数据长度一致）
     */
    private static List<Double> calculateMA(List<StockDetail> dataList, int window, boolean isVolume) {
        List<Double> maList = new ArrayList<>();
        // 前window-1个数据不足窗口，设为null
        for (int i = 0; i < window - 1; i++) {
            maList.add(null);
        }
        // 滑动窗口计算均线
        for (int i = window - 1; i < dataList.size(); i++) {
            double sum = 0d;
            for (int j = i - window + 1; j <= i; j++) {
                sum += isVolume ? dataList.get(j).getAllDealQuantity() : dataList.get(j).getEndPrice();
            }
            Double ma = sum / window;
            maList.add(ma);
        }
        return maList;
    }

    /**
     * 特征1：判断量价配合（上涨日放量，下跌日缩量）
     *
     * @param dataList   K线数据列表
     * @param volMa5List 5日均量列表
     * @return true=满足
     */
    private static boolean isPriceVolumeMatch(List<StockDetail> dataList, List<Double> volMa5List) {
        // 取最近10个交易日判断
        int startIndex = Math.max(0, dataList.size() - 10);
        int matchCount = 0;
        for (int i = startIndex; i < dataList.size(); i++) {
            StockDetail data = dataList.get(i);
            Double volMa5 = volMa5List.get(i);
            if (volMa5 == null) {
                continue;
            }
            Double currVol = data.getAllDealQuantity();
            // 上涨日：成交量>5日均量
            if (data.isYangLine() && currVol.compareTo(volMa5) > 0) {
                matchCount++;
            }
            // 下跌日：成交量<5日均量
            if (data.isYinLine() && currVol.compareTo(volMa5) < 0) {
                matchCount++;
            }
        }
        // 满足度≥70%则视为符合
        return matchCount >= (dataList.size() - startIndex) * 0.7;
    }

    /**
     * 特征2：判断均线多头排列+收盘价站均线
     *
     * @param dataList K线数据列表
     * @param ma5List  5日均线列表
     * @param ma10List 10日均线列表
     * @param ma60List 60日均线列表
     * @return true=满足
     */
    private static boolean isMaBullish(List<StockDetail> dataList, List<Double> ma5List, List<Double> ma10List, List<Double> ma60List) {
        if (dataList.isEmpty()) {
            return false;
        }
        // 取最新一个交易日的数据
        int lastIndex = dataList.size() - 1;
        StockDetail lastData = dataList.get(lastIndex);
        Double ma5 = ma5List.get(lastIndex);
        Double ma10 = ma10List.get(lastIndex);
        Double ma60 = ma60List.get(lastIndex);
        if (ma5 == null || ma10 == null || ma60 == null) {
            return false;
        }
        // 1. 均线多头排列：5日线>10日线>60日线
        boolean maBullish = ma5.compareTo(ma10) > 0 && ma10.compareTo(ma60) > 0;
        // 2. 收盘价站上所有均线
        boolean closeOnMa = lastData.getEndPrice() > ma5
                && lastData.getEndPrice() > ma10
                && lastData.getEndPrice() > ma60;
        return maBullish && closeOnMa;
    }

    /**
     * 特征3：判断成交量逐步放大
     *
     * @param dataList    K线数据列表
     * @param volMa20List 20日均量列表
     * @return true=满足
     */
    private static boolean isVolumeStepUp(List<StockDetail> dataList, List<Double> volMa20List) {
        if (dataList.size() < VOL_STEP_DAYS) {
            return false;
        }
        // 取最近VOL_STEP_DAYS个交易日
        int startIndex = dataList.size() - VOL_STEP_DAYS;
        List<StockDetail> recentData = dataList.subList(startIndex, dataList.size());
        // 1. 成交量逐步增长（单日增长≥5%）
        boolean stepUp = true;
        for (int i = 1; i < recentData.size(); i++) {
            Double prevVol = recentData.get(i - 1).getAllDealQuantity();
            Double currVol = recentData.get(i).getAllDealQuantity();
            if (isEquals(prevVol, 0)) {
                stepUp = false;
                break;
            }
            Double growthRatio = currVol - prevVol / prevVol;
            if (currVol <= prevVol || growthRatio.compareTo(VOL_GROWTH_RATIO) < 0) {
                stepUp = false;
                break;
            }
        }
        if (!stepUp) {
            return false;
        }
        // 2. 最新成交量>20日均量
        int lastIndex = dataList.size() - 1;
        Double lastVolMa20 = volMa20List.get(lastIndex);
        Double lastVol = dataList.get(lastIndex).getAllDealQuantity();
        return lastVol.compareTo(lastVolMa20) > 0;
    }

    /**
     * 特征4：判断上升趋势（近20日涨幅≥5%，60日均线向上）
     *
     * @param dataList K线数据列表
     * @param ma60List 60日均线列表
     * @return true=满足
     */
    private static boolean isUpTrend(List<StockDetail> dataList, List<Double> ma60List) {
        if (dataList.size() < MA20_DAYS || ma60List.size() < 2) {
            return false;
        }
        // 1. 近20日涨幅≥5%
        int startIndex = dataList.size() - MA20_DAYS;
        Double startClose = dataList.get(startIndex).getEndPrice();
        Double lastClose = dataList.get(dataList.size() - 1).getEndPrice();
        Double growthRatio = (lastClose - startClose) / startClose;
        if (growthRatio.compareTo(TREND_UP_RATIO) < 0) {
            return false;
        }
        // 2. 60日均线向上（最新ma60 > 前一日ma60）
        int lastIndex = ma60List.size() - 1;
        Double lastMa60 = ma60List.get(lastIndex);
        Double prevMa60 = ma60List.get(lastIndex - 1);
        return lastMa60.compareTo(prevMa60) > 0;
    }

    /**
     * 特征5：判断看涨吞没形态（最新2根K线）
     *
     * @param dataList K线数据列表
     * @return true=满足
     */
    private static boolean isBullishEngulfing(List<StockDetail> dataList) {
        if (dataList.size() < 2) {
            return false;
        }
        StockDetail prevData = dataList.get(dataList.size() - 2);
        StockDetail currData = dataList.get(dataList.size() - 1);
        // 1. 前一根是阴线，后一根是阳线
        if (!prevData.isYinLine() || !currData.isYangLine()) {
            return false;
        }
        // 2. 阳线实体完全吞没阴线实体
        return currData.getStartPrice() < prevData.getEndPrice()
                && currData.getEndPrice() > prevData.getStartPrice();
    }

    /**
     * 特征6：判断早晨之星形态（最新3根K线）
     *
     * @param dataList K线数据列表
     * @return true=满足
     */
    private static boolean isMorningStar(List<StockDetail> dataList) {
        if (dataList.size() < 3) {
            return false;
        }
        StockDetail first = dataList.get(dataList.size() - 3);
        StockDetail middle = dataList.get(dataList.size() - 2);
        StockDetail last = dataList.get(dataList.size() - 1);
        // 1. 第一根是阴线，第三根是阳线
        if (!first.isYinLine() || !last.isYangLine()) {
            return false;
        }
        // 2. 中间是十字星
        if (!middle.isDoji()) {
            return false;
        }
        // 3. 阳线实体深入阴线实体≥50%
        Double firstEntity = first.getEntityLength();
        Double penetration = Math.abs(first.getEndPrice() - last.getStartPrice());
        return penetration / firstEntity > MORNING_STAR_PENETRATION;
    }

    /**
     * 特征7：判断金针探底形态（最新1根K线）
     *
     * @param dataList K线数据列表
     * @return true=满足
     */
    private static boolean isGoldenPinBottom(List<StockDetail> dataList) {
        if (dataList.isEmpty()) {
            return false;
        }
        StockDetail lastData = dataList.get(dataList.size() - 1);
        Double entityLength = lastData.getEntityLength();
        if (isEquals(entityLength, 0)) {
            return false;
        }
        // 下影线长度≥实体长度的2倍
        Double lowerShadow = lastData.getLowerShadowLength();
        boolean shadowCondition = lowerShadow / entityLength > GOLDEN_PIN_RATIO;
        // 收盘价接近最高价（距离≤总振幅的10%）
        Double totalRange = lastData.getHighPrice() - lastData.getLowPrice();
        Double closeToHigh = lastData.getHighPrice() - lastData.getEndPrice();
        boolean closeCondition = closeToHigh / totalRange < 0.1;
        // 成交量放大（大于5日均量）
        List<Double> volMa5List = calculateMA(dataList, MA5_DAYS, true);
        Double volMa5 = volMa5List.get(volMa5List.size() - 1);
        boolean volumeCondition = lastData.getAllDealQuantity() > volMa5;

        return shadowCondition && closeCondition && volumeCondition;
    }

    /**
     * 核心方法：计算股票上涨概率得分（0-10分，得分越高概率越高）
     *
     * @param dataList K线数据列表（按日期升序，需至少60条数据）
     * @return 得分+上涨概率描述
     */
    public static ScoreResult calculateUpProbability(List<StockDetail> dataList) {
        // 数据校验
        if (dataList == null || dataList.size() < MA60_DAYS) {
            return new ScoreResult(0, "K线数据不足60天，无法判断");
        }

        // 预计算各类均线
        List<Double> closeMa5List = calculateMA(dataList, MA5_DAYS, false);
        List<Double> closeMa10List = calculateMA(dataList, MA10_DAYS, false);
        List<Double> closeMa60List = calculateMA(dataList, MA60_DAYS, false);
        List<Double> volMa5List = calculateMA(dataList, MA5_DAYS, true);
        List<Double> volMa20List = calculateMA(dataList, MA20_DAYS, true);

        // 初始化得分
        int score = 0;
        StringBuilder reason = new StringBuilder();

        // 逐一判断特征，满足则加分（权重根据重要性调整）
        if (isPriceVolumeMatch(dataList, volMa5List)) {
            score += 2;
            reason.append("量价配合；");
        }
        if (isMaBullish(dataList, closeMa5List, closeMa10List, closeMa60List)) {
            score += 2;
            reason.append("均线多头排列且收盘价站均线；");
        }
        if (isVolumeStepUp(dataList, volMa20List)) {
            score += 1;
            reason.append("成交量逐步放大；");
        }
        if (isUpTrend(dataList, closeMa60List)) {
            score += 1;
            reason.append("处于上升趋势；");
        }
        if (isBullishEngulfing(dataList)) {
            score += 1;
            reason.append("出现看涨吞没形态；");
        }
        if (isMorningStar(dataList)) {
            score += 1;
            reason.append("出现早晨之星形态；");
        }
        if (isGoldenPinBottom(dataList)) {
            score += 1;
            reason.append("出现金针探底形态；");
        }

        // 额外加分：若同时满足趋势+量价+形态，加1分
        boolean hasTrend = isUpTrend(dataList, closeMa60List);
        boolean hasPriceVolume = isPriceVolumeMatch(dataList, volMa5List);
        boolean hasPattern = isBullishEngulfing(dataList) || isMorningStar(dataList) || isGoldenPinBottom(dataList);
        if (hasTrend && hasPriceVolume && hasPattern) {
            score += 1;
            reason.append("趋势+量价+形态共振；");
        }

        // 限制得分范围0-10
        score = Math.min(score, 10);
        score = Math.max(score, 0);

        // 概率描述
        String probabilityDesc = switch (score) {
            case 0 -> "上涨概率极低（0-10%）";
            case 1, 2 -> "上涨概率低（10-30%）";
            case 3, 4, 5 -> "上涨概率中等（30-50%）";
            case 6, 7, 8 -> "上涨概率较高（50-80%）";
            case 9, 10 -> "上涨概率极高（80-99%）";
            default -> "无法判断";
        };

        return new ScoreResult(score, probabilityDesc + " 原因：" + (reason.length() > 0 ? reason.substring(0, reason.length() - 1) : "无"));
    }

    /**
     * 结果封装类：得分+描述
     */
    public static class ScoreResult {
        private int score;
        private String description;

        public ScoreResult(int score, String description) {
            this.score = score;
            this.description = description;
        }

        // Getter
        public int getScore() {
            return score;
        }

        public String getDescription() {
            return description;
        }
    }


    public List<String> getStockRecommendByRealTime() {
        List<String> result = new ArrayList<>();
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
            Map<String, String> map1 = new HashMap<>();
            map1.put(LICENCE, BI_YING_LICENCE);
            map1.put(STOCK_CODE, stock.getCode());

            ResponseEntity<List<StockDetailVO>> stockDetailResponse = restTemplate.exchange(REAL_TIME_URL, HttpMethod.GET,
                    null, new ParameterizedTypeReference<>() {
                    }, map1);


            List<Stock> resList = new ArrayList<>();
            //todo 修改
            if (quentityAndPerc(stockDetails)) {
                resList.add(stock);
                System.out.printf("%s_%s\n", stock.getCode(), stock.getName());
            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    result.add(str);
                    fos.write(str.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<String> getStockRecommendByHistory() {
        List<String> result = new ArrayList<>();
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        for (Stock stock : stockList) {
            //30开头是创业板  68开头是科创版
            if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                continue;
            }
            QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
            detailWapper.eq("stock_code", stock.getCode());
            List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);

            List<Stock> resList = new ArrayList<>();
            if (quentityAndPerc(stockDetails)) {
                resList.add(stock);
                System.out.printf("%s_%s\n", stock.getCode(), stock.getName());
            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    result.add(str);
                    fos.write(str.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(StockDetail stockDetail1, StockDetail stockDetail2) {
        double res2 = (stockDetail2.getPricePert() / stockDetail2.getAllDealQuantity());
        double res1 = (stockDetail1.getPricePert() / stockDetail1.getAllDealQuantity());
        return res2 - res1 > 0 + Math.abs(res1 * 1.5);
    }

    /**
     * 涨跌百分比/成交量   的值    在扩大
     */
    private boolean quentityAndPerc(List<StockDetail> stockDetailList) {
        if (Objects.isNull(stockDetailList) || stockDetailList.size() < 5) {
            return false;
        }
        StockDetail stockDetail5 = stockDetailList.get(stockDetailList.size() - 5);
        StockDetail stockDetail4 = stockDetailList.get(stockDetailList.size() - 4);
        StockDetail stockDetail3 = stockDetailList.get(stockDetailList.size() - 3);
        StockDetail stockDetail2 = stockDetailList.get(stockDetailList.size() - 2);
        StockDetail stockDetail1 = stockDetailList.get(stockDetailList.size() - 1);
        if (stockDetail1.getPricePert() > 0.05 || stockDetail1.getPricePert() < 0
                || stockDetail1.getEndPrice() < stockDetail1.getStartPrice()) {
            return false;
        }
        int cnt = 0;
        if (quentityAndPerc(stockDetail5, stockDetail4)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail4, stockDetail3)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail3, stockDetail2)) {
            cnt++;
        }
        if (quentityAndPerc(stockDetail2, stockDetail1)) {
            cnt++;
        }

        return cnt >= 3;
    }


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
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray( new CompletableFuture[0]));
        allTask.get();

        log.info("开始计算");
        for (StockService stockService : stockServiceList) {
            pool.submit(() -> {
                List<OneRes> allRes = new ArrayList<>();
                codeToDetailMap.forEach((k, v) -> {
                    List<OneRes> calcResList = stockService.run(v);
                    allRes.addAll(calcResList);
                });
                long correctCount = allRes.stream().filter(OneRes::getIsCorrect).count();
                double percRate = allRes.stream().filter(OneRes::getIsCorrect).mapToDouble(OneRes::getPricePert).sum();

                if (CollectionUtils.isEmpty(allRes)) {
                    return;
                }
                StockCalcRes calcRes = new StockCalcRes();
                calcRes.setStrategy(stockService.getStrategy());
                calcRes.setWinRate((double) correctCount / allRes.size());
                calcRes.setPercRate(percRate);
                calcRes.setCreateDate(LocalDateTime.now());
                stockCalcResDao.insert(calcRes);
            });
        }
        log.info("结束计算");
    }
}
