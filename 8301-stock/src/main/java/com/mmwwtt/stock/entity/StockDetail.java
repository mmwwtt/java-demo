package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Data
@TableName("stock_detail_t")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockDetail {


    /**
     * 主键
     */
    @EqualsAndHashCode.Include
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
    private BigDecimal dealQuantity;

    /**
     * 成交额
     */
    private BigDecimal dealPrice;

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
    private BigDecimal upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    private BigDecimal upShadowPert;

    /**
     * 下影线长度
     */
    private BigDecimal lowShadowLen;

    /**
     * 下影线站总长的百分比
     */
    private BigDecimal lowShadowPert;

    /**
     * 实体长度
     */
    private BigDecimal entityLen;

    /**
     * 实体占总长的百分比
     */
    private BigDecimal entityPert;

    /**
     * 总长
     */
    private BigDecimal allLen;

    /**
     * 5日线
     */
    private BigDecimal fiveDayLine;

    /**
     * 5日最高
     */
    private BigDecimal fiveDayHigh;

    /**
     * 5日最低
     */
    private BigDecimal fiveDayLow;

    /**
     * 5日均量
     * 当日/5日均量
     * 大于 1.5 为温和放量
     * 大于2 为明显放量
     * 小于0.5为严重缩量
     */
    private BigDecimal fiveDayDealQuantity;

    /**
     * 10日线
     */
    private BigDecimal tenDayLine;

    /**
     * 10日最高
     */
    private BigDecimal tenDayHigh;

    /**
     * 10日最低
     */
    private BigDecimal tenDayLow;
    /**
     * 10日均量
     */
    private BigDecimal tenDayDealQuantity;

    /**
     * 20日线
     */
    private BigDecimal twentyDayLine;

    /**
     * 20日最高
     */
    private BigDecimal twentyDayHigh;

    /**
     * 20日最低
     */
    private BigDecimal twentyDayLow;

    /**
     * 20日均量
     */
    private BigDecimal twentyDayDealQuantity;

    /**
     * 60日线
     */
    private BigDecimal sixtyDayLine;

    /**
     * 60日最高
     */
    private BigDecimal sixtyDayHigh;

    /**
     * 60日最低
     */
    private BigDecimal sixtyDayLow;

    /**
     * 60日均量
     */
    private BigDecimal sixtyDayDealQuantity;

    /**
     * 涨跌成交比
     */
    private BigDecimal pertDivisionQuantity;

    /**
     * 是否为阳线(收盘价高于开盘价，  可能是-9  -> -1  也是阳线)
     */
    private Boolean isUp;

    /**
     * 是否为阴线
     */
    private Boolean isDown;

    /**
     * 开盘价是否等于收盘价
     */
    private Boolean isBalance;

    /**
     * 是否为十字星(英文名 doji)
     */
    private Boolean isTenStar;

    /**
     * 是否被百分比策略过滤 true保留  false被过滤
     */
    @TableField(exist = false)
    private Boolean isFilterPert;

    /**
     * 下个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next1;

    /**
     * 日后第2个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next2;

    /**
     * 当天到2天后的涨幅
     */
    private BigDecimal next2PricePert;

    /**
     * 日后第3个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next3;

    /**
     * 当天到3天后的涨幅
     */
    private BigDecimal next3PricePert;

    /**
     * 日后第4个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next4;

    /**
     * 当天到4天后的涨幅
     */
    private BigDecimal next4PricePert;


    /**
     * 日后第5个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next5;

    /**
     * 当天到5天后的涨幅
     */
    private BigDecimal next5PricePert;

    /**
     * 当天到5天内最高的涨幅
     */
    private BigDecimal next5MaxPricePert;


    /**
     * 日后第10个交易日细节
     */
    @TableField(exist = false)
    private StockDetail next10;

    /**
     * 当天到10天后的涨幅
     */
    private BigDecimal next10PricePert;

    /**
     * 当天到10天内最高的涨幅
     */
    private BigDecimal next10MaxPricePert;

    /**
     * 前1天交易日细节
     */
    @TableField(exist = false)
    private StockDetail t1;

    /**
     * 前2天交易日细节
     */
    @TableField(exist = false)
    private StockDetail t2;

    /**
     * 前3天交易日细节
     */
    @TableField(exist = false)
    private StockDetail t3;

    /**
     * 前4天交易日细节
     */
    @TableField(exist = false)
    private StockDetail t4;

    /**
     * 前5天交易日细节
     */
    @TableField(exist = false)
    private StockDetail t5;


    /**
     * 量比  相较5日线
     */
    @TableField(exist = false)
    private BigDecimal quantityRatio;


    /**
     * 放量
     */
    @TableField(exist = false)
    private Boolean isUpQuantity = false;

    /**
     * 明显放量
     */
    @TableField(exist = false)
    private Boolean getIsBigUpQuantity = false;

    /**
     * 缩量
     */
    @TableField(exist = false)
    private Boolean getIsDownQuantity = false;


    /**
     * 较五日均量  放量
     */
    @TableField(exist = false)
    private Boolean isUpQuantityForFive = false;

    /**
     * 较五日均量  明显放量
     */
    @TableField(exist = false)
    private Boolean getIsBigUpQuantityForFive = false;

    /**
     * 较五日均量  缩量
     */
    @TableField(exist = false)
    private Boolean getIsDownQuantityForFive = false;

    public void calc() {
        allLen = subtract(highPrice, lowPrice).abs();
        upShadowLen = subtract(highPrice, max(startPrice, endPrice)).abs();
        upShadowPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(upShadowLen, allLen);
        lowShadowLen = subtract(min(startPrice, endPrice), lowPrice).abs();
        lowShadowPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(lowShadowLen, allLen);
        entityLen = subtract(endPrice, startPrice).abs();
        entityPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(entityLen, allLen);
        isUp = moreThan(endPrice, lastPrice);
        isDown = lessThan(endPrice, lastPrice);
        isBalance = bigDecimalEquals(endPrice, startPrice);
        pertDivisionQuantity = bigDecimalEquals(dealQuantity, "0") ? BigDecimal.ZERO : divide(pricePert, dealQuantity);
        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = moreThan(allLen, "0") && lessThan(entityPert, "0.05");
    }

    public static void calc(List<StockDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            StockDetail cur = list.get(i);
            if (i - 2 >= 0) {
                cur.setNext2PricePert(divide(subtract(list.get(i - 2).getEndPrice(), cur.getEndPrice()), cur.getEndPrice()));
            }
            if (i - 3 >= 0) {
                cur.setNext3PricePert(divide(subtract(list.get(i - 3).getEndPrice(), cur.getEndPrice()), cur.getEndPrice()));
            }
            if (i - 4 >= 0) {
                cur.setNext4PricePert(divide(subtract(list.get(i - 4).getEndPrice(), cur.getEndPrice()), cur.getEndPrice()));
            }
            if (i - 5 >= 0) {
                cur.setNext5PricePert(divide(subtract(list.get(i - 5).getEndPrice(), cur.getEndPrice()), cur.getEndPrice()));
                List<BigDecimal> highPriceList = list.subList(i - 5, i).stream().map(StockDetail::getHighPrice).toList();
                cur.setNext5MaxPricePert(divide(subtract(max(highPriceList), cur.getEndPrice()), cur.getEndPrice()));
            }

            if (i - 10 >= 0) {
                cur.setNext10PricePert(divide(subtract(list.get(i - 10).getEndPrice(), cur.getEndPrice()), cur.getEndPrice()));
                List<BigDecimal> highPriceList = list.subList(i - 10, i).stream().map(StockDetail::getHighPrice).toList();
                cur.setNext10MaxPricePert(divide(subtract(max(highPriceList), cur.getEndPrice()), cur.getEndPrice()));
            }


            if (list.size() > i + 5) {
                List<StockDetail> fiveList = list.stream().skip(i).limit(5).toList();
                BigDecimal fiveAverage = divide(sum(fiveList.stream().map(StockDetail::getEndPrice).toList()), 5);
                cur.setFiveDayLine(fiveAverage);

                BigDecimal fiveDayHigh = max(fiveList.stream().map(StockDetail::getHighPrice).toList());
                cur.setFiveDayHigh(fiveDayHigh);

                BigDecimal fiveDayLow = min(fiveList.stream().map(StockDetail::getLowPrice).toList());
                cur.setFiveDayLow(fiveDayLow);

                BigDecimal fiveDayDealQuantity = divide(sum(fiveList.stream().map(StockDetail::getDealQuantity).toList()), 5);
                cur.setFiveDayDealQuantity(fiveDayDealQuantity);
            }
            if (list.size() > i + 10) {
                List<StockDetail> tenList = list.stream().skip(i).limit(10).toList();
                BigDecimal tenAverage = divide(sum(tenList.stream().map(StockDetail::getEndPrice).toList()), 10);
                cur.setTenDayLine(tenAverage);

                BigDecimal tenDayHigh = max(tenList.stream().map(StockDetail::getHighPrice).toList());
                cur.setTenDayHigh(tenDayHigh);

                BigDecimal tenDayLow = min(tenList.stream().map(StockDetail::getLowPrice).toList());
                cur.setTenDayLow(tenDayLow);

                BigDecimal tenDayDealQuantity = divide(sum(tenList.stream().map(StockDetail::getDealQuantity).toList()), 10);
                cur.setTenDayDealQuantity(tenDayDealQuantity);
            }

            if (list.size() > i + 20) {
                List<StockDetail> twentyList = list.stream().skip(i).limit(20).toList();
                BigDecimal twentyAverage = divide(sum(twentyList.stream().map(StockDetail::getEndPrice).toList()), 20);
                cur.setTwentyDayLine(twentyAverage);

                BigDecimal twentyDayHigh = max(twentyList.stream().map(StockDetail::getHighPrice).toList());
                cur.setTwentyDayHigh(twentyDayHigh);

                BigDecimal twentyDayLow = min(twentyList.stream().map(StockDetail::getLowPrice).toList());
                cur.setTwentyDayLow(twentyDayLow);

                BigDecimal twentyDayDealQuantity = divide(sum(twentyList.stream().map(StockDetail::getDealQuantity).toList()), 20);
                cur.setTwentyDayDealQuantity(twentyDayDealQuantity);
            }

            if (list.size() > i + 60) {
                List<StockDetail> sixtyList = list.stream().skip(i).limit(60).toList();
                BigDecimal sixtyAverage = divide(sum(sixtyList.stream().map(StockDetail::getEndPrice).toList()), 60);
                cur.setSixtyDayLine(sixtyAverage);

                BigDecimal sixtyDayHigh = max(sixtyList.stream().map(StockDetail::getHighPrice).toList());
                cur.setSixtyDayHigh(sixtyDayHigh);

                BigDecimal sixtyDayLow = min(sixtyList.stream().map(StockDetail::getLowPrice).toList());
                cur.setSixtyDayLow(sixtyDayLow);

                BigDecimal sixtyDayDealQuantity = divide(sum(sixtyList.stream().map(StockDetail::getDealQuantity).toList()), 60);
                cur.setSixtyDayDealQuantity(sixtyDayDealQuantity);
            }
        }
    }

    @Override
    public String toString() {
        return stockDetailId.toString();
    }
}
