package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private StockDetail next;

    /**
     * 日后第5个交易日细节
     */
    @TableField(exist = false)
    private StockDetail nextFive;

    /**
     * 5天内的涨跌幅总和
     */
    @TableField(exist = false)
    private BigDecimal fivePricePert;

    /**
     * 日后第10个交易日细节
     */
    @TableField(exist = false)
    private StockDetail nextTen;

    /**
     * 10天涨跌幅总和
     */
    @TableField(exist = false)
    private BigDecimal tenPricePert;

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
        allLen = highPrice.subtract(lowPrice).abs();
        upShadowLen = highPrice.subtract(startPrice.max(endPrice)).abs();
        upShadowPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : upShadowLen.divide(allLen, 4, RoundingMode.HALF_UP);
        lowShadowLen = startPrice.min(endPrice).subtract(lowPrice).abs();
        lowShadowPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lowShadowLen.divide(allLen, 4, RoundingMode.HALF_UP);
        entityLen = endPrice.subtract(startPrice).abs();
        entityPert = allLen.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : entityLen.divide(allLen, 4, RoundingMode.HALF_UP);
        isUp = endPrice.compareTo(lastPrice) > 0;
        isDown = endPrice.compareTo(lastPrice) < 0;
        isBalance = endPrice.compareTo(startPrice) == 0;
        pertDivisionQuantity = dealQuantity.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : pricePert.divide(dealQuantity, 20, RoundingMode.HALF_UP);
        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = allLen.compareTo(BigDecimal.ZERO) > 0 && entityPert.compareTo(new BigDecimal("0.05")) <= 0;
    }

    public static void calc(List<StockDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            StockDetail cur = list.get(i);
            if (list.size() > i + 5) {
                List<StockDetail> fiveList = list.stream().skip(i).limit(5).toList();
                BigDecimal fiveAverage = fiveList.stream()
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(5), 4, RoundingMode.HALF_UP);
                cur.setFiveDayLine(fiveAverage);

                BigDecimal fiveDayHigh = fiveList.stream().map(StockDetail::getHighPrice).max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setFiveDayHigh(fiveDayHigh);

                BigDecimal fiveDayLow = fiveList.stream().map(StockDetail::getLowPrice).min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setFiveDayLow(fiveDayLow);

                BigDecimal fiveDayDealQuantity = fiveList.stream()
                        .map(StockDetail::getDealQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(5), 4, RoundingMode.HALF_UP);
                ;
                cur.setFiveDayDealQuantity(fiveDayDealQuantity);
            }
            if (list.size() > i + 10) {
                List<StockDetail> tenList = list.stream().skip(i).limit(10).toList();
                BigDecimal tenAverage = tenList.stream()
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(10), 4, RoundingMode.HALF_UP);
                cur.setTenDayLine(tenAverage);

                BigDecimal tenDayHigh = tenList.stream().map(StockDetail::getHighPrice).max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setTenDayHigh(tenDayHigh);

                BigDecimal tenDayLow = tenList.stream().map(StockDetail::getLowPrice).min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setTenDayLow(tenDayLow);

                BigDecimal tenDayDealQuantity = tenList.stream()
                        .map(StockDetail::getDealQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(10), 4, RoundingMode.HALF_UP);
                cur.setTenDayDealQuantity(tenDayDealQuantity);
            }

            if (list.size() > i + 20) {
                List<StockDetail> twentyList = list.stream().skip(i).limit(20).toList();
                BigDecimal twentyAverage = twentyList.stream()
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(20), 4, RoundingMode.HALF_UP);
                cur.setTwentyDayLine(twentyAverage);

                BigDecimal twentyDayHigh = twentyList.stream().map(StockDetail::getHighPrice).max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setTwentyDayHigh(twentyDayHigh);

                BigDecimal twentyDayLow = twentyList.stream().map(StockDetail::getLowPrice).min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setTwentyDayLow(twentyDayLow);

                BigDecimal twentyDayDealQuantity = twentyList.stream()
                        .map(StockDetail::getDealQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(20), 4, RoundingMode.HALF_UP);
                cur.setTwentyDayDealQuantity(twentyDayDealQuantity);
            }

            if (list.size() > i + 60) {
                List<StockDetail> sixtyList = list.stream().skip(i).limit(60).toList();
                BigDecimal sixtyAverage = sixtyList.stream()
                        .map(StockDetail::getEndPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(60), 4, RoundingMode.HALF_UP);
                cur.setSixtyDayLine(sixtyAverage);

                BigDecimal sixtyDayHigh = sixtyList.stream().map(StockDetail::getHighPrice).max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setSixtyDayHigh(sixtyDayHigh);

                BigDecimal sixtyDayLow = sixtyList.stream().map(StockDetail::getLowPrice).min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                cur.setSixtyDayLow(sixtyDayLow);

                BigDecimal sixtyDayDealQuantity = sixtyList.stream()
                        .map(StockDetail::getDealQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(60), 4, RoundingMode.HALF_UP);
                cur.setSixtyDayDealQuantity(sixtyDayDealQuantity);
            }
        }
    }
}
