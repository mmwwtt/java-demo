package com.mmwwtt.stock.enums;

import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.entity.StockDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;

@AllArgsConstructor
@Getter
public enum StrategyEnum implements BaseEnum {

    比前一天缩量("0", (StockDetail t0) -> lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity()), ""),

    当天上涨("1", StockDetail::getIsUp, ""),
    当天下跌("2", StockDetail::getIsDown, ""),
    当天是红("3", StockDetail::getIsRed, "收盘高于开盘"),
    当天是绿("4", StockDetail::getIsGreen, "收盘低于开盘"),


    区间20向上("5", StockDetail::getTwentyIsUp, ""),
    区间40向上("6", StockDetail::getFortyIsUp, ""),
    区间60向上("7", StockDetail::getSixtyIsUp, ""),

    区间20向下("5", (StockDetail t0) -> !t0.getTwentyIsUp(), ""),
    区间40向下("6", (StockDetail t0) -> !t0.getFortyIsUp(), ""),
    区间60向下("7", (StockDetail t0) -> !t0.getSixtyIsUp(), ""),

    下影线占比0_10("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0", "0.1"), ""),
    下影线占比10_20("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.1", "0.2"), ""),
    下影线占比20_30("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.2", "0.3"), ""),
    下影线占比30_40("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.3", "0.4"), ""),
    下影线占比40_50("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.4", "0.5"), ""),
    下影线占比50_60("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.5", "0.6"), ""),
    下影线占比60_70("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.6", "0.7"), ""),
    下影线占比70_80("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.7", "0.8"), ""),
    下影线占比80_90("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.8", "0.9"), ""),
    下影线占比90_100("7", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.9", "0.10"), ""),

    下影线长度_0_01("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0", "0.01"), ""),
    下影线长度_01_02("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.01", "0.02"), ""),
    下影线长度_02_03("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.02", "0.03"), ""),
    下影线长度_03_04("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.03", "0.04"), ""),
    下影线长度_04_05("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.04", "0.05"), ""),
    下影线长度_05_06("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.05", "0.06"), ""),
    下影线长度_06_07("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.06", "0.07"), ""),
    下影线长度_07_08("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.07", "0.08"), ""),
    下影线长度_08_09("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.08", "0.09"), ""),
    下影线长度_09_10("7", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.09", "0.10"), ""),


    上影线占比0_10("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0", "0.1"), ""),
    上影线占比10_20("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.1", "0.2"), ""),
    上影线占比20_30("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.2", "0.3"), ""),
    上影线占比30_40("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.3", "0.4"), ""),
    上影线占比40_50("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.4", "0.5"), ""),
    上影线占比50_60("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.5", "0.6"), ""),
    上影线占比60_70("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.6", "0.7"), ""),
    上影线占比70_80("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.7", "0.8"), ""),
    上影线占比80_90("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.8", "0.9"), ""),
    上影线占比90_100("7", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.9", "0.10"), ""),

    上影线长度_0_01("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0", "0.01"), ""),
    上影线长度_01_02("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.01", "0.02"), ""),
    上影线长度_02_03("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.02", "0.03"), ""),
    上影线长度_03_04("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.03", "0.04"), ""),
    上影线长度_04_05("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.04", "0.05"), ""),
    上影线长度_05_06("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.05", "0.06"), ""),
    上影线长度_06_07("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.06", "0.07"), ""),
    上影线长度_07_08("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.07", "0.08"), ""),
    上影线长度_08_09("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.08", "0.09"), ""),
    上影线长度_09_10("7", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.09", "0.10"), ""),


    总长度_0_01("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0", "0.01"), ""),
    总长度_01_02("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.01", "0.02"), ""),
    总长度_02_03("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.02", "0.03"), ""),
    总长度_03_04("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.03", "0.04"), ""),
    总长度_04_05("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.04", "0.05"), ""),
    总长度_05_06("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.05", "0.06"), ""),
    总长度_06_07("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.06", "0.07"), ""),
    总长度_07_08("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.07", "0.08"), ""),
    总长度_08_09("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.08", "0.09"), ""),
    总长度_09_10("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.09", "0.10"), ""),
    总长度_10_11("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.10", "0.11"), ""),
    总长度_11_12("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.11", "0.12"), ""),
    总长度_12_13("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.12", "0.13"), ""),
    总长度_13_14("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.13", "0.14"), ""),
    总长度_14_15("7", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.14", "0.15"), ""),


    区间60_0_10("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0", "0.1"), ""),
    区间60_10_20("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.1", "0.2"), ""),
    区间60_20_30("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.2", "0.3"), ""),
    区间60_30_40("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.3", "0.4"), ""),
    区间60_40_50("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.4", "0.5"), ""),
    区间60_50_60("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.5", "0.6"), ""),
    区间60_60_70("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.6", "0.7"), ""),
    区间60_70_80("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.7", "0.8"), ""),
    区间60_80_90("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.8", "0.9"), ""),
    区间60_90_100("", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.9", "0.10"), ""),

    区间40_0_10("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0", "0.1"), ""),
    区间40_10_20("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.1", "0.2"), ""),
    区间40_20_30("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.2", "0.3"), ""),
    区间40_30_40("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.3", "0.4"), ""),
    区间40_40_50("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.4", "0.5"), ""),
    区间40_50_60("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.5", "0.6"), ""),
    区间40_60_70("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.6", "0.7"), ""),
    区间40_70_80("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.7", "0.8"), ""),
    区间40_80_90("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.8", "0.9"), ""),
    区间40_90_100("", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.9", "0.10"), ""),

    区间20_0_10("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0", "0.1"), ""),
    区间20_10_20("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.1", "0.2"), ""),
    区间20_20_30("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.2", "0.3"), ""),
    区间20_30_40("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.3", "0.4"), ""),
    区间20_40_50("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.4", "0.5"), ""),
    区间20_50_60("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.5", "0.6"), ""),
    区间20_60_70("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.6", "0.7"), ""),
    区间20_70_80("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.7", "0.8"), ""),
    区间20_80_90("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.8", "0.9"), ""),
    区间20_90_100("", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.9", "0.10"), ""),

    穿过5日线("", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
            && lessThan(t0.getLowPrice(), t0.getFiveDayLine()), ""),
    穿过10日线("", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
            && lessThan(t0.getLowPrice(), t0.getTenDayLine()), ""),
    穿过20日线("", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
            && lessThan(t0.getLowPrice(), t0.getTwentyDayLine()), ""),
    穿过40日线("", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
            && lessThan(t0.getLowPrice(), t0.getFortyDayLine()), ""),
    穿过60日线("", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
            && lessThan(t0.getLowPrice(), t0.getSixtyDayLine()), ""),
    ;
    private final String code;
    private final Function<StockDetail, Boolean> runFunc;
    private final String desc;
}
