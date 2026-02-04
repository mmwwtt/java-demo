package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_strategy_win_t")
@AllArgsConstructor
@NoArgsConstructor
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
    private Integer cnt;

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


}
