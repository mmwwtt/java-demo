package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
     * 总长(振幅)
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
     * 是否上涨
     */
    private Boolean isUp;

    /**
     * 是否下跌
     */
    private Boolean isDown;

    /**
     * 是否为阳线
     */
    private Boolean isRed;

    /**
     * 是否为阴线
     */
    private Boolean isGreen;

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
    private Boolean isBigUpQuantityForFive = false;

    /**
     * 较五日均量  缩量
     */
    @TableField(exist = false)
    private Boolean isDownQuantityForFive = false;


    /**
     * 威廉指标
     * %R = (Hn − C) / (Hn − Ln) × −100
     * Hn：最近 n 日最高价
     * Ln：最近 n 日最低价
     * C：当日收盘
     * <p>
     * 0  ~ −20：超买区（股价靠近区间顶部）
     * −20~−80：常态区
     * −80~−100：超卖区（股价靠近区间底部）
     */
    private BigDecimal wr;


    /**
     * MACD相关指标
     * EMA = 今日收盘价 × 2/(N+1) + 昨日EMA × (N-1)/(N+1)
     */
    private BigDecimal ema12;
    private BigDecimal ema26;

    /**
     * DIF = 12日EMA - 26日EMA
     */
    private BigDecimal dif;

    /**
     * 今日DEA = 今日DIF × 2/10 + 昨日DEA × 8/10
     */
    private BigDecimal dea;

    /**
     * MACD柱 = (DIF - DEA) × 2
     * 大于0表示  EMA26 在  EMA12 之上  做多胜率更高(趋势在上涨)   (26天中的高位)
     * 小于0表示  EMA26 在  EMA12 之下  接飞刀概率大(趋势在下跌)    (26天中的低位)
     */
    private BigDecimal macd;

    /**
     * 在20日中的位置  (收盘价- 20日最低) / (20日最高- 20日最低)   大于80%是高位   小于20%是低位
     */
    private BigDecimal position;

    //违五日线   前四天的综合/五

    public void calc() {
        entityLen = divide(subtract(endPrice, startPrice).abs(), lastPrice);
        upShadowLen = divide(subtract(highPrice, max(startPrice, endPrice)).abs(), lastPrice);
        lowShadowLen = divide(subtract(min(startPrice, endPrice), lowPrice).abs(), lastPrice);
        allLen = divide(subtract(highPrice, lowPrice).abs(), lastPrice);
        upShadowPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(upShadowLen, allLen);
        lowShadowPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(lowShadowLen, allLen);
        entityPert = bigDecimalEquals(allLen, "0") ? BigDecimal.ZERO : divide(entityLen, allLen);
        isUp = moreThan(endPrice, lastPrice);
        isDown = lessThan(endPrice, lastPrice);
        isRed = moreThan(endPrice, startPrice);
        isGreen = lessThan(endPrice, startPrice);
        isBalance = bigDecimalEquals(endPrice, startPrice);
        pertDivisionQuantity = bigDecimalEquals(dealQuantity, "0") ? BigDecimal.ZERO : pricePert.divide(dealQuantity, 15, RoundingMode.UP);
        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = moreThan(allLen, "0") && lessThan(entityPert, "0.1");
    }

    public static void calc(List<StockDetail> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            StockDetail cur = list.get(i);

            //macd相关
            BigDecimal ema12 = i == list.size() - 1
                    ? multiply(cur.endPrice, 2.0 / (12 + 1))
                    : sum(multiply(cur.endPrice, 2.0 / (12 + 1)), multiply(list.get(i + 1).getEma12(), 11.0 / (12 + 1)));
            cur.setEma12(ema12);

            BigDecimal ema26 = i == list.size() - 1
                    ? multiply(cur.endPrice, 2.0 / (26 + 1))
                    : sum(multiply(cur.endPrice, 2.0 / (26 + 1)), multiply(list.get(i + 1).getEma26(), 25.0 / (26 + 1)));
            cur.setEma26(ema26);

            cur.setDif(subtract(ema12, ema26));

            BigDecimal dea = i == list.size() - 1
                    ? multiply(cur.getDif(), 2 / (9 + 1))
                    : sum(multiply(cur.getDif(), 2 / (9 + 1)), multiply(list.get(i + 1).getDea(), (8 / (9 + 1))));
            cur.setDea(dea);

            cur.setMacd(multiply(subtract(cur.getDif(), cur.getDea()), 2));

        }

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


            List<Pair<Integer, List<Consumer<BigDecimal>>>> dayLinePairs = new ArrayList<>();
            dayLinePairs.add(Pair.of(5, Arrays.asList(cur::setFiveDayLine, cur::setFiveDayDealQuantity, cur::setFiveDayHigh, cur::setFiveDayLow)));
            dayLinePairs.add(Pair.of(10, Arrays.asList(cur::setTenDayLine, cur::setTenDayDealQuantity, cur::setTenDayHigh, cur::setTenDayLow)));
            dayLinePairs.add(Pair.of(20, Arrays.asList(cur::setTwentyDayLine, cur::setTwentyDayDealQuantity, cur::setTwentyDayHigh, cur::setTwentyDayLow)));
            dayLinePairs.add(Pair.of(60, Arrays.asList(cur::setSixtyDayLine, cur::setSixtyDayDealQuantity, cur::setSixtyDayHigh, cur::setSixtyDayLow)));
            for (Pair<Integer, List<Consumer<BigDecimal>>> pair : dayLinePairs) {
                Integer dayNum = pair.getLeft();
                List<Consumer<BigDecimal>> setList = pair.getRight();
                if (list.size() <= i + dayNum) {
                    continue;
                }
                BigDecimal sumEndPrice = BigDecimal.ZERO;
                BigDecimal sumDealQuantity = BigDecimal.ZERO;
                BigDecimal dayHigh = list.get(i).getHighPrice();
                BigDecimal dayLow = list.get(i).getLowPrice();
                for (int j = i; j < i + dayNum; j++) {
                    sumEndPrice = sum(sumEndPrice, list.get(j).getEndPrice());
                    sumDealQuantity = sum(sumDealQuantity, list.get(j).getDealQuantity());
                    dayHigh = max(dayHigh, list.get(j).getHighPrice());
                    dayLow = min(dayLow, list.get(j).getLowPrice());
                }
                setList.get(0).accept(divide(sumEndPrice, dayNum));
                setList.get(1).accept(divide(sumDealQuantity, dayNum));
                setList.get(2).accept(dayHigh);
                setList.get(3).accept(dayLow);
            }

            if (list.size() > i + 14) {
                BigDecimal dayHigh = list.get(i).getHighPrice();
                BigDecimal dayLow = list.get(i).getLowPrice();
                for (int j = i + 1; j < i + 14; j++) {
                    dayHigh = max(dayHigh, list.get(j).getHighPrice());
                    dayLow = min(dayLow, list.get(j).getLowPrice());
                }
                cur.setWr(multiply(divide(subtract(dayHigh, cur.endPrice), subtract(dayHigh, dayLow)), "-100"));
            }

            cur.position = divide(subtract(cur.twentyDayHigh, cur.twentyDayLow), subtract(cur.endPrice, cur.twentyDayLow));
        }

    }

    @Override
    public String toString() {
        return stockDetailId.toString();
    }
}
