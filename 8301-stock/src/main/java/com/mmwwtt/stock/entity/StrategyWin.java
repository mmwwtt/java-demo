package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
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
@Slf4j
public class StrategyWin {

    private Long strategyWinId;
    /**
     * 策略编码  组合编码
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
     * 5天最大涨幅的中位数
     */
    private BigDecimal fiveMaxMiddlePercRate;

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
    private List<StockDetail> list = Collections.synchronizedList(new ArrayList<>());
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
    private Set<String> strategyCodeSet = new HashSet<>();

    @TableField(exist = false)
    private StrategyWin parentWin;

    /**
     * 将结果累加到数据中
     */
    public void addToResult(StockDetail stockDetail) {
        if (Objects.isNull(stockDetail)) {
            log.info("不存在的详情");
            return;
        }
        list.add(stockDetail);
    }

    /**
     * 填充数据
     */
    public void fillData1() {
        strategyCodeSet.add(strategyCode);
        if(Objects.nonNull(parentWin)) {
            strategyCodeSet.addAll(parentWin.getStrategyCodeSet());
        }

        this.level = strategyCodeSet.size();
        this.strategyCode = String.join(" ", strategyCodeSet);
        this.strategyName = strategyCodeSet.stream()
                .map(item -> StrategyEnum.codeToEnumMap.get(item).getName())
                .collect(Collectors.joining(" "));


        Map<String, List<BigDecimal>> dateToFiveMaxPertMap = new HashMap<>();
        list.stream().filter(item -> Objects.nonNull(item.getNext5MaxPricePert()))
                .forEach(stockDetail ->
                        dateToFiveMaxPertMap.computeIfAbsent(stockDetail.getDealDate(), k -> new ArrayList<>())
                                .add(stockDetail.getNext5MaxPricePert()));

        dateToFiveMaxPertMap.forEach((date, fiveMaxPerts) -> {
            if (CollectionUtils.isNotEmpty(fiveMaxPerts)) {
                fiveMaxPercRateList.add(divide(sum(fiveMaxPerts), fiveMaxPerts.size()));
            }
        });
        fiveMaxPercRate = average(fiveMaxPercRateList);
        cnt = fiveMaxPercRateList.size();
    }

    /**
     * 填充数据
     */
    public void fillData2() {
        Map<String, List<StockDetail>> dateToDetailListMap = list.stream().collect(Collectors.groupingBy(StockDetail::getDealDate));

        dateToDetailListMap.forEach((date, details) -> {
            List<BigDecimal> curOnePertList = new ArrayList<>();
            List<BigDecimal> curTwoPertList = new ArrayList<>();
            List<BigDecimal> curThreePertList = new ArrayList<>();
            List<BigDecimal> curFourPertList = new ArrayList<>();
            List<BigDecimal> curFivePertList = new ArrayList<>();
            List<BigDecimal> curFiveMinPertList = new ArrayList<>();
            List<BigDecimal> curTenPertList = new ArrayList<>();
            List<BigDecimal> curTenMaxPertList = new ArrayList<>();
            List<BigDecimal> curTenMinPertList = new ArrayList<>();
            for (StockDetail detail : details) {
                BigDecimal endPrice = detail.getEndPrice();
                if (Objects.nonNull(detail.getNext1())) {
                    curOnePertList.add(divide(subtract(detail.getNext1().getEndPrice(), endPrice), endPrice));
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curTwoPertList.add(divide(subtract(detail.getNext2().getEndPrice(), endPrice), endPrice));
                }
                if (Objects.nonNull(detail.getNext3())) {
                    curThreePertList.add(divide(subtract(detail.getNext3().getEndPrice(), endPrice), endPrice));
                }
                if (Objects.nonNull(detail.getNext4())) {
                    curFourPertList.add(divide(subtract(detail.getNext4().getEndPrice(), endPrice), endPrice));
                }
                if (Objects.nonNull(detail.getNext5())) {
                    curFivePertList.add(divide(subtract(detail.getNext5().getEndPrice(), endPrice), endPrice));
                    if (Objects.nonNull(detail.getNext5MinPricePert())) {
                        curFiveMinPertList.add(detail.getNext5MinPricePert());
                    }
                }
                if (Objects.nonNull(detail.getNext10())) {
                    curTenPertList.add(divide(subtract(detail.getNext10().getEndPrice(), endPrice), endPrice));
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
            }
            if (CollectionUtils.isNotEmpty(curFiveMinPertList)) {
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
        fiveMaxMiddlePercRate = getMiddle(fiveMaxPercRateList);
        tenPercRate = average(tenPercRateList);
        tenMaxPercRate = average(tenMaxPercRateList);
        tenMinPercRate = average(tenMinPercRateList);
    }


    public static StrategyWin createByStrategyName(StrategyEnum strategyEnum) {
        StrategyWin strategyWin = new StrategyWin();
        strategyWin.setStrategyName(strategyEnum.getName());
        strategyWin.setStrategyCode(strategyEnum.getCode());
        return strategyWin;

    }

    public StrategyWin(String strategyCode) {
        this.strategyCode = strategyCode;
    }

    public StrategyWin(String strategyCode, StrategyWin parentWin) {
        this.strategyCode = strategyCode;
        this.parentWin = parentWin;
    }


    /**
     * 求平均值；当 50 < size < 150 时去掉首尾各5个再平均
     */
    private BigDecimal average(List<BigDecimal> list) {
        return list.size() < 150 && list.size() > 50
                ? divide(sum(list.stream().sorted(Comparator.comparing(BigDecimal::doubleValue))
                        .limit(list.size() - 5).skip(5).toList()),
                list.size() - 10)
                : divide(sum(list), list.size());
    }

    private BigDecimal getMiddle(List<BigDecimal> list) {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> sortList = list.stream()
                .sorted(Comparator.comparing(BigDecimal::doubleValue))
                .toList();
        return sortList.get(sortList.size() / 2);
    }
}