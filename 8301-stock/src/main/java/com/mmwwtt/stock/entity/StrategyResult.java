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
    private String dateList;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 条件层数
     */
    private Integer level;

    public StrategyResult(Integer level, String strategyCode, String stockCode, List<String> dateList,LocalDateTime createDate) {
        this.level = level;
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.dateList = String.join(" ", dateList);
        this.createDate = createDate;
    }

    public StrategyResult(Integer level,String strategyCode, String stockCode, String dateListStr,LocalDateTime createDate) {
        this.level = level;
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.dateList = dateListStr;
        this.createDate = createDate;
    }

}
