package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Double winRate;

    /**
     * 百分比叠加后的胜率
     */
    private Double percRate;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;
}
