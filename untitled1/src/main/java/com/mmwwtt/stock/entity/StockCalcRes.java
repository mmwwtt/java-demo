package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_calculation_result_t")
public class StockCalcRes {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long calResId;

    /**
     * 策略描述
     */
    private String strategy;

    /**
     * 胜率
     */
    private BigDecimal winRate;

    /**
     * 预测后的平均涨幅
     */
    private BigDecimal percRate;

    /**
     * 符合条件的数据量
     */
    private Integer allCnt;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;
}
