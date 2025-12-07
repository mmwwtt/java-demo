package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock_detail_t")
public class StockDetail {


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long stockDetailId;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 交易时间
     */
    private String dealDate;

    /**
     * 开盘价
     */
    private float startPrice;

    /**
     * 最高价
     */
    private float highPrice;

    /**
     * 最低价
     */
    private float lowPrice;

    /**
     * 收盘价
     */
    private float endPrice;

    /**
     * 成交量
     */
    private float allDealQuantity;

    /**
     * 成交额
     */
    private float allDealPrice;

    /**
     * 前收盘价
     */
    private float lastPrice;

    /**
     * 涨跌幅
     */
    private float pricePert;

    public float getPricePert1() {
        return (endPrice-lastPrice)/lastPrice;
    }
}
