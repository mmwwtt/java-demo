package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("stock_strategy_result_t")
public class StrategyResult {

    /**
     * 结果id
     */
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
    private List<String> winDateList;

    /**
     * 预测错的列表
     */
    private List<String> failDateList;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

}
