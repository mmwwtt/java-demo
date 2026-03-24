package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.function.Consumer;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Data
@TableName("detail_t")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Detail {


    /**
     * 主键
     */
    @EqualsAndHashCode.Include
    @TableId(type = IdType.AUTO)
    private Integer detailId;

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
    private Double startPrice;

    /**
     * 最高价
     */
    private Double highPrice;

    /**
     * 最低价
     */
    private Double lowPrice;

    /**
     * 收盘价
     */
    private Double endPrice;

    /**
     * 成交量
     */
    private Double dealQuantity;

    /**
     * 成交额
     */
    private Double dealPrice;

    /**
     * 前收盘价
     */
    private Double lastPrice;


    // ------------- 新增：K线分析常用计算方法（适配后续判断逻辑）-------------


    /**
     * 上影线长度
     */
    private Double upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    private Double upShadowPert;

    /**
     * 下影线长度
     */
    private Double downShadowLen;

    /**
     * 下影线站总长的百分比
     */
    private Double downShadowPert;

    /**
     * 实体长度
     */
    private Double entityLen;

    /**
     * 实体占总长的百分比
     */
    private Double entityPert;

    /**
     * 总长(振幅)
     */
    private Double allLen;

    /**
     * 5日均线
     */
    private Double fiveDayLine;

    /**
     * 5日最高
     */
    private Double fiveHigh;

    /**
     * 5日最低
     */
    private Double fiveLow;

    /**
     * 5日均量
     * 当日/5日均量
     * 大于 1.5 为温和放量
     * 大于2 为明显放量
     * 小于0.5为严重缩量
     */
    private Double fiveDayDealQuantity;

    /**
     * 5日内最高的日期
     */
    private String fiveHighDate;

    /**
     * 5日内最低的日期
     */
    private String fiveLowDate;

    /**
     * 5日内当前日是否处于上涨
     */
    private Boolean fiveIsUp;

    /**
     * 10日线
     */
    private Double tenDayLine;

    /**
     * 10日最高
     */
    private Double tenHigh;

    /**
     * 10日最低
     */
    private Double tenLow;
    /**
     * 10日均量
     */
    private Double tenDayDealQuantity;

    /**
     * 10日内最高的日期
     */
    private String tenHighDate;

    /**
     * 10日内最低的日期
     */
    private String tenLowDate;

    /**
     * 10日内当前日是否处于上涨
     */
    private Boolean tenIsUp;

    /**
     * 20日线
     */
    private Double twentyDayLine;

    /**
     * 20日最高
     */
    private Double twentyHigh;

    /**
     * 20日最低
     */
    private Double twentyLow;

    /**
     * 20日内最高的日期
     */
    private String twentyHighDate;

    /**
     * 20日内最低的日期
     */
    private String twentyLowDate;

    /**
     * 20日内当前日是否处于上涨
     */
    private Boolean twentyIsUp;

    /**
     * 20日均量
     */
    private Double twentyDayDealQuantity;

    /**
     * 40日线
     */
    private Double fortyDayLine;

    /**
     * 40日最高
     */
    private Double fortyHigh;

    /**
     * 40日最低
     */
    private Double fortyLow;

    /**
     * 40日内最高的日期
     */
    private String fortyHighDate;

    /**
     * 40日内最低的日期
     */
    private String fortyLowDate;

    /**
     * 40日内当前日是否处于上涨
     */
    private Boolean fortyIsUp;

    /**
     * 40日均量
     */
    private Double fortyDayDealQuantity;

    /**
     * 60日线
     */
    private Double sixtyDayLine;

    /**
     * 60日最高
     */
    private Double sixtyHigh;

    /**
     * 60日最低
     */
    private Double sixtyLow;


    /**
     * 60日内最高的日期
     */
    private String sixtyHighDate;

    /**
     * 60日内最低的日期
     */
    private String sixtyLowDate;


    /**
     * 60日内当前日是否处于上涨， 离最低近就是上涨， 离最高近就是下跌， 最高最低不能是当天
     */
    private Boolean sixtyIsUp;

    /**
     * 60日均量
     */
    private Double sixtyDayDealQuantity;

    /**
     * 涨跌成交比
     */
    private Double pertDivisionQuantity;

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
     * 当天收盘到后续N天收盘的涨幅
     */
    private Double rise0;
    private Double rise1;
    private Double rise2;
    private Double rise3;
    private Double rise3Max;
    private Double rise3Min;
    private Double rise4;
    private Double rise5;
    private Double rise5Max;
    private Double rise5Min;
    private Double rise10;
    private Double rise10Max;
    private Double rise10Min;
    private Double rise20;
    private Double rise20Max;
    private Double rise20Min;

    /**
     * 后续N天交易日详情
     */
    @TableField(exist = false)
    private Detail next1;

    @TableField(exist = false)
    private Detail next2;

    @TableField(exist = false)
    private Detail next3;

    @TableField(exist = false)
    private Detail next4;

    @TableField(exist = false)
    private Detail next5;

    @TableField(exist = false)
    private Detail next10;

    @TableField(exist = false)
    private Detail next20;
    /**
     * 前N天交易日详情
     */
    @TableField(exist = false)
    private Detail t1;

    @TableField(exist = false)
    private Detail t2;

    @TableField(exist = false)
    private Detail t3;

    @TableField(exist = false)
    private Detail t4;

    @TableField(exist = false)
    private Detail t5;

    @TableField(exist = false)
    private Detail t6;

    @TableField(exist = false)
    private Detail t7;

    @TableField(exist = false)
    private Detail t8;

    @TableField(exist = false)
    private Detail t9;

    @TableField(exist = false)
    private Detail t10;


    /**
     * 量比  相较5日线
     */
    @TableField(exist = false)
    private Double quantityRatio;

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
    private Double wr;


    /**
     * MACD相关指标
     * EMA = 今日收盘价 × 2/(N+1) + 昨日EMA × (N-1)/(N+1)
     */
    private Double ema12;
    private Double ema26;

    /**
     * 快线
     * DIF = 12日EMA - 26日EMA
     */
    private Double dif;

    /**
     * 慢线
     * 今日DEA = 今日DIF × 2/10 + 昨日DEA × 8/10
     */
    private Double dea;

    /**
     * 动能
     * MACD柱 = (DIF - DEA) × 2
     * 大于0表示  EMA12 在  EMA26 之上  做多胜率更高(趋势在上涨)   (26天中的高位)
     * 小于0表示  EMA12 在  EMA26 之下  接飞刀概率大(趋势在下跌)    (26天中的低位)
     */
    private Double macd;

    /**
     * 5日中的位置
     */
    private Double position5;

    /**
     * 10日中的位置
     */
    private Double position10;

    /**
     * 在20日中的位置  (收盘价- 20日最低) / (20日最高- 20日最低)   大于80%是高位   小于20%是低位
     */
    private Double position20;

    /**
     * 40日中的位置
     */
    private Double position40;

    /**
     * 60日中的位置
     */
    private Double position60;

    // ========== 趋势判断补充属性 ==========

    /**
     * RSI相对强弱指标(14日)
     * 计算: RS = 14日内平均涨幅 / 14日内平均跌幅, RSI = 100 - 100/(1+RS)
     * 意义: 0~100, >70超买, <30超卖, 50附近多空平衡
     */
    private Double rsi;

    /**
     * ATR平均真实波幅(14日)
     * 计算: TR = max(当日最高-最低, |当日最高-前收|, |当日最低-前收|), ATR=14日TR的EMA
     * 意义: 反映波动率, 值越大波动越剧烈, 可用于止损/仓位管理
     */
    private Double atr14;

    /**
     * 5日乖离率
     * 计算: (收盘价 - 5日均线) / 5日均线 × 100
     * 意义: >5%偏离均线过远可能回调, <-5%可能反弹
     */
    private Double bias5;

    /**
     * 10日乖离率
     */
    private Double bias10;

    /**
     * 20日乖离率
     */
    private Double bias20;

    /**
     * 均线多头排列强度(0~4)
     * 计算: 满足 5日>10日 + 10日>20日 + 20日>40日 + 40日>60日 的个数
     * 意义: 4=完美多头, 0=无多头排列
     */
    private Integer maAlignBullScore;

    /**
     * 均线空头排列强度(0~4)
     * 计算: 满足 5日<10日 + 10日<20日 + 20日<40日 + 40日<60日 的个数
     */
    private Integer maAlignBearScore;

    /**
     * 布林带位置(0~1)
     * 计算: (收盘价 - 下轨) / (上轨 - 下轨), 上轨=20日均+2×20日标准差, 下轨=20日均-2×20日标准差
     * 意义: >1突破上轨, <0跌破下轨, 0.5在中轨附近
     */
    private Double bollPosition;

    /**
     * 20日均线斜率(日变化率)
     * 计算: (当日20日线 - 前一日20日线) / 前一日20日线
     * 意义: >0均线向上, <0均线向下, 反映趋势加速度
     */
    private Double ma20Slope;

    /**
     * 20日波动率
     * 计算: 20日收盘价标准差 / 20日均价
     * 意义: 值越大波动越大, 可用于评估风险
     */
    private Double volatility20;

    /**
     * 量价背离信号
     * 计算: 价涨量缩=1, 价跌量增=-1, 同向=0
     * 意义: 背离常预示趋势可能反转
     */
    private Integer volumePriceDivergence;


    /**
     * 在拉取数据的时候就进行计算  并保存到数据库中，避免之后重复计算
     */
    public void calc() {
        entityLen = divide(Math.abs(subtract(endPrice, startPrice)), lastPrice);
        upShadowLen = divide(Math.abs(subtract(highPrice, max(startPrice, endPrice))), lastPrice);
        downShadowLen = divide(Math.abs(subtract(lowPrice, min(startPrice, endPrice))), lastPrice);
        allLen = divide(Math.abs(subtract(highPrice, lowPrice)), lastPrice);
        upShadowPert = isEquals(allLen, 0.0) ? 0 : divide(upShadowLen, allLen);
        downShadowPert = isEquals(allLen, 0.0) ? 0 : divide(downShadowLen, allLen);
        entityPert = isEquals(allLen, 0.0) ? 0 : divide(entityLen, allLen);
        isUp = moreThan(endPrice, lastPrice);
        isDown = lessThan(endPrice, lastPrice);
        isRed = moreThan(endPrice, startPrice);
        isGreen = lessThan(endPrice, startPrice);
        isBalance = isEquals(endPrice, startPrice);
        pertDivisionQuantity = isEquals(dealQuantity, 0.0) ? 0 : divide(rise0, dealQuantity);
        isTenStar = moreThan(allLen, 0.0) && lessThan(entityLen, 0.005);
    }


    /**
     * 在拉取数据的时候就进行计算  并保存到数据库中，避免之后重复计算
     */
    public static void calc(List<Detail> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Detail cur = list.get(i);

            //macd相关
            double ema12 = i == list.size() - 1
                    ? multiply(cur.endPrice, 2.0 / (12 + 1))
                    : sum(multiply(cur.endPrice, 2.0 / (12 + 1)), multiply(list.get(i + 1).getEma12(), 11.0 / (12 + 1)));
            cur.setEma12(ema12);

            double ema26 = i == list.size() - 1
                    ? multiply(cur.endPrice, 2.0 / (26 + 1))
                    : sum(multiply(cur.endPrice, 2.0 / (26 + 1)), multiply(list.get(i + 1).getEma26(), 25.0 / (26 + 1)));
            cur.setEma26(ema26);

            cur.setDif(subtract(ema12, ema26));

            Double dea = i == list.size() - 1
                    ? multiply(cur.getDif(), 2.0 / (9 + 1))
                    : sum(multiply(cur.getDif(), 2.0 / (9 + 1)), multiply(list.get(i + 1).getDea(), 8.0 / (9 + 1)));
            cur.setDea(dea);

            cur.setMacd(multiply(subtract(cur.getDif(), cur.getDea()), 2));

        }

        for (int i = 0; i < list.size(); i++) {
            Detail cur = list.get(i);
            if (i - 1 >= 0) {
                cur.setRise1(getRise(list.get(i - 1).getEndPrice(), cur.getEndPrice()));
            }
            if (i - 2 >= 0) {
                cur.setRise2(getRise(list.get(i - 2).getEndPrice(), cur.getEndPrice()));
            }
            if (i - 3 >= 0) {
                cur.setRise3(getRise(list.get(i - 3).getEndPrice(), cur.getEndPrice()));
                List<Double> highPriceList = list.subList(i - 3, i).stream().map(Detail::getHighPrice).toList();
                List<Double> lowPriceList = list.subList(i - 3, i).stream().map(Detail::getLowPrice).toList();
                cur.setRise3Max(getRise(max(highPriceList), cur.getEndPrice()));
                cur.setRise3Min(getRise(min(lowPriceList), cur.getEndPrice()));
            }
            if (i - 4 >= 0) {
                cur.setRise4(getRise(list.get(i - 4).getEndPrice(), cur.getEndPrice()));
            }
            if (i - 5 >= 0) {
                cur.setRise5(getRise(list.get(i - 5).getEndPrice(), cur.getEndPrice()));
                List<Double> highPriceList = list.subList(i - 5, i).stream().map(Detail::getHighPrice).toList();
                List<Double> lowPriceList = list.subList(i - 5, i).stream().map(Detail::getLowPrice).toList();
                cur.setRise5Max(getRise(max(highPriceList), cur.getEndPrice()));
                cur.setRise5Min(getRise(min(lowPriceList), cur.getEndPrice()));
            }

            if (i - 10 >= 0) {
                cur.setRise10(getRise(list.get(i - 10).getEndPrice(), cur.getEndPrice()));
                List<Double> highPriceList = list.subList(i - 10, i).stream().map(Detail::getHighPrice).toList();
                List<Double> lowPriceList = list.subList(i - 10, i).stream().map(Detail::getLowPrice).toList();
                cur.setRise10Max(getRise(max(highPriceList), cur.getEndPrice()));
                cur.setRise10Min(getRise(min(lowPriceList), cur.getEndPrice()));
            }
            if (i - 20 >= 0) {
                cur.setRise20(getRise(list.get(i - 20).getEndPrice(), cur.getEndPrice()));
                List<Double> highPriceList = list.subList(i - 20, i).stream().map(Detail::getHighPrice).toList();
                List<Double> lowPriceList = list.subList(i - 20, i).stream().map(Detail::getLowPrice).toList();
                cur.setRise20Max(getRise(max(highPriceList), cur.getEndPrice()));
                cur.setRise20Min(getRise(min(lowPriceList), cur.getEndPrice()));
            }

            calcIsUp(list, i, 5, cur::setFiveDayLine, cur::setFiveDayDealQuantity, cur::setFiveHigh, cur::setFiveLow,
                    cur::setFiveHighDate, cur::setFiveLowDate, cur::setFiveIsUp);
            calcIsUp(list, i, 10, cur::setTenDayLine, cur::setTenDayDealQuantity, cur::setTenHigh, cur::setTenLow,
                    cur::setTenHighDate, cur::setTenLowDate, cur::setTenIsUp);
            calcIsUp(list, i, 20, cur::setTwentyDayLine, cur::setTwentyDayDealQuantity, cur::setTwentyHigh, cur::setTwentyLow,
                    cur::setTwentyHighDate, cur::setTwentyLowDate, cur::setTwentyIsUp);
            calcIsUp(list, i, 40, cur::setFortyDayLine, cur::setFortyDayDealQuantity, cur::setFortyHigh, cur::setFortyLow,
                    cur::setFortyHighDate, cur::setFortyLowDate, cur::setFortyIsUp);
            calcIsUp(list, i, 60, cur::setSixtyDayLine, cur::setSixtyDayDealQuantity, cur::setSixtyHigh, cur::setSixtyLow,
                    cur::setSixtyHighDate, cur::setSixtyLowDate, cur::setSixtyIsUp);

            if (list.size() > i + 14) {
                Double dayHigh = list.get(i).getHighPrice();
                Double dayLow = list.get(i).getLowPrice();
                for (int j = i + 1; j < i + 14; j++) {
                    dayHigh = max(dayHigh, list.get(j).getHighPrice());
                    dayLow = min(dayLow, list.get(j).getLowPrice());
                }
                cur.setWr(multiply(divide(subtract(dayHigh, cur.endPrice), subtract(dayHigh, dayLow)), -100));
            }

            cur.position5 = divide(subtract(cur.endPrice, cur.fiveLow), subtract(cur.fiveHigh, cur.fiveLow));
            cur.position10 = divide(subtract(cur.endPrice, cur.tenLow), subtract(cur.tenHigh, cur.tenLow));
            cur.position20 = divide(subtract(cur.endPrice, cur.twentyLow), subtract(cur.twentyHigh, cur.twentyLow));
            cur.position40 = divide(subtract(cur.endPrice, cur.fortyLow), subtract(cur.fortyHigh, cur.fortyLow));
            cur.position60 = divide(subtract(cur.endPrice, cur.sixtyLow), subtract(cur.sixtyHigh, cur.sixtyLow));

        }
        for (int i = 0; i < list.size(); i++) {
            Detail cur = list.get(i);
            // RSI(14): 14日涨跌的平均
            if (i + 14 < list.size()) {
                double upSum = 0;
                double downSum = 0;
                for (int j = i; j <= i + 13; j++) {
                    double chg = subtract(list.get(j).getEndPrice(), list.get(j + 1).getEndPrice());
                    if (chg > 0) {
                        upSum += chg;
                    } else if (chg < 0) {
                        downSum += chg;
                    }
                }
                double avgUp = divide(upSum, 14);
                double avgDown = divide(Math.abs(downSum), 14);
                if (isEquals(avgDown, 0.0)) {
                    cur.setRsi(moreThan(avgUp, 0.0) ? 100.0 : 50.0);
                } else {
                    double rs = divide(avgUp, avgDown);
                    cur.setRsi(subtract(100, divide(100, sum(1.0, rs))));
                }
            }

            // ATR(14): TR = max(H-L, |H-前收|, |L-前收|)
            if (i + 14 < list.size()) {
                double atrSum = 0;
                for (int j = i; j <= i + 13; j++) {
                    Detail d = list.get(j);
                    Detail prev = list.get(j + 1);
                    if (d == null || prev == null)
                        continue;
                    double hl = Math.abs(subtract(d.getHighPrice(), d.getLowPrice()));
                    double hc = Math.abs(subtract(d.getHighPrice(), prev.getEndPrice()));
                    double lc = Math.abs(subtract(d.getLowPrice(), prev.getEndPrice()));
                    double tr = max(hl, hc, lc);
                    atrSum = sum(atrSum, tr);
                }
                cur.setAtr14(divide(atrSum, 14));
            }

            // 乖离率
            if (cur.getFiveDayLine() != null && !isEquals(cur.getFiveDayLine(), 0.0)) {
                cur.setBias5(multiply(getRise(cur.endPrice, cur.fiveDayLine), 100));
            }
            if (cur.getTenDayLine() != null && !isEquals(cur.getTenDayLine(), 0.0)) {
                cur.setBias10(multiply(getRise(cur.endPrice, cur.tenDayLine), 100));
            }
            if (cur.getTwentyDayLine() != null && !isEquals(cur.getTwentyDayLine(), 0.0)) {
                cur.setBias20(multiply(getRise(cur.endPrice, cur.twentyDayLine), 100));
            }

            // 均线排列强度
            int bullScore = 0, bearScore = 0;
            if (cur.fiveDayLine != null && cur.tenDayLine != null && moreThan(cur.fiveDayLine, cur.tenDayLine))
                bullScore++;
            if (cur.tenDayLine != null && cur.twentyDayLine != null && moreThan(cur.tenDayLine, cur.twentyDayLine))
                bullScore++;
            if (cur.twentyDayLine != null && cur.fortyDayLine != null && moreThan(cur.twentyDayLine, cur.fortyDayLine))
                bullScore++;
            if (cur.fortyDayLine != null && cur.sixtyDayLine != null && moreThan(cur.fortyDayLine, cur.sixtyDayLine))
                bullScore++;
            if (cur.fiveDayLine != null && cur.tenDayLine != null && lessThan(cur.fiveDayLine, cur.tenDayLine))
                bearScore++;
            if (cur.tenDayLine != null && cur.twentyDayLine != null && lessThan(cur.tenDayLine, cur.twentyDayLine))
                bearScore++;
            if (cur.twentyDayLine != null && cur.fortyDayLine != null && lessThan(cur.twentyDayLine, cur.fortyDayLine))
                bearScore++;
            if (cur.fortyDayLine != null && cur.sixtyDayLine != null && lessThan(cur.fortyDayLine, cur.sixtyDayLine))
                bearScore++;
            cur.setMaAlignBullScore(bullScore);
            cur.setMaAlignBearScore(bearScore);

            // 布林带位置: 中轨=20日均, 上轨=中轨+2σ, 下轨=中轨-2σ
            if (i + 20 < list.size() && cur.twentyDayLine != null) {
                double mean = cur.twentyDayLine;
                double sumSq = 0;
                for (int j = i; j < i + 20; j++) {
                    double diff = subtract(list.get(j).getEndPrice(), mean);
                    sumSq += multiply(diff, diff);
                }
                double std = Math.sqrt(divide(sumSq, 20));
                double upper = add(mean, multiply(std, 2));
                double lower = subtract(mean, multiply(std, 2));
                double bandRange = subtract(upper, lower);
                if (!isEquals(bandRange, 0.0)) {
                    cur.setBollPosition(divide(subtract(cur.endPrice, lower), bandRange));
                }
            }

            // 20日均线斜率
            if (i + 21 < list.size()) {
                Detail prev = list.get(i + 1);
                Double prevMa20 = prev.getTwentyDayLine();
                if (prevMa20 != null) {
                    cur.setMa20Slope(divide(subtract(cur.twentyDayLine, prevMa20), prevMa20));
                }
            }

            // 20日波动率
            if (i + 20 < list.size() && cur.twentyDayLine != null) {
                double sumSq = 0;
                for (int j = i; j < i + 20; j++) {
                    double diff = subtract(list.get(j).getEndPrice(), cur.twentyDayLine);
                    sumSq += multiply(diff, diff);
                }
                double std = Math.sqrt(divide(sumSq, 20));
                cur.setVolatility20(divide(std, cur.twentyDayLine));
            }

            // 量价背离: 当日涨跌 vs 量能变化
            if (i + 1 < list.size() && cur.getT1() != null) {
                boolean priceUp = moreThan(cur.endPrice, cur.getT1().getEndPrice());
                boolean volUp = cur.getDealQuantity() != null && cur.getT1().getDealQuantity() != null
                        && moreThan(cur.getDealQuantity(), cur.getT1().getDealQuantity());
                if (priceUp && !volUp)
                    cur.setVolumePriceDivergence(1);   // 价涨量缩
                else if (!priceUp && volUp)
                    cur.setVolumePriceDivergence(-1); // 价跌量增
                else
                    cur.setVolumePriceDivergence(0);
            }
        }

    }

    private static void calcIsUp(List<Detail> list, Integer curIdx, Integer dayNum,
                                 Consumer<Double> setDayLine, Consumer<Double> setDayDealQuantity,
                                 Consumer<Double> setHigh, Consumer<Double> setLow,
                                 Consumer<String> setHighDate, Consumer<String> setLowDate, Consumer<Boolean> setIsUp) {
        String highDate = "";
        String lowDate = "";
        if (list.size() <= curIdx + dayNum) {
            return;
        }
        double sumEndPrice = 0;
        double sumDealQuantity = 0;
        double dayHigh = list.get(curIdx).getHighPrice();
        double dayLow = list.get(curIdx).getLowPrice();
        for (int i = curIdx; i < curIdx + dayNum; i++) {
            Detail cur1 = list.get(i);
            sumEndPrice = sum(sumEndPrice, cur1.getEndPrice());
            sumDealQuantity = sum(sumDealQuantity, cur1.getDealQuantity());
            dayHigh = max(dayHigh, cur1.getHighPrice());
            dayLow = min(dayLow, cur1.getLowPrice());

            if (isEquals(dayHigh, cur1.getHighPrice())) {
                highDate = cur1.getDealDate();
            }
            if (isEquals(dayLow, cur1.getLowPrice())) {
                lowDate = cur1.getDealDate();
            }
        }
        setDayLine.accept(divide(sumEndPrice, dayNum.doubleValue()));
        setDayDealQuantity.accept(divide(sumDealQuantity, dayNum.doubleValue()));
        setHigh.accept(dayHigh);
        setLow.accept(dayLow);

        //highDate靠前(新)表示 下跌中，   lowDate靠前表示上升
        boolean isUp = highDate.compareTo(lowDate) < 0;
        setHighDate.accept(highDate);
        setLowDate.accept(lowDate);
        setIsUp.accept(isUp);
    }

    @Override
    public String toString() {
        return detailId.toString();
    }
}
