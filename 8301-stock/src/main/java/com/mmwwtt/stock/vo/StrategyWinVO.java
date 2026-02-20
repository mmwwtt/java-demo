package com.mmwwtt.stock.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
public class StrategyWinVO {
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
    private BigDecimal fiveMaxPercRateStart;

    /**
     * 预测的5天内最高价 的平均涨幅
     */
    private BigDecimal fiveMaxPercRateEnd;

    /**
     * 预测的10天内最高价 的平均涨幅
     */
    private BigDecimal tenMaxPercRate;

    /**
     * 策略层级
     */
    private Integer level;
}
