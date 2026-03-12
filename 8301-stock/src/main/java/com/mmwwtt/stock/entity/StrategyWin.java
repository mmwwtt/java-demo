package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

/**
 * 策略胜率
 *
 * @author moweitao
 */
@Data
@TableName(value = "stock_strategy_win_t")
@NoArgsConstructor
public class StrategyWin {
    /**
     * 策略编码
     */
    private String strategyCode;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 样本量
     */
    private Integer cnt;

    /**
     * 1天平均涨幅
     */
    private BigDecimal onePercRate;

    /**
     * 2天平均涨幅
     */
    private BigDecimal twoPercRate;

    /**
     * 3天平均涨幅
     */
    private BigDecimal threePercRate;

    /**
     * 4天平均涨幅
     */
    private BigDecimal fourPercRate;

    /**
     * 5天平均涨幅
     */
    private BigDecimal fivePercRate;

    /**
     * 5天平均最高涨幅
     */
    private BigDecimal fiveMaxPercRate;

    /**
     * 5天平均最低涨幅
     */
    private BigDecimal fiveMinPercRate;

    /**
     * 10天平均涨幅
     */
    private BigDecimal tenPercRate;

    /**
     * 10天平均最高涨幅
     */
    private BigDecimal tenMaxPercRate;

    /**
     * 10天平均最低涨幅
     */
    private BigDecimal tenMinPercRate;

    private Integer level;

    /**
     * 日期统计
     */
    private String dateCnt;
    
    /**
     * 5天收益胜率（盈利的交易次数占总交易次数的比例）
     */
    private BigDecimal fiveWinRate;
    
    /**
     * 5天收益中位数
     */
    private BigDecimal fiveMedianPercRate;
    
    /**
     * 5天收益最大回撤
     */
    private BigDecimal fiveMaxDrawdown;
    
    /**
     * 10天收益胜率
     */
    private BigDecimal tenWinRate;
    
    /**
     * 10天收益中位数
     */
    private BigDecimal tenMedianPercRate;
    
    /**
     * 10天收益最大回撤
     */
    private BigDecimal tenMaxDrawdown;
    
    /**
     * 夏普比率（假设无风险利率为2%）
     */
    private BigDecimal sharpeRatio;

    /**
     * 临时属性
     */
    @TableField(exist = false)
    private List<BigDecimal> onePercRateList = new ArrayList<>();

    @TableField(exist = false)
    private List<BigDecimal> twoPercRateList = new ArrayList<>();

    @TableField(exist = false)
    private List<BigDecimal> threePercRateList = new ArrayList<>();

    @TableField(exist = false)
    private List<BigDecimal> fourPercRateList = new ArrayList<>();

    @TableField(exist = false)
    private List<BigDecimal> fivePercRateList = new ArrayList<>();
    @TableField(exist = false)
    private List<BigDecimal> fiveMaxPercRateList = new ArrayList<>();
    @TableField(exist = false)
    private List<BigDecimal> fiveMinPercRateList = new ArrayList<>();
    
    @TableField(exist = false)
    private List<BigDecimal> fiveDailyPercRateList = new ArrayList<>();

    @TableField(exist = false)
    private List<BigDecimal> tenPercRateList = new ArrayList<>();
    @TableField(exist = false)
    private List<BigDecimal> tenMaxPercRateList = new ArrayList<>();
    @TableField(exist = false)
    private List<BigDecimal> tenMinPercRateList = new ArrayList<>();
    
    @TableField(exist = false)
    private List<BigDecimal> tenDailyPercRateList = new ArrayList<>();

    @TableField(exist = false)
    private Map<String, List<StockDetail>> dateToDetailListMap = new ConcurrentHashMap<>();

    @TableField(exist = false)
    private LinkedHashSet<String> strategyCodeSet = new LinkedHashSet<>();

    /**
     * 将结果累加到数据中
     */
    public synchronized void addToResult(StockDetail stockDetail) {
        if (Objects.isNull(stockDetail)) {
            return;
        }

        dateToDetailListMap.computeIfAbsent(stockDetail.getDealDate(), 
                k -> Collections.synchronizedList(new ArrayList<>())).add(stockDetail);
    }


    /**
     * 填充数据
     */
    public void fillData() {
        List<BigDecimal> allFivePercRates = new ArrayList<>();
        List<BigDecimal> allTenPercRates = new ArrayList<>();
        
        dateToDetailListMap.forEach((date, details) -> {
            List<BigDecimal> curOnePertList = new ArrayList<>();
            List<BigDecimal> curTwoPertList = new ArrayList<>();
            List<BigDecimal> curThreePertList = new ArrayList<>();
            List<BigDecimal> curFourPertList = new ArrayList<>();
            List<BigDecimal> curFivePertList = new ArrayList<>();
            List<BigDecimal> curFiveMaxPertList = new ArrayList<>();
            List<BigDecimal> curFiveMinPertList = new ArrayList<>();
            List<BigDecimal> curTenPertList = new ArrayList<>();
            List<BigDecimal> curTenMaxPertList = new ArrayList<>();
            List<BigDecimal> curTenMinPertList = new ArrayList<>();
            for (StockDetail detail : details) {
                if (Objects.nonNull(detail.getNext1())) {
                    BigDecimal onPert = divide(subtract(detail.getNext1().getEndPrice(), detail.getEndPrice()), detail.getEndPrice());
                    curOnePertList.add(onPert);
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curTwoPertList.add(divide(subtract(detail.getNext2().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                }

                if (Objects.nonNull(detail.getNext3())) {
                    curThreePertList.add(divide(subtract(detail.getNext3().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                }


                if (Objects.nonNull(detail.getNext4())) {
                    curFourPertList.add(divide(subtract(detail.getNext4().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                }


                if (Objects.nonNull(detail.getNext5())) {
                    BigDecimal fivePert = divide(subtract(detail.getNext5().getEndPrice(), detail.getEndPrice()), detail.getEndPrice());
                    curFivePertList.add(fivePert);
                    allFivePercRates.add(fivePert);
                    curFiveMaxPertList.add(detail.getNext5MaxPricePert());
                    curFiveMinPertList.add(detail.getNext5MinPricePert());
                }


                if (Objects.nonNull(detail.getNext10())) {
                    BigDecimal tenPert = divide(subtract(detail.getNext10().getEndPrice(), detail.getEndPrice()), detail.getEndPrice());
                    curTenPertList.add(tenPert);
                    allTenPercRates.add(tenPert);
                    curTenMaxPertList.add(detail.getNext10MaxPricePert());
                    curTenMinPertList.add(detail.getNext10MinPricePert());
                }
            }

            if (CollectionUtils.isNotEmpty(curOnePertList)) {
                onePercRateList.add(divide(sum(curOnePertList), curOnePertList.size()));
            }
            if (CollectionUtils.isNotEmpty(curTwoPertList)) {
                twoPercRateList.add(divide(sum(curTwoPertList), curTwoPertList.size()));
            }

            if (CollectionUtils.isNotEmpty(curThreePertList)) {
                threePercRateList.add(divide(sum(curThreePertList), curThreePertList.size()));
            }


            if (CollectionUtils.isNotEmpty(curFourPertList)) {
                fourPercRateList.add(divide(sum(curFourPertList), curFourPertList.size()));
            }


            if (CollectionUtils.isNotEmpty(curFivePertList)) {
                fivePercRateList.add(divide(sum(curFivePertList), curFivePertList.size()));
                fiveMaxPercRateList.add(divide(sum(curFiveMaxPertList), curFiveMaxPertList.size()));
                fiveMinPercRateList.add(divide(sum(curFiveMinPertList), curFiveMinPertList.size()));
            }


            if (CollectionUtils.isNotEmpty(curTenPertList)) {
                tenPercRateList.add(divide(sum(curTenPertList), curTenPertList.size()));
                tenMaxPercRateList.add(divide(sum(curTenMaxPertList), curTenMaxPertList.size()));
                tenMinPercRateList.add(divide(sum(curTenMinPertList), curTenMinPertList.size()));
            }

        });

        onePercRate = average(onePercRateList);
        twoPercRate = average(twoPercRateList);
        threePercRate = average(threePercRateList);
        fourPercRate = average(fourPercRateList);
        fivePercRate = average(fivePercRateList);
        fiveMaxPercRate = average(fiveMaxPercRateList);
        fiveMinPercRate = average(fiveMinPercRateList);
        tenPercRate = average(tenPercRateList);
        tenMaxPercRate = average(tenMaxPercRateList);
        tenMinPercRate = average(tenMinPercRateList);
        cnt = onePercRateList.size();
        
        // 计算胜率、中位数、最大回撤等指标
        calculateAdditionalMetrics(allFivePercRates, allTenPercRates);

        if (cnt < 100 && !dateToDetailListMap.isEmpty()) {
            dateCnt = dateToDetailListMap.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getValue().size(), Comparator.reverseOrder()))
                    .map(e -> String.format("%s_%d", e.getKey(), e.getValue().size()))
                    .collect(Collectors.joining(" \n"));
        }
    }
    
    /**
     * 计算额外的统计指标
     */
    private void calculateAdditionalMetrics(List<BigDecimal> fivePercRates, List<BigDecimal> tenPercRates) {
        // 计算5天收益指标
        if (CollectionUtils.isNotEmpty(fivePercRates)) {
            // 胜率
            long profitableTrades = fivePercRates.stream().filter(rate -> rate.compareTo(BigDecimal.ZERO) > 0).count();
            fiveWinRate = divide(new BigDecimal(profitableTrades), new BigDecimal(fivePercRates.size()));
            
            // 中位数
            List<BigDecimal> sortedFiveRates = new ArrayList<>(fivePercRates);
            Collections.sort(sortedFiveRates);
            int midIndex = sortedFiveRates.size() / 2;
            if (sortedFiveRates.size() % 2 == 0) {
                fiveMedianPercRate = divide(add(sortedFiveRates.get(midIndex - 1), sortedFiveRates.get(midIndex)), new BigDecimal(2));
            } else {
                fiveMedianPercRate = sortedFiveRates.get(midIndex);
            }
            
            // 最大回撤
            fiveMaxDrawdown = calculateMaxDrawdown(sortedFiveRates);
        }
        
        // 计算10天收益指标
        if (CollectionUtils.isNotEmpty(tenPercRates)) {
            // 胜率
            long profitableTrades = tenPercRates.stream().filter(rate -> rate.compareTo(BigDecimal.ZERO) > 0).count();
            tenWinRate = divide(new BigDecimal(profitableTrades), new BigDecimal(tenPercRates.size()));
            
            // 中位数
            List<BigDecimal> sortedTenRates = new ArrayList<>(tenPercRates);
            Collections.sort(sortedTenRates);
            int midIndex = sortedTenRates.size() / 2;
            if (sortedTenRates.size() % 2 == 0) {
                tenMedianPercRate = divide(add(sortedTenRates.get(midIndex - 1), sortedTenRates.get(midIndex)), new BigDecimal(2));
            } else {
                tenMedianPercRate = sortedTenRates.get(midIndex);
            }
            
            // 最大回撤
            tenMaxDrawdown = calculateMaxDrawdown(sortedTenRates);
        }
        
        // 计算夏普比率（简化版，使用5天收益率）
        if (CollectionUtils.isNotEmpty(fivePercRates)) {
            BigDecimal avgReturn = fivePercRate;
            BigDecimal stdDev = calculateStandardDeviation(fivePercRates);
            BigDecimal riskFreeRate = divide(new BigDecimal("0.02"), new BigDecimal("252")); // 假设年化无风险利率为2%，转换为日利率
            
            if (stdDev.compareTo(BigDecimal.ZERO) > 0) {
                sharpeRatio = divide(subtract(avgReturn, riskFreeRate), stdDev);
            }
        }
    }
    
    /**
     * 计算最大回撤
     */
    private BigDecimal calculateMaxDrawdown(List<BigDecimal> returns) {
        if (returns.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal maxDrawdown = BigDecimal.ZERO;
        BigDecimal peak = returns.get(0);
        
        for (BigDecimal ret : returns) {
            // 更新峰值
            if (ret.compareTo(peak) > 0) {
                peak = ret;
            }
            
            // 计算当前回撤
            BigDecimal drawdown = divide(subtract(peak, ret), peak);
            
            // 更新最大回撤
            if (drawdown.compareTo(maxDrawdown) > 0) {
                maxDrawdown = drawdown;
            }
        }
        
        return maxDrawdown;
    }
    
    /**
     * 计算标准差
     */
    private BigDecimal calculateStandardDeviation(List<BigDecimal> returns) {
        if (returns.size() <= 1) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal mean = divide(sum(returns), new BigDecimal(returns.size()));
        BigDecimal varianceSum = BigDecimal.ZERO;
        
        for (BigDecimal ret : returns) {
            BigDecimal diff = subtract(ret, mean);
            varianceSum = add(varianceSum, multiply(diff, diff));
        }
        
        BigDecimal variance = divide(varianceSum, new BigDecimal(returns.size() - 1));
        
        // 简化版开平方，使用BigDecimal的sqrt方法（需要Java 9+）
        return variance.sqrt(MathContext.DECIMAL128);
    }

    public static StrategyWin createByStrategyName(StrategyEnum strategyEnum) {
        StrategyWin strategyWin = new StrategyWin();
        strategyWin.setStrategyName(strategyEnum.getName());
        strategyWin.setStrategyCode(strategyEnum.getCode());
        return strategyWin;

    }

    public StrategyWin(String strategyCode) {
        this(new LinkedHashSet<>(Set.of(strategyCode)));
    }

    public StrategyWin(LinkedHashSet<String> strategyCodeSet) {
        List<String> list = new ArrayList<>(strategyCodeSet);
        String name = list.stream()
                .map(item -> StrategyEnum.codeToEnumMap.get(item).getName())
                .collect(Collectors.joining(" "));

        this.strategyCode = String.join(" ", list);
        this.strategyName = name;
        this.level = strategyCodeSet.size();
    }

    /**
     * 求平均值  去掉首尾各2个
     *
     * @return
     */
    private BigDecimal average(List<BigDecimal> list) {
        return list.size() < 150 && list.size() > 50
                ? divide(sum(list.stream().sorted(Comparator.comparing(BigDecimal::doubleValue))
                        .limit(list.size() - 5).skip(5).toList()),
                list.size() - 10)
                : divide(sum(list), list.size());
    }
}