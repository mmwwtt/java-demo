package com.mmwwtt.stock.entity;

import com.mmwwtt.stock.convert.VoConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.common.CommonUtils.isInRangeNotEquals;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEnum {

    private String code;
    private String name;
    private Function<StockDetail, Boolean> runFunc;

    public static final List<StrategyEnum> strategyList = new ArrayList<>();

    public static final List<StrategyEnum> baseStrategyList = new ArrayList<>();

    public static final Map<String, StrategyEnum> codeToEnumMap = new HashMap<>();

    static {
        baseStrategyList.addAll(Arrays.asList(new StrategyEnum("0004", "二连红",
                        (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyEnum("0005", "三连红", (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyEnum("0006", "是十字星", StockDetail::getIsTenStar),
                new StrategyEnum("0007", "多头排列_5日线_大于10_大于20", (StockDetail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyEnum("0010", "区间5向上", StockDetail::getFiveIsUp),
                new StrategyEnum("0011", "区间10向上", StockDetail::getTenIsUp),
                new StrategyEnum("0012", "区间20向上", StockDetail::getTwentyIsUp),
                new StrategyEnum("0013", "区间40向上", StockDetail::getFortyIsUp),
                new StrategyEnum("0014", "区间60向上", StockDetail::getSixtyIsUp),

                new StrategyEnum("0015", "区间5向下", (StockDetail t0) -> !t0.getFiveIsUp()),
                new StrategyEnum("0016", "区间10向下", (StockDetail t0) -> !t0.getTenIsUp()),
                new StrategyEnum("0017", "区间20向下", (StockDetail t0) -> !t0.getTwentyIsUp()),
                new StrategyEnum("0018", "区间40向下", (StockDetail t0) -> !t0.getFortyIsUp()),
                new StrategyEnum("0019", "区间60向下", (StockDetail t0) -> !t0.getSixtyIsUp()),

                //macd相关
                //DIF 快线
                //DEA 慢线
                new StrategyEnum("0020", "DIF线上穿DEA线_金叉", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),
                new StrategyEnum("0030", "macd_小于负2", (StockDetail t0) -> lessThan(t0.getMacd(), "-2")),
                new StrategyEnum("0031", "macd_负2_负1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-2", "-1")),
                new StrategyEnum("0032", "macd_负1_0", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-1", "0")),
                new StrategyEnum("0033", "macd_0_1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "0", "1")),
                new StrategyEnum("0034", "macd_1_2", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "1", "2")),
                new StrategyEnum("0035", "macd_2_10", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "2", "10")),
                new StrategyEnum("0036", "macd_大于10", (StockDetail t0) -> moreThan(t0.getMacd(), "10")),


                new StrategyEnum("0040", "WR威廉指标_上穿负80_脱离超卖区", (StockDetail t0) -> moreThan(t0.getWr(), "-80")
                        && lessThan(t0.getT1().getWr(), "-80")
                        && lessThan(t0.getT2().getWr(), "-80")),


                new StrategyEnum("0100", "上穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFiveDayLine())),
                new StrategyEnum("0101", "上穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTenDayLine())),
                new StrategyEnum("0102", "上穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("0103", "上穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFortyDayLine())),
                new StrategyEnum("0104", "上穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getSixtyDayLine())),

                new StrategyEnum("0105", "下穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFiveDayLine())),
                new StrategyEnum("0106", "下穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTenDayLine())),
                new StrategyEnum("0107", "下穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("0108", "下穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFortyDayLine())),
                new StrategyEnum("0109", "下穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getSixtyDayLine())),


                new StrategyEnum("1000", "下影线占比0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0", "0.1")),
                new StrategyEnum("1001", "下影线占比10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.1", "0.2")),
                new StrategyEnum("1002", "下影线占比20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.2", "0.3")),
                new StrategyEnum("1003", "下影线占比30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.3", "0.4")),
                new StrategyEnum("1004", "下影线占比40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.4", "0.5")),
                new StrategyEnum("1005", "下影线占比50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.5", "0.6")),
                new StrategyEnum("1006", "下影线占比60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.6", "0.7")),
                new StrategyEnum("1007", "下影线占比70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.7", "0.8")),
                new StrategyEnum("1008", "下影线占比80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.8", "0.9")),
                new StrategyEnum("1009", "下影线占比90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.9", "0.10")),


                new StrategyEnum("1020", "上影线占比0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0", "0.1")),
                new StrategyEnum("1021", "上影线占比10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.1", "0.2")),
                new StrategyEnum("1022", "上影线占比20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.2", "0.3")),
                new StrategyEnum("1023", "上影线占比30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.3", "0.4")),
                new StrategyEnum("1024", "上影线占比40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.4", "0.5")),
                new StrategyEnum("1025", "上影线占比50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.5", "0.6")),
                new StrategyEnum("1026", "上影线占比60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.6", "0.7")),
                new StrategyEnum("1027", "上影线占比70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.7", "0.8")),
                new StrategyEnum("1028", "上影线占比80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.8", "0.9")),
                new StrategyEnum("1029", "上影线占比90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.9", "0.10")),

                new StrategyEnum("1030", "上影线长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0", "0.01")),
                new StrategyEnum("1031", "上影线长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.01", "0.02")),
                new StrategyEnum("1032", "上影线长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.02", "0.03")),
                new StrategyEnum("1033", "上影线长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.03", "0.04")),
                new StrategyEnum("1034", "上影线长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.04", "0.05")),
                new StrategyEnum("1035", "上影线长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.05", "0.06")),
                new StrategyEnum("1036", "上影线长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.06", "0.07")),
                new StrategyEnum("1037", "上影线长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.07", "0.08")),
                new StrategyEnum("1038", "上影线长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.08", "0.09")),
                new StrategyEnum("1039", "上影线长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.09", "0.10")),


                new StrategyEnum("1040", "总长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0", "0.01")),
                new StrategyEnum("1041", "总长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.01", "0.02")),
                new StrategyEnum("1042", "总长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.02", "0.03")),
                new StrategyEnum("1043", "总长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.03", "0.04")),
                new StrategyEnum("1044", "总长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.04", "0.05")),
                new StrategyEnum("1045", "总长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.05", "0.06")),
                new StrategyEnum("1046", "总长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.06", "0.07")),
                new StrategyEnum("1047", "总长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.07", "0.08")),
                new StrategyEnum("1048", "总长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.08", "0.09")),
                new StrategyEnum("1049", "总长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.09", "0.10")),
                new StrategyEnum("1050", "总长度_10_11", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.10", "0.11")),
                new StrategyEnum("1051", "总长度_11_12", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.11", "0.12")),
                new StrategyEnum("1052", "总长度_12_13", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.12", "0.13")),
                new StrategyEnum("1053", "总长度_13_14", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.13", "0.14")),
                new StrategyEnum("1054", "总长度_14_15", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.14", "0.15")),


                new StrategyEnum("1060", "区间60_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0", "0.1")),
                new StrategyEnum("1061", "区间60_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.1", "0.2")),
                new StrategyEnum("1062", "区间60_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.2", "0.3")),
                new StrategyEnum("1063", "区间60_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.3", "0.4")),
                new StrategyEnum("1064", "区间60_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.4", "0.5")),
                new StrategyEnum("1065", "区间60_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.5", "0.6")),
                new StrategyEnum("1066", "区间60_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.6", "0.7")),
                new StrategyEnum("1067", "区间60_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.7", "0.8")),
                new StrategyEnum("1068", "区间60_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.8", "0.9")),
                new StrategyEnum("1069", "区间60_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.9", "0.10")),

                new StrategyEnum("1070", "区间40_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0", "0.1")),
                new StrategyEnum("1071", "区间40_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.1", "0.2")),
                new StrategyEnum("1072", "区间40_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.2", "0.3")),
                new StrategyEnum("1073", "区间40_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.3", "0.4")),
                new StrategyEnum("1074", "区间40_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.4", "0.5")),
                new StrategyEnum("1075", "区间40_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.5", "0.6")),
                new StrategyEnum("1076", "区间40_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.6", "0.7")),
                new StrategyEnum("1077", "区间40_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.7", "0.8")),
                new StrategyEnum("1078", "区间40_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.8", "0.9")),
                new StrategyEnum("1079", "区间40_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.9", "0.10")),

                new StrategyEnum("1080", "区间20_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0", "0.1")),
                new StrategyEnum("1081", "区间20_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.1", "0.2")),
                new StrategyEnum("1082", "区间20_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.2", "0.3")),
                new StrategyEnum("1083", "区间20_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.3", "0.4")),
                new StrategyEnum("1084", "区间20_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.4", "0.5")),
                new StrategyEnum("1085", "区间20_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.5", "0.6")),
                new StrategyEnum("1086", "区间20_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.6", "0.7")),
                new StrategyEnum("1087", "区间20_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.7", "0.8")),
                new StrategyEnum("1088", "区间20_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.8", "0.9")),
                new StrategyEnum("1089", "区间20_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.9", "0.10")),


                new StrategyEnum("1090", "上升缺口_00_01", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.00", "0.01");
                }),
                new StrategyEnum("1091", "上升缺口_01_02", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.01", "0.02");
                }),
                new StrategyEnum("1092", "上升缺口_02_03", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.02", "0.03");
                }),
                new StrategyEnum("1093", "上升缺口_03_04", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.03", "0.04");
                }),
                new StrategyEnum("1094", "上升缺口_04_05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.04", "0.05");
                }),
                new StrategyEnum("1095", "上升缺口_大于05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, "0.05");
                }),

                new StrategyEnum("1100", "下影线长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0", "0.01")),
                new StrategyEnum("1101", "下影线长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.01", "0.02")),
                new StrategyEnum("1102", "下影线长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.02", "0.03")),
                new StrategyEnum("1103", "下影线长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.03", "0.04")),
                new StrategyEnum("1104", "下影线长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.04", "0.05")),
                new StrategyEnum("1105", "下影线长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.05", "0.06")),
                new StrategyEnum("1106", "下影线长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.06", "0.07")),
                new StrategyEnum("1107", "下影线长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.07", "0.08")),
                new StrategyEnum("1108", "下影线长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.08", "0.09")),
                new StrategyEnum("1109", "下影线长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.09", "0.10")),
                new StrategyEnum("1110", "下影线长度_10_11", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.10", "0.11")),
                new StrategyEnum("1111", "下影线长度_11_12", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.11", "0.12")),
                new StrategyEnum("1112", "下影线长度_12_13", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.12", "0.13")),
                new StrategyEnum("1113", "下影线长度_13_14", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.13", "0.14")),
                new StrategyEnum("1114", "下影线长度_14_15", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.14", "0.15")),
                new StrategyEnum("1115", "下影线长度_15_16", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.15", "0.16")),
                new StrategyEnum("1116", "下影线长度_16_17", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.16", "0.17")),
                new StrategyEnum("1117", "下影线长度_17_18", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.17", "0.18")),
                new StrategyEnum("1118", "下影线长度_18_19", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.18", "0.19")),
                new StrategyEnum("1119", "下影线长度_19_20", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.19", "0.20")),

                new StrategyEnum("1120", "比前一天缩量_00_10", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(), BigDecimal.ZERO,
                        multiply(t0.getT1().getDealQuantity(), "0.1"))),
                new StrategyEnum("1121", "比前一天缩量_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.1"), multiply(t0.getT1().getDealQuantity(), "0.2"))),
                new StrategyEnum("1122", "比前一天缩量_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.2"), multiply(t0.getT1().getDealQuantity(), "0.3"))),
                new StrategyEnum("1123", "比前一天缩量_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.3"), multiply(t0.getT1().getDealQuantity(), "0.4"))),
                new StrategyEnum("1124", "比前一天缩量_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.4"), multiply(t0.getT1().getDealQuantity(), "0.5"))),
                new StrategyEnum("1125", "比前一天缩量_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.5"), multiply(t0.getT1().getDealQuantity(), "0.6"))),
                new StrategyEnum("1126", "比前一天缩量_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.6"), multiply(t0.getT1().getDealQuantity(), "0.7"))),
                new StrategyEnum("1127", "比前一天缩量_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.7"), multiply(t0.getT1().getDealQuantity(), "0.8"))),
                new StrategyEnum("1128", "比前一天缩量_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.8"), multiply(t0.getT1().getDealQuantity(), "0.9"))),
                new StrategyEnum("1129", "比前一天缩量_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.9"), multiply(t0.getT1().getDealQuantity(), "1.0"))),


                new StrategyEnum("1130", "比前一天放量_00_10", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1"), multiply(t0.getT1().getDealQuantity(), "2.8"))),
                new StrategyEnum("1131", "比前一天放量_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.1"), multiply(t0.getT1().getDealQuantity(), "1.2"))),
                new StrategyEnum("1132", "比前一天放量_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.2"), multiply(t0.getT1().getDealQuantity(), "1.3"))),
                new StrategyEnum("1133", "比前一天放量_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.3"), multiply(t0.getT1().getDealQuantity(), "1.4"))),
                new StrategyEnum("1134", "比前一天放量_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.4"), multiply(t0.getT1().getDealQuantity(), "1.5"))),
                new StrategyEnum("1135", "比前一天放量_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.5"), multiply(t0.getT1().getDealQuantity(), "1.6"))),
                new StrategyEnum("1136", "比前一天放量_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.6"), multiply(t0.getT1().getDealQuantity(), "1.7"))),
                new StrategyEnum("1137", "比前一天放量_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.7"), multiply(t0.getT1().getDealQuantity(), "1.8"))),
                new StrategyEnum("1138", "比前一天放量_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.8"), multiply(t0.getT1().getDealQuantity(), "1.9"))),
                new StrategyEnum("1139", "比前一天放量_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.9"), multiply(t0.getT1().getDealQuantity(), "2.0"))),
                new StrategyEnum("1140", "比前一天放量_100_110", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.0"), multiply(t0.getT1().getDealQuantity(), "2.1"))),
                new StrategyEnum("1141", "比前一天放量_110_120", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.1"), multiply(t0.getT1().getDealQuantity(), "2.2"))),
                new StrategyEnum("1142", "比前一天放量_120_130", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.2"), multiply(t0.getT1().getDealQuantity(), "2.3"))),
                new StrategyEnum("1143", "比前一天放量_130_140", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.3"), multiply(t0.getT1().getDealQuantity(), "2.4"))),
                new StrategyEnum("1144", "比前一天放量_140_150", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.4"), multiply(t0.getT1().getDealQuantity(), "2.5"))),
                new StrategyEnum("1145", "比前一天放量_150_160", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.5"), multiply(t0.getT1().getDealQuantity(), "2.6"))),
                new StrategyEnum("1146", "比前一天放量_160_170", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.6"), multiply(t0.getT1().getDealQuantity(), "2.7"))),
                new StrategyEnum("1147", "比前一天放量_170_180", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.7"), multiply(t0.getT1().getDealQuantity(), "2.8"))),
                new StrategyEnum("1148", "比前一天放量_180_190", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.8"), multiply(t0.getT1().getDealQuantity(), "2.9"))),
                new StrategyEnum("1149", "比前一天放量_大于190", (StockDetail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.9"))),

                new StrategyEnum("1150", "区间10_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0", "0.1")),
                new StrategyEnum("1151", "区间10_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.1", "0.2")),
                new StrategyEnum("1152", "区间10_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.2", "0.3")),
                new StrategyEnum("1153", "区间10_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.3", "0.4")),
                new StrategyEnum("1154", "区间10_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.4", "0.5")),
                new StrategyEnum("1155", "区间10_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.5", "0.6")),
                new StrategyEnum("1156", "区间10_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.6", "0.7")),
                new StrategyEnum("1157", "区间10_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.7", "0.8")),
                new StrategyEnum("1158", "区间10_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.8", "0.9")),
                new StrategyEnum("1159", "区间10_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.9", "0.10"))));
    }

    static {
        List<StrategyEnum> t0List = baseStrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("0" + item.getCode());
            cur.setName("T0-" + item.getName());
            cur.setRunFunc(item.getRunFunc());
            return cur;
        }).toList();

        List<StrategyEnum> t1List = baseStrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("1" + item.getCode());
            cur.setName("T1-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT1()));
            return cur;
        }).toList();

        List<StrategyEnum> t2List = baseStrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("2" + item.getCode());
            cur.setName("T2-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT2()));
            return cur;
        }).toList();

        List<StrategyEnum> t3List = baseStrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("3" + item.getCode());
            cur.setName("T3-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT3()));
            return cur;
        }).toList();

        strategyList.addAll(t0List);
//        strategyList.addAll(t1List);
//        strategyList.addAll(t2List);
//        strategyList.addAll(t3List);
        for (StrategyEnum strategyEnum : strategyList) {
            codeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }
    }
}
