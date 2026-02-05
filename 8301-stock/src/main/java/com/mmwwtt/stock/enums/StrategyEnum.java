package com.mmwwtt.stock.enums;

import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.entity.StockDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@AllArgsConstructor
@Getter
public enum StrategyEnum implements BaseEnum {


    下影线占比0_10("1000", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0", "0.1"), ""),
    下影线占比10_20("1001", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.1", "0.2"), ""),
    下影线占比20_30("1002", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.2", "0.3"), ""),
    下影线占比30_40("1003", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.3", "0.4"), ""),
    下影线占比40_50("1004", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.4", "0.5"), ""),
    下影线占比50_60("1005", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.5", "0.6"), ""),
    下影线占比60_70("1006", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.6", "0.7"), ""),
    下影线占比70_80("1007", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.7", "0.8"), ""),
    下影线占比80_90("1008", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.8", "0.9"), ""),
    下影线占比90_100("1009", (StockDetail t0) -> isInRange(t0.getLowShadowPert(), "0.9", "0.10"), ""),


    上影线占比0_10("1020", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0", "0.1"), ""),
    上影线占比10_20("1021", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.1", "0.2"), ""),
    上影线占比20_30("1022", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.2", "0.3"), ""),
    上影线占比30_40("1023", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.3", "0.4"), ""),
    上影线占比40_50("1024", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.4", "0.5"), ""),
    上影线占比50_60("1025", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.5", "0.6"), ""),
    上影线占比60_70("1026", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.6", "0.7"), ""),
    上影线占比70_80("1027", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.7", "0.8"), ""),
    上影线占比80_90("1028", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.8", "0.9"), ""),
    上影线占比90_100("1029", (StockDetail t0) -> isInRange(t0.getUpShadowPert(), "0.9", "0.10"), ""),

    上影线长度_0_01("1030", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0", "0.01"), ""),
    上影线长度_01_02("1031", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.01", "0.02"), ""),
    上影线长度_02_03("1032", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.02", "0.03"), ""),
    上影线长度_03_04("1033", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.03", "0.04"), ""),
    上影线长度_04_05("1034", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.04", "0.05"), ""),
    上影线长度_05_06("1035", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.05", "0.06"), ""),
    上影线长度_06_07("1036", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.06", "0.07"), ""),
    上影线长度_07_08("1037", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.07", "0.08"), ""),
    上影线长度_08_09("1038", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.08", "0.09"), ""),
    上影线长度_09_10("1039", (StockDetail t0) -> isInRange(t0.getUpShadowLen(), "0.09", "0.10"), ""),


    总长度_0_01("1040", (StockDetail t0) -> isInRange(t0.getAllLen(), "0", "0.01"), ""),
    总长度_01_02("1041", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.01", "0.02"), ""),
    总长度_02_03("1042", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.02", "0.03"), ""),
    总长度_03_04("1043", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.03", "0.04"), ""),
    总长度_04_05("1044", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.04", "0.05"), ""),
    总长度_05_06("1045", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.05", "0.06"), ""),
    总长度_06_07("1046", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.06", "0.07"), ""),
    总长度_07_08("1047", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.07", "0.08"), ""),
    总长度_08_09("1048", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.08", "0.09"), ""),
    总长度_09_10("1049", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.09", "0.10"), ""),
    总长度_10_11("1050", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.10", "0.11"), ""),
    总长度_11_12("1051", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.11", "0.12"), ""),
    总长度_12_13("1052", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.12", "0.13"), ""),
    总长度_13_14("1053", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.13", "0.14"), ""),
    总长度_14_15("1054", (StockDetail t0) -> isInRange(t0.getAllLen(), "0.14", "0.15"), ""),


    区间60_0_10("1060", (StockDetail t0) -> isInRange(t0.getPosition60(), "0", "0.1"), ""),
    区间60_10_20("1061", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.1", "0.2"), ""),
    区间60_20_30("1062", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.2", "0.3"), ""),
    区间60_30_40("1063", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.3", "0.4"), ""),
    区间60_40_50("1064", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.4", "0.5"), ""),
    区间60_50_60("1065", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.5", "0.6"), ""),
    区间60_60_70("1066", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.6", "0.7"), ""),
    区间60_70_80("1067", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.7", "0.8"), ""),
    区间60_80_90("1068", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.8", "0.9"), ""),
    区间60_90_100("1069", (StockDetail t0) -> isInRange(t0.getPosition60(), "0.9", "0.10"), ""),

    区间40_0_10("1070", (StockDetail t0) -> isInRange(t0.getPosition40(), "0", "0.1"), ""),
    区间40_10_20("1071", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.1", "0.2"), ""),
    区间40_20_30("1072", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.2", "0.3"), ""),
    区间40_30_40("1073", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.3", "0.4"), ""),
    区间40_40_50("1074", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.4", "0.5"), ""),
    区间40_50_60("1075", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.5", "0.6"), ""),
    区间40_60_70("1076", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.6", "0.7"), ""),
    区间40_70_80("1077", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.7", "0.8"), ""),
    区间40_80_90("1078", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.8", "0.9"), ""),
    区间40_90_100("1079", (StockDetail t0) -> isInRange(t0.getPosition40(), "0.9", "0.10"), ""),

    区间20_0_10("1080", (StockDetail t0) -> isInRange(t0.getPosition20(), "0", "0.1"), ""),
    区间20_10_20("1081", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.1", "0.2"), ""),
    区间20_20_30("1082", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.2", "0.3"), ""),
    区间20_30_40("1083", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.3", "0.4"), ""),
    区间20_40_50("1084", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.4", "0.5"), ""),
    区间20_50_60("1085", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.5", "0.6"), ""),
    区间20_60_70("1086", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.6", "0.7"), ""),
    区间20_70_80("1087", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.7", "0.8"), ""),
    区间20_80_90("1088", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.8", "0.9"), ""),
    区间20_90_100("1089", (StockDetail t0) -> isInRange(t0.getPosition20(), "0.9", "0.10"), ""),

    上升缺口_00_01("1090", (StockDetail t0) -> {
        BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
        return isInRange(space, "0.00", "0.01");}, ""),
    上升缺口_01_02("1091", (StockDetail t0) -> {
        BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
        return isInRange(space, "0.01", "0.02");}, ""),
    上升缺口_02_03("1092", (StockDetail t0) -> {
        BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
        return isInRange(space, "0.02", "0.03");}, ""),
    上升缺口_03_04("1093", (StockDetail t0) -> {
        BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
        return isInRange(space, "0.03", "0.04");}, ""),
    上升缺口_04_05("1094", (StockDetail t0) -> {
        BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
        return isInRange(space, "0.04", "0.05");}, ""),

    下影线长度_0_01("1100", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0", "0.01"), ""),
    下影线长度_01_02("1101", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.01", "0.02"), ""),
    下影线长度_02_03("1102", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.02", "0.03"), ""),
    下影线长度_03_04("1103", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.03", "0.04"), ""),
    下影线长度_04_05("1104", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.04", "0.05"), ""),
    下影线长度_05_06("1105", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.05", "0.06"), ""),
    下影线长度_06_07("1106", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.06", "0.07"), ""),
    下影线长度_07_08("1107", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.07", "0.08"), ""),
    下影线长度_08_09("1108", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.08", "0.09"), ""),
    下影线长度_09_10("1109", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.09", "0.10"), ""),
    下影线长度_10_11("1110", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.10", "0.11"), ""),
    下影线长度_11_12("1111", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.11", "0.12"), ""),
    下影线长度_12_13("1112", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.12", "0.13"), ""),
    下影线长度_13_14("1113", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.13", "0.14"), ""),
    下影线长度_14_15("1114", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.14", "0.15"), ""),
    下影线长度_15_16("1115", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.15", "0.16"), ""),
    下影线长度_16_17("1116", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.16", "0.17"), ""),
    下影线长度_17_18("1117", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.17", "0.18"), ""),
    下影线长度_18_19("1118", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.18", "0.19"), ""),
    下影线长度_19_20("1119", (StockDetail t0) -> isInRange(t0.getLowShadowLen(), "0.19", "0.20"), ""),





    比前一天缩量("1", (StockDetail t0) -> lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity()), ""),
    比前一天放量("2", (StockDetail t0) -> moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity()), ""),
    涨幅成交比扩大("3", (StockDetail t0) -> lessThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity()), ""),

    二连红("4", (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed(), ""),
    三连红("5", (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed(), ""),

    是十字星("6", StockDetail::getIsTenStar, ""),
    当天上涨("7", StockDetail::getIsUp, ""),
    当天下跌("8", StockDetail::getIsDown, ""),
    当天是红("9", StockDetail::getIsRed, "收盘高于开盘"),
    当天是绿("10", StockDetail::getIsGreen, "收盘低于开盘"),


    区间20向上("11", StockDetail::getTwentyIsUp, ""),
    区间40向上("12", StockDetail::getFortyIsUp, ""),
    区间60向上("13", StockDetail::getSixtyIsUp, ""),

    区间20向下("14", (StockDetail t0) -> !t0.getTwentyIsUp(), ""),
    区间40向下("15", (StockDetail t0) -> !t0.getFortyIsUp(), ""),
    区间60向下("16", (StockDetail t0) -> !t0.getSixtyIsUp(), ""),

    穿过5日线("102", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
            && lessThan(t0.getLowPrice(), t0.getFiveDayLine()), ""),
    穿过10日线("103", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
            && lessThan(t0.getLowPrice(), t0.getTenDayLine()), ""),
    穿过20日线("104", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
            && lessThan(t0.getLowPrice(), t0.getTwentyDayLine()), ""),
    穿过40日线("105", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
            && lessThan(t0.getLowPrice(), t0.getFortyDayLine()), ""),
    穿过60日线("106", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
            && lessThan(t0.getLowPrice(), t0.getSixtyDayLine()), ""),

    ;
    private final String code;
    private final Function<StockDetail, Boolean> runFunc;
    private final String desc;

    public static final Map<String, String> codeToNameMap;
    static {
        codeToNameMap = Arrays.stream(StrategyEnum.values()).collect(Collectors.toMap(StrategyEnum::getCode, StrategyEnum::name));
    }


}
