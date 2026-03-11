package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Data
@TableName("stock_strategy_win_t")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyWin {
    /**
     * 结果id
     */
    @EqualsAndHashCode.Include
    @TableId(type = IdType.AUTO)
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
     * 符合数据的股票详情总数
     */
    private Integer cnt = 0;

    /**
     * 预测后的1天平均涨幅
     */
    private BigDecimal onePercRate;

    /**
     * 预测后的2天平均涨幅
     */
    private BigDecimal twoPercRate;


    /**
     * 预测后的3天平均涨幅
     */
    private BigDecimal threePercRate;


    /**
     * 预测后的4天平均涨幅
     */
    private BigDecimal fourPercRate;

    /**
     * 预测后的5天平均涨幅
     */
    private BigDecimal fivePercRate;

    /**
     * 预测后的10天平均涨幅
     */
    private BigDecimal tenPercRate;

    /**
     * 预测的5天内最高价 的平均涨幅
     */
    private BigDecimal fiveMaxPercRate;

    /**
     * 预测的10天内最高价 的平均涨幅
     */
    private BigDecimal tenMaxPercRate;

    /**
     * 策略层级
     */
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
    private List<BigDecimal> tenPercRateList = new ArrayList<>();
    @TableField(exist = false)
    private List<BigDecimal> tenMaxPercRateList = new ArrayList<>();

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
            List<BigDecimal> curTenPertList = new ArrayList<>();
            List<BigDecimal> curTenMaxPertList = new ArrayList<>();
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
                    curFivePertList.add(divide(subtract(detail.getNext5().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curFiveMaxPertList.add(detail.getNext5MaxPricePert());
                }


                if (Objects.nonNull(detail.getNext10())) {
                    curTenPertList.add(divide(subtract(detail.getNext10().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curTenMaxPertList.add(detail.getNext10MaxPricePert());
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
            }


            if (CollectionUtils.isNotEmpty(curTenPertList)) {
                tenPercRateList.add(divide(sum(curTenPertList), curTenPertList.size()));
                tenMaxPercRateList.add(divide(sum(curTenMaxPertList), curTenMaxPertList.size()));
            }

        });

        onePercRate = average(onePercRateList);
        twoPercRate = average(twoPercRateList);
        threePercRate = average(threePercRateList);
        fourPercRate = average(fourPercRateList);
        fivePercRate = average(fivePercRateList);
        fiveMaxPercRate = average(fiveMaxPercRateList);
        tenPercRate = average(tenPercRateList);
        tenMaxPercRate = average(tenMaxPercRateList);
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
     *
     * @return
     */
    private BigDecimal average(List<BigDecimal> list) {
        return list.size() < 100 && list.size() > 20
                ? divide(sum(list.subList(2, list.size() - 2)), list.size() - 4)
                : divide(sum(list), list.size());
    }
}
