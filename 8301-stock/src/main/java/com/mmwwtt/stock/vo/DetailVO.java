package com.mmwwtt.stock.vo;

import lombok.Data;

@Data
public class DetailVO {
    /**
     * 交易时间
     */
    private String t;

    /**
     * 开盘价
     */
    private double o;

    /**
     * 最高价
     */
    private double h;

    /**
     * 最低价
     */
    private double l;

    /**
     * 收盘价
     */
    private double c;

    /**
     * 成交量
     */
    private double v;

    /**
     * 成交额
     */
    private double a;

    /**
     * 前收盘价
     */
    private double pc;

    /**
     * 1停牌；  0不停牌
     */
    private int sf;

    private String stockCode;

    public void setT(String t){
        this.t = t.substring(0, 10);
    }
}
