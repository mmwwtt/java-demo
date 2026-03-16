package com.mmwwtt.stock.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

import java.util.Comparator;
import java.util.List;

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
     * 预测对的列表
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray stockDetailIdList;


    /**
     * 条件层数
     */
    private Integer level;

    public StrategyResult(Integer level, String strategyCode, List<Integer> stockDetailIds) {
        this.level = level;
        this.strategyCode = strategyCode;
        stockDetailIds.sort(Comparator.comparing(Integer::intValue));
        JSONArray array = new JSONArray(stockDetailIds.size());
        array.addAll(stockDetailIds);
        this.stockDetailIdList = array;
    }
}
