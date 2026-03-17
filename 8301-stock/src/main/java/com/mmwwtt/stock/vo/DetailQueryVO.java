package com.mmwwtt.stock.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailQueryVO {

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
    private double dealQuantity;

    /**
     * 成交额
     */
    private double dealPrice;

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
    private double upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    private double upShadowPert;

    /**
     * 下影线长度
     */
    private double lowShadowLen;

    /**
     * 下影线站总长的百分比
     */
    private double lowShadowPert;

    /**
     * 实体长度
     */
    private double entityLen;

    /**
     * 实体占总长的百分比
     */
    private double entityPert;

    /**
     * 总长(振幅)
     */
    private double allLen;

    /**
     * 5日线
     */
    private double fiveDayLine;

    /**
     * 5日最高
     */
    private double fiveHigh;

    /**
     * 5日最低
     */
    private double fiveLow;

    /**
     * 5日均量
     * 当日/5日均量
     * 大于 1.5 为温和放量
     * 大于2 为明显放量
     * 小于0.5为严重缩量
     */
    private double fiveDayDealQuantity;

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
    private double tenDayLine;

    /**
     * 10日最高
     */
    private double tenHigh;

    /**
     * 10日最低
     */
    private double tenLow;
    /**
     * 10日均量
     */
    private double tenDayDealQuantity;

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
    private double twentyDayLine;

    /**
     * 20日最高
     */
    private double twentyHigh;

    /**
     * 20日最低
     */
    private double twentyLow;

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
    private double twentyDayDealQuantity;

    /**
     * 40日线
     */
    private double fortyDayLine;

    /**
     * 40日最高
     */
    private double fortyHigh;

    /**
     * 40日最低
     */
    private double fortyLow;

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
    private double fortyDayDealQuantity;

    /**
     * 60日线
     */
    private double sixtyDayLine;

    /**
     * 60日最高
     */
    private double sixtyHigh;

    /**
     * 60日最低
     */
    private double sixtyLow;


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
    private double sixtyDayDealQuantity;

    /**
     * 涨跌成交比
     */
    private double pertDivisionQuantity;

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
    private DetailQueryVO next1;

    /**
     * 日后第2个交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO next2;

    /**
     * 当天到2天后的涨幅
     */
    private double next2PricePert;

    /**
     * 日后第3个交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO next3;

    /**
     * 当天到3天后的涨幅
     */
    private double next3PricePert;

    /**
     * 日后第4个交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO next4;

    /**
     * 当天到4天后的涨幅
     */
    private double next4PricePert;


    /**
     * 日后第5个交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO next5;

    /**
     * 当天到5天后的涨幅
     */
    private double next5PricePert;

    /**
     * 当天到5天内最高的涨幅
     */
    private double next5MaxPricePert;


    /**
     * 日后第10个交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO next10;

    /**
     * 当天到10天后的涨幅
     */
    private double next10PricePert;

    /**
     * 当天到10天内最高的涨幅
     */
    private double next10MaxPricePert;

    /**
     * 前1天交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO t1;

    /**
     * 前2天交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO t2;

    /**
     * 前3天交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO t3;

    /**
     * 前4天交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO t4;

    /**
     * 前5天交易日细节
     */
    @TableField(exist = false)
    private DetailQueryVO t5;

    @TableField(exist = false)
    private DetailQueryVO t6;
    @TableField(exist = false)
    private DetailQueryVO t7;
    @TableField(exist = false)
    private DetailQueryVO t8;
    @TableField(exist = false)
    private DetailQueryVO t9;
    @TableField(exist = false)
    private DetailQueryVO t10;


    /**
     * 量比  相较5日线
     */
    @TableField(exist = false)
    private double quantityRatio;


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
    private double wr;


    /**
     * MACD相关指标
     * EMA = 今日收盘价 × 2/(N+1) + 昨日EMA × (N-1)/(N+1)
     */
    private double ema12;
    private double ema26;

    /**
     * DIF = 12日EMA - 26日EMA
     */
    private double dif;

    /**
     * 今日DEA = 今日DIF × 2/10 + 昨日DEA × 8/10
     */
    private double dea;

    /**
     * MACD柱 = (DIF - DEA) × 2
     * 大于0表示  EMA26 在  EMA12 之上  做多胜率更高(趋势在上涨)   (26天中的高位)
     * 小于0表示  EMA26 在  EMA12 之下  接飞刀概率大(趋势在下跌)    (26天中的低位)
     */
    private double macd;

    /**
     * 5日中的位置
     */
    private double position5;

    /**
     * 10日中的位置
     */
    private double position10;

    /**
     * 在20日中的位置  (收盘价- 20日最低) / (20日最高- 20日最低)   大于80%是高位   小于20%是低位
     */
    private double position20;

    /**
     * 40日中的位置
     */
    private double position40;

    /**
     * 60日中的位置
     */
    private double position60;

    private Integer limit;

    public DetailQueryVO(String stockCode) {
        this.stockCode = stockCode;
    }
}
