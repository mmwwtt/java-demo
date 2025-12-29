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
    private double startPrice;

    /**
     * 最高价
     */
    private double highPrice;

    /**
     * 最低价
     */
    private double lowPrice;

    /**
     * 收盘价
     */
    private double endPrice;

    /**
     * 成交量
     */
    private double allDealQuantity;

    /**
     * 成交额
     */
    private double allDealPrice;

    /**
     * 前收盘价
     */
    private double lastPrice;

    /**
     * 涨跌幅
     */
    private double pricePert;


    // ------------- 新增：K线分析常用计算方法（适配后续判断逻辑）-------------
    /**
     * 计算K线实体长度（|收盘价-开盘价|）
     */
    public double getEntityLength() {
        return Math.abs(endPrice - startPrice);
    }

    public double getAllLength() {
        return highPrice- lowPrice;
    }
    /**
     * 计算上影线长度（最高价 - 开盘/收盘的较高值）
     */
    public double getUpperShadowLength() {
        double maxOpenClose = Math.max(startPrice, endPrice);
        return highPrice - maxOpenClose;
    }

    /**
     * 计算下影线长度（开盘/收盘的较低值 - 最低价）
     */
    public double getLowerShadowLength() {
        double minOpenClose = Math.min(startPrice, endPrice);
        return minOpenClose - lowPrice;
    }

    /**
     * 判断是否为阳线（收盘价 > 开盘价）
     */
    public boolean isYangLine() {
        return endPrice > startPrice;
    }

    /**
     * 判断是否为阴线（收盘价 < 开盘价）
     */
    public boolean isYinLine() {
        return endPrice < startPrice;
    }

    /**
     * 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
     * 总振幅 = 最高价 - 最低价
     */
    public boolean isDoji() {
        double totalRange = highPrice - lowPrice;
        if (totalRange == 0) {
            return false;
        }
        return (getEntityLength() / totalRange) <= 0.05d;
    }
}
