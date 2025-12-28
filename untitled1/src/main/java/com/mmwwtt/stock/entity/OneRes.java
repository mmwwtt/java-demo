package com.mmwwtt.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneRes {

    /**
     * 预测的日期
     */
    private String dealDate;

    /**
     * 是否预测正确
     */
    private Boolean isCorrect;

    /**
     * 第二天的涨幅
     */
    private double pricePert;

    public OneRes(StockDetail tomorrow) {
        this.pricePert = tomorrow.getPricePert();
        this.setIsCorrect(tomorrow.getPricePert() > 0);
        this.setDealDate(tomorrow.getDealDate());
    }
}
