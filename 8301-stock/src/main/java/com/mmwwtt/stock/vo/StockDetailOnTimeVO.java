package com.mmwwtt.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDetailOnTimeVO {
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
     * 涨跌幅
     */
    private BigDecimal pc;

    /**
     * 当前价格
     */
    private BigDecimal p;

    /**
     * 成交量
     */
    private BigDecimal v;

    /**
     * 成交量
     */
    private BigDecimal cje;

    /**
     * 昨日收盘价
     */
    private BigDecimal yc;




    private String stockCode;

    public void setT(String t){
        this.t = t.substring(0, 10);
    }
}
