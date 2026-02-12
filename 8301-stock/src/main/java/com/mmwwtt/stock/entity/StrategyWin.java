package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
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
     * 胜率
     */
    private BigDecimal winRate;

    /**
     * 预测后的预测对的平均涨幅
     */
    private BigDecimal winPercRate;

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
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 策略层级
     */
    private Integer level;

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

    /**
     * 将结果累加到数据中
     */
    public synchronized void addToResult(StockDetail stockDetail) {
        if(Objects.isNull(stockDetail)) {
            return;
        }
        if (Objects.nonNull(stockDetail.getNext1())) {
            BigDecimal onPert = divide(subtract(stockDetail.getNext1().getEndPrice(), stockDetail.getEndPrice()), stockDetail.getEndPrice());
            onePriceRateSum = add(onePriceRateSum, onPert);
            oneCnt++;
            if (stockDetail.getNext1().getIsUp()) {
                winPriceRateSum = add(winPriceRateSum, onPert);
                winCnt++;
            }
        }
        if (Objects.nonNull(stockDetail.getNext2())) {
            twoPriceRateSum = add(twoPriceRateSum, divide(subtract(stockDetail.getNext2().getEndPrice(), stockDetail.getEndPrice()), stockDetail.getEndPrice()));
            twoCnt++;
        }

        if (Objects.nonNull(stockDetail.getNext3())) {
            threePriceRateSum = add(threePriceRateSum, divide(subtract(stockDetail.getNext3().getEndPrice(), stockDetail.getEndPrice()), stockDetail.getEndPrice()));
            threeCnt++;
        }


        if (Objects.nonNull(stockDetail.getNext4())) {
            fourPriceRateSum = add(fourPriceRateSum, divide(subtract(stockDetail.getNext1().getEndPrice(), stockDetail.getEndPrice()), stockDetail.getEndPrice()));
            fourCnt++;
        }


        if (Objects.nonNull(stockDetail.getNext5())) {
            fivePriceRateSum = add(fivePriceRateSum, divide(subtract(stockDetail.getNext5().getEndPrice(),
                    stockDetail.getEndPrice()), stockDetail.getEndPrice()));
            fiveMaxPriceRateSum = add(fiveMaxPriceRateSum, stockDetail.getNext5MaxPricePert());
            fiveCnt++;
        }


        if (Objects.nonNull(stockDetail.getNext10())) {
            tenPriceRateSum = add(tenPriceRateSum, divide(subtract(stockDetail.getNext10().getEndPrice(), stockDetail.getEndPrice()), stockDetail.getEndPrice()));
            tenMaxPriceRateSum = add(tenMaxPriceRateSum, stockDetail.getNext10MaxPricePert());
            tenCnt++;
        }
    }


    /**
     * 填充数据
     */
    public void fillData() {
        winRate = divide(winCnt, oneCnt);
        winPercRate = divide(winPriceRateSum, winCnt);
        onePercRate = divide(onePriceRateSum, oneCnt);
        twoPercRate = divide(twoPriceRateSum, twoCnt);
        threePercRate = divide(threePriceRateSum, threeCnt);
        fourPercRate = divide(fourPriceRateSum, fourCnt);
        fivePercRate = divide(fivePriceRateSum, fiveCnt);
        tenPercRate = divide(tenPriceRateSum, tenCnt);
        tenMaxPercRate = divide(tenMaxPriceRateSum, tenCnt);
        fiveMaxPercRate = divide(fiveMaxPriceRateSum, fiveCnt);
        cnt = oneCnt;
    }

    public StrategyWin(String strategyCode, LocalDateTime now) {
        String[] codes = strategyCode.split(" ");
        String name = Arrays.stream(codes)
                .map(item -> StrategyEnum.codeToEnumMap.get(item).getName())
                .collect(Collectors.joining(" "));

        this.createDate = now;
        this.strategyCode = strategyCode;
        this.strategyName = name;
        this.level = ((int) Arrays.stream(codes).count());

    }

    public StrategyWin(Set<String> strategyCodeSet) {
        String name = strategyCodeSet.stream()
                .map(item -> StrategyEnum.codeToEnumMap.get(item).getName())
                .collect(Collectors.joining(" "));

        this.strategyCode = String.join(" ", strategyCodeSet);
        this.strategyName = name;
        this.level = strategyCodeSet.size();
    }
}
