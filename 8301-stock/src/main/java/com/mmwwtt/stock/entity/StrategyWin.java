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

    private Long strategyWinId;
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
                    curFiveMaxPertList.add(detail.getNext5MaxPricePert());
                    curFiveMinPertList.add(detail.getNext5MinPricePert());
                }


                if (Objects.nonNull(detail.getNext10())) {
                    BigDecimal tenPert = divide(subtract(detail.getNext10().getEndPrice(), detail.getEndPrice()), detail.getEndPrice());
                    curTenPertList.add(tenPert);
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

        if (cnt < 100 && !dateToDetailListMap.isEmpty()) {
            dateCnt = dateToDetailListMap.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getValue().size(), Comparator.reverseOrder()))
                    .map(e -> String.format("%s_%d", e.getKey(), e.getValue().size()))
                    .collect(Collectors.joining(" \n"));
        }
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
     */
    private BigDecimal average(List<BigDecimal> list) {
        return list.size() < 150 && list.size() > 50
                ? divide(sum(list.stream().sorted(Comparator.comparing(BigDecimal::doubleValue))
                        .limit(list.size() - 5).skip(5).toList()),
                list.size() - 10)
                : divide(sum(list), list.size());
    }
}