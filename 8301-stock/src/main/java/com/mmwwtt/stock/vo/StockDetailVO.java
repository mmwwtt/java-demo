package com.mmwwtt.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDetailVO {
    /**
     * 交易时间
     */
    private String t;

    /**
     * 开盘价
     */
    private BigDecimal o;

    /**
     * 最高价
     */
    private BigDecimal h;

    /**
     * 最低价
     */
    private BigDecimal l;

    /**
     * 收盘价
     */
    private BigDecimal c;

    /**
     * 成交量
     */
    private BigDecimal v;

    /**
     * 成交额
     */
    private BigDecimal a;

    /**
     * 前收盘价
     */
    private BigDecimal pc;

    /**
     * 1停牌；  0不停牌
     */
    private int sf;

    private String stockCode;

    public void setT(String t){
        this.t = t.substring(0, 10);
    }
}
