package com.mmwwtt.stock.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@TableName(value = "stock_strategy_result_t", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyResult {

    /**
     * 结果id
     */
    @EqualsAndHashCode.Include
    @TableId(type = IdType.AUTO)
    private Long strategyResultId;

    /**
     * 策略编码
     */
    private String strategyCode;

    /**
     * 股票编码
     */
    private String stockCode;


    /**
     * 预测对的列表
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray stockDetailIdList;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 条件层数
     */
    private Integer level;

    public StrategyResult(Integer level, String strategyCode, String stockCode, Set<Integer> stockDetailIdSet, LocalDateTime createDate) {
        this.level = level;
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        JSONArray array = new JSONArray(stockDetailIdSet.size());
        array.addAll(stockDetailIdSet);
        this.stockDetailIdList = array;
        this.createDate = createDate;
    }
}
