package com.mmwwtt.stock.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

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
    private Integer cnt=0;

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
    private BigDecimal winPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int winCnt = 0;
    @TableField(exist = false)
    private BigDecimal onePriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int oneCnt = 0;
    @TableField(exist = false)
    private BigDecimal twoPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int twoCnt = 0;
    @TableField(exist = false)
    private BigDecimal threePriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int threeCnt = 0;
    @TableField(exist = false)
    private BigDecimal fourPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int fourCnt = 0;
    @TableField(exist = false)
    private BigDecimal fivePriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private BigDecimal fiveMaxPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int fiveCnt = 0;
    @TableField(exist = false)
    private BigDecimal tenPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private BigDecimal tenMaxPriceRateSum = BigDecimal.ZERO;
    @TableField(exist = false)
    private int tenCnt = 0;
    @TableField(exist = false)
    private Map<String, List<StockDetail>> dateToDetailListMap = new ConcurrentHashMap<>();

    /**
     * 将结果累加到数据中
     */
    public synchronized void addToResult(StockDetail stockDetail) {
        if(Objects.isNull(stockDetail)) {
            return;
        }

        dateToDetailListMap.computeIfAbsent(stockDetail.getDealDate(),
                k-> Collections.synchronizedList(new ArrayList<>())).add(stockDetail);
    }


    /**
     * 填充数据
     */
    public void fillData() {
        dateToDetailListMap.forEach((date, details) -> {
            BigDecimal curOnePertSum=BigDecimal.ZERO;
            BigDecimal curTwoPertSum=BigDecimal.ZERO;
            BigDecimal curThreePertSum=BigDecimal.ZERO;
            BigDecimal curFourPertSum=BigDecimal.ZERO;
            BigDecimal curFivePertSum=BigDecimal.ZERO;
            BigDecimal curFiveMaxPertSum=BigDecimal.ZERO;
            int curOneCnt=0;
            int curTwoCnt=0;
            int curThreeCnt=0;
            int curFourCnt=0;
            int curFiveCnt=0;
            int curTenCnt=0;
            BigDecimal curTenPertSum=BigDecimal.ZERO;
            BigDecimal curTenMaxPertSum=BigDecimal.ZERO;
            for (StockDetail detail : details) {
                if (Objects.nonNull(detail.getNext1())) {
                    BigDecimal onPert = divide(subtract(detail.getNext1().getEndPrice(), detail.getEndPrice()), detail.getEndPrice());
                    curOnePertSum = add(curOnePertSum, onPert);
                     curOneCnt++;
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curTwoPertSum = add(curTwoPertSum, divide(subtract(detail.getNext2().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curTwoCnt++;
                }

                if (Objects.nonNull(detail.getNext3())) {
                    curThreePertSum = add(curThreePertSum, divide(subtract(detail.getNext3().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curThreeCnt++;
                }


                if (Objects.nonNull(detail.getNext4())) {
                    curFourPertSum = add(curFourPertSum, divide(subtract(detail.getNext4().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curFourCnt++;
                }


                if (Objects.nonNull(detail.getNext5())) {
                    curFivePertSum = add(curFivePertSum, divide(subtract(detail.getNext5().getEndPrice(),
                            detail.getEndPrice()), detail.getEndPrice()));
                    curFiveMaxPertSum = add(curFiveMaxPertSum, detail.getNext5MaxPricePert());
                    curFiveCnt++;
                }


                if (Objects.nonNull(detail.getNext10())) {
                    curTenPertSum = add(curTenPertSum, divide(subtract(detail.getNext10().getEndPrice(), detail.getEndPrice()), detail.getEndPrice()));
                    curTenMaxPertSum = add(curTenMaxPertSum, detail.getNext10MaxPricePert());
                    curTenCnt++;
                }
            }

            if (curOneCnt>0) {
                onePriceRateSum = add(onePriceRateSum, divide(curOnePertSum, curOneCnt));
                oneCnt++;
            }
            if (curTwoCnt>0) {
                twoPriceRateSum = add(twoPriceRateSum, divide(curTwoPertSum, curTwoCnt));
                twoCnt++;
            }

            if (curThreeCnt>0) {
                threePriceRateSum = add(threePriceRateSum, divide(curThreePertSum, curThreeCnt));
                threeCnt++;
            }


            if (curFourCnt>0) {
                fourPriceRateSum = add(fourPriceRateSum, divide(curFourPertSum, curFourCnt));
                fourCnt++;
            }


            if (curFiveCnt>0) {
                fivePriceRateSum = add(fivePriceRateSum, divide(curFivePertSum, curFiveCnt));
                fiveMaxPriceRateSum = add(fiveMaxPriceRateSum, divide(curFiveMaxPertSum, curFiveCnt));
                fiveCnt++;
            }


            if (curTenCnt > 0) {
                tenPriceRateSum = add(tenPriceRateSum, divide(curTenPertSum, curTenCnt));
                tenMaxPriceRateSum = add(tenMaxPriceRateSum, divide(curTenMaxPertSum, curTenCnt));
                tenCnt++;
            }

        });

        onePercRate = divide(onePriceRateSum, oneCnt);
        twoPercRate = divide(twoPriceRateSum, twoCnt);
        threePercRate = divide(threePriceRateSum, threeCnt);
        fourPercRate = divide(fourPriceRateSum, fourCnt);
        fivePercRate = divide(fivePriceRateSum, fiveCnt);
        tenPercRate = divide(tenPriceRateSum, tenCnt);
        tenMaxPercRate = divide(tenMaxPriceRateSum, tenCnt);
        fiveMaxPercRate = divide(fiveMaxPriceRateSum, fiveCnt);
        cnt = oneCnt;
        // 按日聚合后 oneCnt=交易日数；dateCnt 用 dateToDetailListMap 现算，避免 dateToCntMap 未维护为空
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
        this(Set.of(strategyCode));
    }

    public StrategyWin(Set<String> strategyCodeSet) {
        List<String> list = new ArrayList<>(strategyCodeSet);
        Collections.sort(list);
        String name = list.stream()
                .map(item -> StrategyEnum.codeToEnumMap.get(item).getName())
                .collect(Collectors.joining(" "));

        this.strategyCode = String.join(" ", list);
        this.strategyName = name;
        this.level = strategyCodeSet.size();
    }
}
