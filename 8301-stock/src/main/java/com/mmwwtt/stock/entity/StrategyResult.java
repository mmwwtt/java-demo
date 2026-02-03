package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("stock_strategy_result_t")
@AllArgsConstructor
@NoArgsConstructor
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
    private String winDateList;

    /**
     * 预测错的列表
     */
    private String failDateList;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    public StrategyResult(String strategyCode, String stockCode, List<String> winDateList,
                          List<String> failDateList,LocalDateTime createDate) {
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.winDateList = String.join(" ", winDateList);
        this.failDateList = String.join(" ", failDateList);
        this.createDate = createDate;
    }

    public StrategyResult(String strategyCode, String stockCode, String winDateStr,
                          String failDateStr,LocalDateTime createDate) {
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.winDateList = winDateStr;
        this.failDateList = failDateStr;
        this.createDate = createDate;
    }

}
