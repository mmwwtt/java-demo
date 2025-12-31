package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
    private BigDecimal startPrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    private BigDecimal endPrice;

    /**
     * 成交量
     */
    private BigDecimal allDealQuantity;

    /**
     * 成交额
     */
    private BigDecimal allDealPrice;

    /**
     * 前收盘价
     */
    private BigDecimal lastPrice;

    /**
     * 涨跌幅
     */
    private BigDecimal pricePert;


    // ------------- 新增：K线分析常用计算方法（适配后续判断逻辑）-------------


    /**
     * 上影线长度
     */
    public BigDecimal upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    public BigDecimal upShadowPert;

    /**
     * 下影线长度
     */
    public BigDecimal lowShadowLen;

    /**
     * 下影线站总长的百分比
     */
    public BigDecimal lowShadowPert;

    /**
     * 实体长度
     */
    public BigDecimal entityLen;

    /**
     * 实体占总长的百分比
     */
    public BigDecimal entityPert;

    /**
     * 总长
     */
    public BigDecimal allLen;


    /**
     * 5日线
     */
    public BigDecimal fiveDayLine;

    /**
     * 10日线
     */
    public BigDecimal tenDayLine;

    /**
     * 20日线
     */
    public BigDecimal twentyDayLine;

    /**
     * 60日线
     */
    public BigDecimal sixtyDayLine;

    /**
     * 涨跌成交比
     */
    public BigDecimal pertDivisionQuentity;

    /**
     * 是否为阳线(收盘价高于开盘价，  可能是-9  -> -1  也是阳线)
     */
    public Boolean isUp;

    /**
     * 是否为阴线
     */
    public Boolean isDown;

    /**
     * 开盘价是否等于收盘价
     */
    public Boolean isBalance;

    /**
     * 是否为十字星
     */
    public Boolean isTenStar;

    public void calc() {
        allLen = highPrice.subtract(lowPrice).abs();
        upShadowLen = highPrice.subtract(startPrice.max(endPrice));
        upShadowPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : upShadowLen.divide(allLen, 4, RoundingMode.HALF_UP);
        lowShadowLen = startPrice.min(endPrice).subtract(lowPrice);
        lowShadowPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lowShadowLen.divide(allLen, 4, RoundingMode.HALF_UP);
        entityLen = endPrice.subtract(startPrice).abs();
        entityPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : entityLen.divide(allLen, 4, RoundingMode.HALF_UP);
        isUp = endPrice.compareTo(startPrice) > 0;
        isDown = endPrice.compareTo(startPrice) < 0;
        isBalance = endPrice.compareTo(startPrice) == 0;
        pertDivisionQuentity = allDealQuantity.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : pricePert.divide(allDealQuantity, 4, RoundingMode.HALF_UP);
        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = allLen.compareTo(BigDecimal.ZERO) > 0 && entityLen.divide(allLen, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.05")) <= 0;
    }

    public static void calc(List<StockDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            StockDetail cur = list.get(i);
            if (list.size() > i + 5) {
                BigDecimal fiveAverage = list.stream().skip(i).limit(5)
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(5), 4, RoundingMode.HALF_UP);
                cur.setFiveDayLine(fiveAverage);
            }
            if (list.size() > i + 10) {
                BigDecimal tenAverage = list.stream().skip(i).limit(10)
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(10), 4, RoundingMode.HALF_UP);
                cur.setTenDayLine(tenAverage);
            }
            if (list.size() > i + 20) {
                BigDecimal twentyAverage = list.stream().skip(i).limit(20)
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(20), 4, RoundingMode.HALF_UP);
                cur.setTwentyDayLine(twentyAverage);
            }
            if (list.size() > i + 60) {
                BigDecimal sixtyAverage = list.stream().skip(i).limit(60)
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(60), 4, RoundingMode.HALF_UP);
                cur.setSixtyDayLine(sixtyAverage);
            }
        }
    }
}
