package com.mmwwtt.stock.enums;

import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.entity.StockDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.isInRange;
import static com.mmwwtt.stock.common.CommonUtils.lessThan;

@AllArgsConstructor
@Getter
public enum StrategyEnum implements BaseEnum {

    比前一天缩量("0", (StockDetail t0) -> lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity()), ""),

    当天上涨("1", StockDetail::getIsUp, ""),
    当天下跌("2", StockDetail::getIsDown, ""),
    当天是红("3", StockDetail::getIsRed, "收盘高于开盘"),
    当天是绿("4", StockDetail::getIsGreen, "收盘低于开盘"),


    向上20日区间("5", StockDetail::getTwentyIsUp, ""),
    向下40日区间("6", StockDetail::getFortyIsUp, ""),
    向下60日区间("7", StockDetail::getSixtyIsUp, ""),

    下影线占比0_10("7",(StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0", "0.1"),""),
    下影线占比10_20("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.1", "0.2"),""),
    下影线占比20_30("7",(StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.2", "0.3"),""),
    下影线占比30_40("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.3", "0.4"),""),
    下影线占比40_50("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.4", "0.5"),""),
    下影线占比50_60("7",(StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.5", "0.6"),""),
    下影线占比60_70("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.6", "0.7"),""),
    下影线占比70_80("7",(StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.7", "0.8"),""),
    下影线占比80_90("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.8", "0.9"),""),
    下影线占比90_100("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.9", "0.10"),""),
    ;
    private final String code;
    private final Function<StockDetail, Boolean> runFunc;
    private final String desc;
}
