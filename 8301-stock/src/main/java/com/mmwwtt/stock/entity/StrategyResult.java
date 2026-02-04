package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@TableName("stock_strategy_result_t")
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
    private String stockDetailIdList;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 条件层数
     */
    private Integer level;

    public StrategyResult(Integer level, String strategyCode, String stockCode, List<Long> stockDetailIdList, LocalDateTime createDate) {
        this.level = level;
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.stockDetailIdList = stockDetailIdList.stream().map(String::valueOf).collect(Collectors.joining(" "));
        this.createDate = createDate;
    }

    public StrategyResult(Integer level,String strategyCode, String stockCode, String stockDetailIdListStr,LocalDateTime createDate) {
        this.level = level;
        this.strategyCode = strategyCode;
        this.stockCode = stockCode;
        this.stockDetailIdList = stockDetailIdListStr;
        this.createDate = createDate;
    }

}
