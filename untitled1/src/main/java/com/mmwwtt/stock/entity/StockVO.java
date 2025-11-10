package com.mmwwtt.stock.entity;

import lombok.Data;

@Data
public class StockVO {
    /**
     * 股票代码
     */
    private String dm;

    /**
     * 股票名称
     */
    private String mc;

    /**
     * 交易所
     */
    private String jys;
}
