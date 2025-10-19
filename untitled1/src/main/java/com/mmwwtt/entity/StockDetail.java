package com.mmwwtt.entity;

import lombok.Data;

@Data
public class StockDetail {
    /**
     * 交易时间
     */
    private String t;

    /**
     * 开盘价
     */
    private float o;
}
