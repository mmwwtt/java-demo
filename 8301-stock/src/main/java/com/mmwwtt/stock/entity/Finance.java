package com.mmwwtt.stock.entity;

import lombok.Data;

@Data
public class Finance {

    /**
     * 主键
     */
    private Integer financeId;

    /**
     * 股票编码
     */
    private String stockCode;

    /**
     * 日期
     */
    private String date;

    /**
     * 当天融资融券净买入
     */
    private Long finance;

    /**
     * 融资融券余额
     */
    private Long remain;
}
