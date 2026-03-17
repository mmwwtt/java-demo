package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "strategy_t", autoResultMap = true)
public class Strategy extends BaseStrategy {

    /**
     * 策略层级
     */
    private Integer level;

    /**
     * 有符合数据的日期天数
     */
    protected Integer dateCnt;
}
