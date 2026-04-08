package com.mmwwtt.stock.vo;

import lombok.Data;

@Data
public class DetailOnTimeVO {
    /**
     * 交易时间
     */
    private String t;

    /**
     * 股票代码
     */
    private String dm;

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
     * 涨跌幅
     */
    private double pc;

    /**
     * 当前价格
     */
    private double p;

    /**
     * 成交量
     */
    private double v;

    /**
     * 成交量
     */
    private double cje;

    /**
     * 昨日收盘价
     */
    private double yc;




    private String stockCode;

    public void setT(String t){
        this.t = t.substring(0, 10).replaceAll("-", "");
    }
}
