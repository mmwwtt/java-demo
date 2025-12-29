package com.mmwwtt.stock.entity;

import lombok.Data;

@Data
public class StockDetailVO {
    /**
     * 交易时间
     */
    private String t;

    /**
     * 开盘价
     */
    private Double o;

    /**
     * 最高价
     */
    private Double h;

    /**
     * 最低价
     */
    private Double l;

    /**
     * 收盘价
     */
    private Double c;

    /**
     * 成交量
     */
    private Double v;

    /**
     * 成交额
     */
    private Double a;

    /**
     * 前收盘价
     */
    private Double pc;

    /**
     * 1停牌；  0不停牌
     */
    private int sf;

    private String stockCode;

    public Double getPert() {
        return (c-pc)/pc;
    }
}
