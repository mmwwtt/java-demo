package com.mmwwtt.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockStrategy {

    /**
     * 上影线下限
     */
    private BigDecimal upShadowLowLimit;

    /**
     * 上影线上线
     */
    private BigDecimal upShadowUpLimit;

    /**
     * 下影线 下限
     */
    private BigDecimal lowShadowLowLimit;

    /**
     * 下影线 上限
     */
    private BigDecimal lowShadowUpLimit;

    /**
     * 涨跌幅下限
     */
    private BigDecimal pricePertLowLimit;

    /**
     * 涨跌幅上限
     */
    private BigDecimal pricePertUpLimit;
}
