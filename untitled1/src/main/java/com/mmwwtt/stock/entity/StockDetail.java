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
     * 上影线长度
     */
    public double upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    public double upShadowPert;

    /**
     * 下影线长度
     */
    public double lowShadowLen;

    /**
     * 下影线站总长的百分比
     */
    public double lowShadowPert;

    /**
     * 实体长度
     */
    public double entityLen;

    /**
     * 实体占总长的百分比
     */
    public double entityPert;

    /**
     * 总长
     */
    public double allLen;

    /**
     * 是否为阳线(收盘价高于开盘价，  可能是-9  -> -1  也是阳线)
     */
    public boolean isUp;

    /**
     * 是否为阴线
     */
    public boolean isDown;

    /**
     * 开盘价是否等于收盘价
     */
    public boolean isBalance;

    /**
     * 是否为十字星
     */
    public boolean isTenStar;

    public void calc() {
        upShadowLen = highPrice - Math.max(startPrice, endPrice);
        lowShadowLen = Math.min(startPrice, endPrice) - lowPrice;
        allLen = Math.abs(highPrice- lowPrice);
        entityLen = Math.abs(endPrice - startPrice);
        isUp = endPrice > startPrice;
        isDown = endPrice < startPrice;
        isBalance = endPrice == startPrice;

        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = (entityLen / allLen) <= 0.05d;
    }
}
