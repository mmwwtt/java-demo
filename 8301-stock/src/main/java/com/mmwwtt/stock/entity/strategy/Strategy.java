package com.mmwwtt.stock.entity.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Strategy extends BaseStrategy {

    /**
     * 策略层级
     */
    private Integer level;
}
