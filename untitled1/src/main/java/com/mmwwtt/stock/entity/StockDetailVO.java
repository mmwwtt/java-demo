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
    private float o;

    /**
     * 最高价
     */
    private float h;

    /**
     * 最低价
     */
    private float l;

    /**
     * 收盘价
     */
    private float c;

    /**
     * 成交量
     */
    private float v;

    /**
     * 成交额
     */
    private float a;

    /**
     * 前收盘价
     */
    private float pc;

    /**
     * 1停牌；  0不停牌
     */
    private int sf;

    public float getPert() {
        return (c-pc)/pc;
    }
}
