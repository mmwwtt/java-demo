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

    public static final List<StrategyEnum> baseStrategyList = new ArrayList<>();
    public static final List<StrategyEnum> base2StrategyList = new ArrayList<>();
    public static final List<StrategyEnum> strategy1DayList = new ArrayList<>();

    public static final List<StrategyEnum> strategy3DayList = new ArrayList<>();
    public static final List<StrategyEnum> strategy4DayList = new ArrayList<>();
    public static final List<StrategyEnum> strategy2_4DayList = new ArrayList<>();
    public static final Map<String, StrategyEnum> codeToEnumMap = new HashMap<>();

    static {
        baseStrategyList.addAll(Arrays.asList(new StrategyEnum("10004", "二连红",
                        (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyEnum("10005", "三连红", (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyEnum("10006", "是十字星", StockDetail::getIsTenStar),
                new StrategyEnum("10007", "多头排列_5日线_大于10_大于20", (StockDetail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyEnum("10010", "区间5向上", StockDetail::getFiveIsUp),
                new StrategyEnum("10011", "区间10向上", StockDetail::getTenIsUp),
                new StrategyEnum("10012", "区间20向上", StockDetail::getTwentyIsUp),
                new StrategyEnum("10013", "区间40向上", StockDetail::getFortyIsUp),
                new StrategyEnum("10014", "区间60向上", StockDetail::getSixtyIsUp),

                new StrategyEnum("10015", "区间5向下", (StockDetail t0) -> !t0.getFiveIsUp()),
                new StrategyEnum("10016", "区间10向下", (StockDetail t0) -> !t0.getTenIsUp()),
                new StrategyEnum("10017", "区间20向下", (StockDetail t0) -> !t0.getTwentyIsUp()),
                new StrategyEnum("10018", "区间40向下", (StockDetail t0) -> !t0.getFortyIsUp()),
                new StrategyEnum("10019", "区间60向下", (StockDetail t0) -> !t0.getSixtyIsUp()),

                //macd相关
                //DIF 快线
                //DEA 慢线
                new StrategyEnum("10020", "DIF线上穿DEA线_金叉", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),
                new StrategyEnum("10030", "macd_小于负2", (StockDetail t0) -> lessThan(t0.getMacd(), "-2")),
                new StrategyEnum("10031", "macd_负2_负1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-2", "-1")),
                new StrategyEnum("10032", "macd_负1_0", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-1", "0")),
                new StrategyEnum("10033", "macd_0_1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "0", "1")),
                new StrategyEnum("10034", "macd_1_2", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "1", "2")),
                new StrategyEnum("10035", "macd_2_10", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "2", "10")),
                new StrategyEnum("10036", "macd_大于10", (StockDetail t0) -> moreThan(t0.getMacd(), "10")),


                new StrategyEnum("10040", "WR威廉指标_上穿负80_脱离超卖区", (StockDetail t0) -> moreThan(t0.getWr(), "-80")
                        && lessThan(t0.getT1().getWr(), "-80")
                        && lessThan(t0.getT2().getWr(), "-80")),


                new StrategyEnum("10100", "上穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFiveDayLine())),
                new StrategyEnum("10101", "上穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTenDayLine())),
                new StrategyEnum("10102", "上穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("10103", "上穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFortyDayLine())),
                new StrategyEnum("10104", "上穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getSixtyDayLine())),

                new StrategyEnum("10105", "下穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFiveDayLine())),
                new StrategyEnum("10106", "下穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTenDayLine())),
                new StrategyEnum("10107", "下穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("10108", "下穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFortyDayLine())),
                new StrategyEnum("10109", "下穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getSixtyDayLine())),


                new StrategyEnum("11000", "下影线占比0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0", "0.1")),
                new StrategyEnum("11001", "下影线占比10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.1", "0.2")),
                new StrategyEnum("11002", "下影线占比20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.2", "0.3")),
                new StrategyEnum("11003", "下影线占比30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.3", "0.4")),
                new StrategyEnum("11004", "下影线占比40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.4", "0.5")),
                new StrategyEnum("11005", "下影线占比50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.5", "0.6")),
                new StrategyEnum("11006", "下影线占比60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.6", "0.7")),
                new StrategyEnum("11007", "下影线占比70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.7", "0.8")),
                new StrategyEnum("11008", "下影线占比80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.8", "0.9")),
                new StrategyEnum("11009", "下影线占比90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.9", "0.10")),


                new StrategyEnum("11020", "上影线占比0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0", "0.1")),
                new StrategyEnum("11021", "上影线占比10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.1", "0.2")),
                new StrategyEnum("11022", "上影线占比20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.2", "0.3")),
                new StrategyEnum("11023", "上影线占比30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.3", "0.4")),
                new StrategyEnum("11024", "上影线占比40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.4", "0.5")),
                new StrategyEnum("11025", "上影线占比50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.5", "0.6")),
                new StrategyEnum("11026", "上影线占比60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.6", "0.7")),
                new StrategyEnum("11027", "上影线占比70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.7", "0.8")),
                new StrategyEnum("11028", "上影线占比80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.8", "0.9")),
                new StrategyEnum("11029", "上影线占比90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.9", "0.10")),

                new StrategyEnum("11030", "上影线长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0", "0.01")),
                new StrategyEnum("11031", "上影线长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.01", "0.02")),
                new StrategyEnum("11032", "上影线长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.02", "0.03")),
                new StrategyEnum("11033", "上影线长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.03", "0.04")),
                new StrategyEnum("11034", "上影线长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.04", "0.05")),
                new StrategyEnum("11035", "上影线长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.05", "0.06")),
                new StrategyEnum("11036", "上影线长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.06", "0.07")),
                new StrategyEnum("11037", "上影线长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.07", "0.08")),
                new StrategyEnum("11038", "上影线长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.08", "0.09")),
                new StrategyEnum("11039", "上影线长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.09", "0.10")),


                new StrategyEnum("11040", "总长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0", "0.01")),
                new StrategyEnum("11041", "总长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.01", "0.02")),
                new StrategyEnum("11042", "总长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.02", "0.03")),
                new StrategyEnum("11043", "总长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.03", "0.04")),
                new StrategyEnum("11044", "总长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.04", "0.05")),
                new StrategyEnum("11045", "总长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.05", "0.06")),
                new StrategyEnum("11046", "总长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.06", "0.07")),
                new StrategyEnum("11047", "总长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.07", "0.08")),
                new StrategyEnum("11048", "总长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.08", "0.09")),
                new StrategyEnum("11049", "总长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.09", "0.10")),
                new StrategyEnum("11050", "总长度_10_11", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.10", "0.11")),
                new StrategyEnum("11051", "总长度_11_12", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.11", "0.12")),
                new StrategyEnum("11052", "总长度_12_13", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.12", "0.13")),
                new StrategyEnum("11053", "总长度_13_14", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.13", "0.14")),
                new StrategyEnum("11054", "总长度_14_15", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.14", "0.15")),


                new StrategyEnum("11060", "区间60_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0", "0.1")),
                new StrategyEnum("11061", "区间60_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.1", "0.2")),
                new StrategyEnum("11062", "区间60_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.2", "0.3")),
                new StrategyEnum("11063", "区间60_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.3", "0.4")),
                new StrategyEnum("11064", "区间60_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.4", "0.5")),
                new StrategyEnum("11065", "区间60_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.5", "0.6")),
                new StrategyEnum("11066", "区间60_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.6", "0.7")),
                new StrategyEnum("11067", "区间60_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.7", "0.8")),
                new StrategyEnum("11068", "区间60_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.8", "0.9")),
                new StrategyEnum("11069", "区间60_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.9", "0.10")),

                new StrategyEnum("11070", "区间40_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0", "0.1")),
                new StrategyEnum("11071", "区间40_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.1", "0.2")),
                new StrategyEnum("11072", "区间40_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.2", "0.3")),
                new StrategyEnum("11073", "区间40_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.3", "0.4")),
                new StrategyEnum("11074", "区间40_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.4", "0.5")),
                new StrategyEnum("11075", "区间40_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.5", "0.6")),
                new StrategyEnum("11076", "区间40_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.6", "0.7")),
                new StrategyEnum("11077", "区间40_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.7", "0.8")),
                new StrategyEnum("11078", "区间40_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.8", "0.9")),
                new StrategyEnum("11079", "区间40_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.9", "0.10")),

                new StrategyEnum("11080", "区间20_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0", "0.1")),
                new StrategyEnum("11081", "区间20_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.1", "0.2")),
                new StrategyEnum("11082", "区间20_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.2", "0.3")),
                new StrategyEnum("11083", "区间20_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.3", "0.4")),
                new StrategyEnum("11084", "区间20_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.4", "0.5")),
                new StrategyEnum("11085", "区间20_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.5", "0.6")),
                new StrategyEnum("11086", "区间20_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.6", "0.7")),
                new StrategyEnum("11087", "区间20_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.7", "0.8")),
                new StrategyEnum("11088", "区间20_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.8", "0.9")),
                new StrategyEnum("11089", "区间20_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.9", "0.10")),


                new StrategyEnum("11090", "上升缺口_00_01", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.00", "0.01");
                }),
                new StrategyEnum("11091", "上升缺口_01_02", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.01", "0.02");
                }),
                new StrategyEnum("11092", "上升缺口_02_03", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.02", "0.03");
                }),
                new StrategyEnum("11093", "上升缺口_03_04", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.03", "0.04");
                }),
                new StrategyEnum("11094", "上升缺口_04_05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.04", "0.05");
                }),
                new StrategyEnum("11095", "上升缺口_大于05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, "0.05");
                }),

                new StrategyEnum("11100", "下影线长度_0_01", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0", "0.01")),
                new StrategyEnum("11101", "下影线长度_01_02", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.01", "0.02")),
                new StrategyEnum("11102", "下影线长度_02_03", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.02", "0.03")),
                new StrategyEnum("11103", "下影线长度_03_04", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.03", "0.04")),
                new StrategyEnum("11104", "下影线长度_04_05", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.04", "0.05")),
                new StrategyEnum("11105", "下影线长度_05_06", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.05", "0.06")),
                new StrategyEnum("11106", "下影线长度_06_07", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.06", "0.07")),
                new StrategyEnum("11107", "下影线长度_07_08", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.07", "0.08")),
                new StrategyEnum("11108", "下影线长度_08_09", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.08", "0.09")),
                new StrategyEnum("11109", "下影线长度_09_10", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.09", "0.10")),
                new StrategyEnum("11110", "下影线长度_10_11", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.10", "0.11")),
                new StrategyEnum("11111", "下影线长度_11_12", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.11", "0.12")),
                new StrategyEnum("11112", "下影线长度_12_13", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.12", "0.13")),
                new StrategyEnum("11113", "下影线长度_13_14", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.13", "0.14")),
                new StrategyEnum("11114", "下影线长度_14_15", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.14", "0.15")),
                new StrategyEnum("11115", "下影线长度_15_16", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.15", "0.16")),
                new StrategyEnum("11116", "下影线长度_16_17", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.16", "0.17")),
                new StrategyEnum("11117", "下影线长度_17_18", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.17", "0.18")),
                new StrategyEnum("11118", "下影线长度_18_19", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.18", "0.19")),
                new StrategyEnum("11119", "下影线长度_19_20", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.19", "0.20")),

                new StrategyEnum("11120", "比前一天缩量_00_10", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(), BigDecimal.ZERO,
                        multiply(t0.getT1().getDealQuantity(), "0.1"))),
                new StrategyEnum("11121", "比前一天缩量_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.1"), multiply(t0.getT1().getDealQuantity(), "0.2"))),
                new StrategyEnum("11122", "比前一天缩量_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.2"), multiply(t0.getT1().getDealQuantity(), "0.3"))),
                new StrategyEnum("11123", "比前一天缩量_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.3"), multiply(t0.getT1().getDealQuantity(), "0.4"))),
                new StrategyEnum("11124", "比前一天缩量_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.4"), multiply(t0.getT1().getDealQuantity(), "0.5"))),
                new StrategyEnum("11125", "比前一天缩量_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.5"), multiply(t0.getT1().getDealQuantity(), "0.6"))),
                new StrategyEnum("11126", "比前一天缩量_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.6"), multiply(t0.getT1().getDealQuantity(), "0.7"))),
                new StrategyEnum("11127", "比前一天缩量_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.7"), multiply(t0.getT1().getDealQuantity(), "0.8"))),
                new StrategyEnum("11128", "比前一天缩量_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.8"), multiply(t0.getT1().getDealQuantity(), "0.9"))),
                new StrategyEnum("11129", "比前一天缩量_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.9"), multiply(t0.getT1().getDealQuantity(), "1.0"))),


                new StrategyEnum("11130", "比前一天放量_00_10", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1"), multiply(t0.getT1().getDealQuantity(), "1.1"))),
                new StrategyEnum("11131", "比前一天放量_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.1"), multiply(t0.getT1().getDealQuantity(), "1.2"))),
                new StrategyEnum("11132", "比前一天放量_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.2"), multiply(t0.getT1().getDealQuantity(), "1.3"))),
                new StrategyEnum("11133", "比前一天放量_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.3"), multiply(t0.getT1().getDealQuantity(), "1.4"))),
                new StrategyEnum("11134", "比前一天放量_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.4"), multiply(t0.getT1().getDealQuantity(), "1.5"))),
                new StrategyEnum("11135", "比前一天放量_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.5"), multiply(t0.getT1().getDealQuantity(), "1.6"))),
                new StrategyEnum("11136", "比前一天放量_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.6"), multiply(t0.getT1().getDealQuantity(), "1.7"))),
                new StrategyEnum("11137", "比前一天放量_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.7"), multiply(t0.getT1().getDealQuantity(), "1.8"))),
                new StrategyEnum("11138", "比前一天放量_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.8"), multiply(t0.getT1().getDealQuantity(), "1.9"))),
                new StrategyEnum("11139", "比前一天放量_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.9"), multiply(t0.getT1().getDealQuantity(), "2.0"))),
                new StrategyEnum("11140", "比前一天放量_100_110", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.0"), multiply(t0.getT1().getDealQuantity(), "2.1"))),
                new StrategyEnum("11141", "比前一天放量_110_120", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.1"), multiply(t0.getT1().getDealQuantity(), "2.2"))),
                new StrategyEnum("11142", "比前一天放量_120_130", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.2"), multiply(t0.getT1().getDealQuantity(), "2.3"))),
                new StrategyEnum("11143", "比前一天放量_130_140", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.3"), multiply(t0.getT1().getDealQuantity(), "2.4"))),
                new StrategyEnum("11144", "比前一天放量_140_150", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.4"), multiply(t0.getT1().getDealQuantity(), "2.5"))),
                new StrategyEnum("11145", "比前一天放量_150_160", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.5"), multiply(t0.getT1().getDealQuantity(), "2.6"))),
                new StrategyEnum("11146", "比前一天放量_160_170", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.6"), multiply(t0.getT1().getDealQuantity(), "2.7"))),
                new StrategyEnum("11147", "比前一天放量_170_180", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.7"), multiply(t0.getT1().getDealQuantity(), "2.8"))),
                new StrategyEnum("11148", "比前一天放量_180_190", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.8"), multiply(t0.getT1().getDealQuantity(), "2.9"))),
                new StrategyEnum("11149", "比前一天放量_大于190", (StockDetail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.9"))),

                new StrategyEnum("11150", "区间10_0_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0", "0.1")),
                new StrategyEnum("11151", "区间10_10_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.1", "0.2")),
                new StrategyEnum("11152", "区间10_20_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.2", "0.3")),
                new StrategyEnum("11153", "区间10_30_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.3", "0.4")),
                new StrategyEnum("11154", "区间10_40_50", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.4", "0.5")),
                new StrategyEnum("11155", "区间10_50_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.5", "0.6")),
                new StrategyEnum("11156", "区间10_60_70", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.6", "0.7")),
                new StrategyEnum("11157", "区间10_70_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.7", "0.8")),
                new StrategyEnum("11158", "区间10_80_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.8", "0.9")),
                new StrategyEnum("11159", "区间10_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.9", "0.10"))));
    }


    static {
        base2StrategyList.addAll(Arrays.asList(new StrategyEnum("20004", "二连红",
                        (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyEnum("20005", "三连红", (StockDetail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyEnum("20006", "是十字星", StockDetail::getIsTenStar),
                new StrategyEnum("20007", "多头排列_5日线_大于10_大于20", (StockDetail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyEnum("20010", "区间5向上", StockDetail::getFiveIsUp),
                new StrategyEnum("20011", "区间10向上", StockDetail::getTenIsUp),
                new StrategyEnum("20012", "区间20向上", StockDetail::getTwentyIsUp),
                new StrategyEnum("20013", "区间40向上", StockDetail::getFortyIsUp),
                new StrategyEnum("20014", "区间60向上", StockDetail::getSixtyIsUp),

                new StrategyEnum("20015", "区间5向下", (StockDetail t0) -> !t0.getFiveIsUp()),
                new StrategyEnum("20016", "区间10向下", (StockDetail t0) -> !t0.getTenIsUp()),
                new StrategyEnum("20017", "区间20向下", (StockDetail t0) -> !t0.getTwentyIsUp()),
                new StrategyEnum("20018", "区间40向下", (StockDetail t0) -> !t0.getFortyIsUp()),
                new StrategyEnum("20019", "区间60向下", (StockDetail t0) -> !t0.getSixtyIsUp()),

                //macd相关
                //DIF 快线
                //DEA 慢线
                new StrategyEnum("20020", "DIF线上穿DEA线_金叉", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),
                new StrategyEnum("20030", "macd_小于负2", (StockDetail t0) -> lessThan(t0.getMacd(), "-2")),
                new StrategyEnum("20031", "macd_负2_负1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-2", "-1")),
                new StrategyEnum("20032", "macd_负1_0", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "-1", "0")),
                new StrategyEnum("20033", "macd_0_1", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "0", "1")),
                new StrategyEnum("20034", "macd_1_2", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "1", "2")),
                new StrategyEnum("20035", "macd_2_10", (StockDetail t0) -> isInRangeNotEquals(t0.getMacd(), "2", "10")),
                new StrategyEnum("20036", "macd_大于10", (StockDetail t0) -> moreThan(t0.getMacd(), "10")),


                new StrategyEnum("20040", "WR威廉指标_上穿负80_脱离超卖区", (StockDetail t0) -> moreThan(t0.getWr(), "-80")
                        && lessThan(t0.getT1().getWr(), "-80")
                        && lessThan(t0.getT2().getWr(), "-80")),


                new StrategyEnum("20100", "上穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFiveDayLine())),
                new StrategyEnum("20101", "上穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTenDayLine())),
                new StrategyEnum("20102", "上穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("20103", "上穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFortyDayLine())),
                new StrategyEnum("20104", "上穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getSixtyDayLine())),

                new StrategyEnum("20105", "下穿过5日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFiveDayLine())),
                new StrategyEnum("20106", "下穿过10日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTenDayLine())),
                new StrategyEnum("20107", "下穿过20日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("20108", "下穿过40日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFortyDayLine())),
                new StrategyEnum("20109", "下穿过60日线", (StockDetail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getSixtyDayLine())),


                new StrategyEnum("21002", "下影线占比20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.2", "0.4")),
                new StrategyEnum("21004", "下影线占比40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.4", "0.6")),
                new StrategyEnum("21006", "下影线占比60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.6", "0.8")),
                new StrategyEnum("21008", "下影线占比80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.8", "1.0")),


                new StrategyEnum("21022", "上影线占比20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.2", "0.4")),
                new StrategyEnum("21024", "上影线占比40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.4", "0.6")),
                new StrategyEnum("21026", "上影线占比60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.6", "0.8")),
                new StrategyEnum("21028", "上影线占比80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.8", "1.0")),

                new StrategyEnum("21031", "上影线长度_00_02", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.00", "0.02")),
                new StrategyEnum("21032", "上影线长度_02_04", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.02", "0.04")),
                new StrategyEnum("21034", "上影线长度_04_06", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.04", "0.06")),
                new StrategyEnum("21036", "上影线长度_06_08", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.06", "0.08")),
                new StrategyEnum("21038", "上影线长度_08_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.08", "0.10")),


                new StrategyEnum("21040", "总长度_0_02", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0", "0.02")),
                new StrategyEnum("21042", "总长度_02_04", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.02", "0.04")),
                new StrategyEnum("21044", "总长度_04_06", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.04", "0.06")),
                new StrategyEnum("21046", "总长度_06_08", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.06", "0.08")),
                new StrategyEnum("21048", "总长度_08_10", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.08", "0.010")),
                new StrategyEnum("21050", "总长度_10_12", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.10", "0.12")),
                new StrategyEnum("21052", "总长度_12_14", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.12", "0.14")),
                new StrategyEnum("21054", "总长度_14_16", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.14", "0.16")),


                new StrategyEnum("21060", "区间60_0_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0", "0.2")),
                new StrategyEnum("21062", "区间60_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.2", "0.4")),
                new StrategyEnum("21064", "区间60_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.4", "0.6")),
                new StrategyEnum("21066", "区间60_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.6", "0.8")),
                new StrategyEnum("21068", "区间60_80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.8", "1.0")),

                new StrategyEnum("21070", "区间40_0_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0", "0.2")),
                new StrategyEnum("21072", "区间40_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.2", "0.4")),
                new StrategyEnum("21074", "区间40_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.4", "0.6")),
                new StrategyEnum("21076", "区间40_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.6", "0.8")),
                new StrategyEnum("21078", "区间40_80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.8", "01.0")),

                new StrategyEnum("21080", "区间20_0_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0", "0.2")),
                new StrategyEnum("21082", "区间20_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.2", "0.4")),
                new StrategyEnum("21084", "区间20_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.4", "0.6")),
                new StrategyEnum("21086", "区间20_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.6", "0.8")),
                new StrategyEnum("21088", "区间20_80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.8", "1.0")),


                new StrategyEnum("21090", "上升缺口_00_02", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.00", "0.02");
                }),

                new StrategyEnum("21092", "上升缺口_02_04", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.02", "0.04");
                }),
                new StrategyEnum("21094", "上升缺口_04_05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.04", "0.05");
                }),
                new StrategyEnum("21095", "上升缺口_大于05", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, "0.05");
                }),

                new StrategyEnum("21100", "下影线长度_0_02", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0", "0.02")),
                new StrategyEnum("21102", "下影线长度_02_04", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.02", "0.04")),
                new StrategyEnum("21104", "下影线长度_04_06", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.04", "0.06")),
                new StrategyEnum("21106", "下影线长度_06_08", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.06", "0.08")),
                new StrategyEnum("21108", "下影线长度_08_10", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.08", "0.010")),
                new StrategyEnum("21110", "下影线长度_10_12", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.10", "0.12")),
                new StrategyEnum("21112", "下影线长度_12_14", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.12", "0.14")),
                new StrategyEnum("21114", "下影线长度_14_16", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.14", "0.16")),
                new StrategyEnum("21116", "下影线长度_16_18", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.16", "0.18")),
                new StrategyEnum("21118", "下影线长度_18_20", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.18", "0.20")),

                new StrategyEnum("21120", "比前一天缩量_00_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(), BigDecimal.ZERO,
                        multiply(t0.getT1().getDealQuantity(), "0.2"))),
                new StrategyEnum("21122", "比前一天缩量_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.2"), multiply(t0.getT1().getDealQuantity(), "0.4"))),
                new StrategyEnum("21124", "比前一天缩量_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.4"), multiply(t0.getT1().getDealQuantity(), "0.6"))),
                new StrategyEnum("21126", "比前一天缩量_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.6"), multiply(t0.getT1().getDealQuantity(), "0.8"))),
                new StrategyEnum("21128", "比前一天缩量_80_10", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.8"), multiply(t0.getT1().getDealQuantity(), "1.0"))),


                new StrategyEnum("21130", "比前一天放量_00_20", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1"), multiply(t0.getT1().getDealQuantity(), "1.2"))),
                new StrategyEnum("21132", "比前一天放量_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.2"), multiply(t0.getT1().getDealQuantity(), "1.4"))),
                new StrategyEnum("21134", "比前一天放量_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.4"), multiply(t0.getT1().getDealQuantity(), "1.6"))),
                new StrategyEnum("21136", "比前一天放量_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.6"), multiply(t0.getT1().getDealQuantity(), "1.8"))),
                new StrategyEnum("21138", "比前一天放量_80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.8"), multiply(t0.getT1().getDealQuantity(), "2.0"))),
                new StrategyEnum("21140", "比前一天放量_100_120", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.0"), multiply(t0.getT1().getDealQuantity(), "2.2"))),
                new StrategyEnum("21142", "比前一天放量_120_140", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.2"), multiply(t0.getT1().getDealQuantity(), "2.4"))),
                new StrategyEnum("21144", "比前一天放量_140_160", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.4"), multiply(t0.getT1().getDealQuantity(), "2.6"))),
                new StrategyEnum("21146", "比前一天放量_160_180", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.6"), multiply(t0.getT1().getDealQuantity(), "2.8"))),
                new StrategyEnum("21148", "比前一天放量_180", (StockDetail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.8"))),

                new StrategyEnum("21150", "区间10_0_20", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0", "0.2")),
                new StrategyEnum("21152", "区间10_20_40", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.2", "0.4")),
                new StrategyEnum("21154", "区间10_40_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.4", "0.6")),
                new StrategyEnum("21156", "区间10_60_80", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.6", "0.8")),
                new StrategyEnum("21158", "区间10_80_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.8", "1.0"))));
    }

    static {
        List<StrategyEnum> t0List = baseStrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("0" + item.getCode());
            cur.setName("T0-" + item.getName());
            cur.setRunFunc(item.getRunFunc());
            return cur;
        }).toList();
        strategy1DayList.addAll(t0List);
        for (StrategyEnum strategyEnum : strategy1DayList) {
            codeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }
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

        strategy4DayList.addAll(t0List);
        strategy4DayList.addAll(t1List);
        strategy4DayList.addAll(t2List);
        strategy4DayList.addAll(t3List);
        for (StrategyEnum strategyEnum : strategy4DayList) {
            codeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }
    }

    static {
        List<StrategyEnum> t0List = base2StrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("0" + item.getCode());
            cur.setName("T0-" + item.getName());
            cur.setRunFunc(item.getRunFunc());
            return cur;
        }).toList();

        List<StrategyEnum> t1List = base2StrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("1" + item.getCode());
            cur.setName("T1-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT1()));
            return cur;
        }).toList();

        List<StrategyEnum> t2List = base2StrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("2" + item.getCode());
            cur.setName("T2-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT2()));
            return cur;
        }).toList();

        List<StrategyEnum> t3List = base2StrategyList.stream().map(item -> {
            StrategyEnum cur = VoConvert.INSTANCE.convertTo(item);
            cur.setCode("3" + item.getCode());
            cur.setName("T3-" + item.getName());
            cur.setRunFunc((StockDetail t0) -> item.getRunFunc().apply(t0.getT3()));
            return cur;
        }).toList();

        strategy2_4DayList.addAll(t0List);
        strategy2_4DayList.addAll(t1List);
        strategy2_4DayList.addAll(t2List);
        strategy2_4DayList.addAll(t3List);
        for (StrategyEnum strategyEnum : strategy2_4DayList) {
            codeToEnumMap.put(strategyEnum.getCode(), strategyEnum);
        }
    }
}
