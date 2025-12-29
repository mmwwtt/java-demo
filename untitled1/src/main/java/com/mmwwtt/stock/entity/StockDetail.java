package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

import static com.mmwwtt.stock.common.CommonUtils.isEquals;

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
    private Double allDealQuantity;

    /**
     * 成交额
     */
    private Double allDealPrice;

    /**
     * 前收盘价
     */
    private Double lastPrice;

    /**
     * 涨跌幅
     */
    private Double pricePert;


    // ------------- 新增：K线分析常用计算方法（适配后续判断逻辑）-------------


    /**
     * 上影线长度
     */
    public Double upShadowLen;

    /**
     * 上影线站总长的百分比
     */
    public Double upShadowPert;

    /**
     * 下影线长度
     */
    public Double lowShadowLen;

    /**
     * 下影线站总长的百分比
     */
    public Double lowShadowPert;

    /**
     * 实体长度
     */
    public Double entityLen;

    /**
     * 实体占总长的百分比
     */
    public Double entityPert;

    /**
     * 总长
     */
    public Double allLen;


    /**
     * 5日线
     */
    public Double fiveDayLine;

    /**
     * 10日线
     */
    public Double tenDayLine;

    /**
     * 20日线
     */
    public Double twentyDayLine;

    /**
     * 60日线
     */
    public Double sixtyDayLine;

    /**
     * 涨跌成交比
     */
    public Double pertDivisionQuentity;

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
        allLen = Math.abs(highPrice - lowPrice);
        upShadowLen = highPrice - Math.max(startPrice, endPrice);
        upShadowPert =allLen==0 ? 0 : upShadowLen / allLen;
        lowShadowLen = Math.min(startPrice, endPrice) - lowPrice;
        lowShadowPert =allLen==0 ? 0 : lowShadowLen / allLen;
        entityLen = Math.abs(endPrice - startPrice);
        entityPert = allLen==0 ? 0 :entityLen / allLen;
        isUp = endPrice > startPrice;
        isDown = endPrice < startPrice;
        isBalance = isEquals(endPrice, startPrice);
        pertDivisionQuentity = pricePert / allDealQuantity;
        // 判断是否为十字星（实体长度占总振幅的比例 ≤ 5%）
        isTenStar = (entityLen / allLen) <= 0.05d;
    }

    public static void calc(List<StockDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            StockDetail cur = list.get(i);
            if (list.size() > i + 5) {
                double fiveAverage = list.stream().skip(i).limit(5).mapToDouble(StockDetail::getEndPrice).average().orElse(0);
                cur.setFiveDayLine(fiveAverage);
            }
            if (list.size() > i + 10) {
                double tenAverage = list.stream().skip(i).limit(10).mapToDouble(StockDetail::getEndPrice).average().orElse(0);
                cur.setTenDayLine(tenAverage);
            }
            if (list.size() > i + 20) {
                double twentyAverage = list.stream().skip(i).limit(20).mapToDouble(StockDetail::getEndPrice).average().orElse(0);
                cur.setTwentyDayLine(twentyAverage);
            }
            if (list.size() > i + 60) {
                double sixtyAverage = list.stream().skip(i).limit(60).mapToDouble(StockDetail::getEndPrice).average().orElse(0);
                cur.setSixtyDayLine(sixtyAverage);
            }
        }
    }
}
