package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.demo.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_calculation_result_t")
public class StockCalcRes {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long calcResId;

    /**
     * 策略描述
     */
    private String type;

    /**
     * 策略描述
     */
    private String strategyDesc;

    /**
     * 策略描述
     */
    @TableField(exist = false)
    private StockStrategy stockStrategy;

    /**
     * 胜率
     */
    private BigDecimal winRate;

    /**
     * 预测后的平均涨幅
     */
    private BigDecimal percRate;

    /**
     * 预测后的预测对的平均涨幅
     */
    private BigDecimal winPercRate;

    /**
     * 符合条件的数据量
     */
    private Integer allCnt;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    @AllArgsConstructor
    @Getter
    public enum TypeEnum implements BaseEnum {
        INTERVAL("0", "上下影线和涨跌幅区间计算胜率"),
        DETAIL("1", "具体的策略"),
        ;
        private final String code;
        private final String desc;
    }
}
