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

    public static final List<StrategyEnum> base2StrategyList = new ArrayList<>();
    public static final List<StrategyEnum> strategy2_4DayList = new ArrayList<>();
    public static final Map<String, StrategyEnum> codeToEnumMap = new HashMap<>();

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


                new StrategyEnum("21002", "下影线占比10_40", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.1", "0.4")),
                new StrategyEnum("21004", "下影线占比40_70", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.4", "0.7")),
                new StrategyEnum("21006", "下影线占比70_100", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowPert(), "0.7", "1.0")),

                new StrategyEnum("21022", "上影线占比10_40", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.1", "0.4")),
                new StrategyEnum("21024", "上影线占比40_70", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.4", "0.7")),
                new StrategyEnum("21028", "上影线占比70_100", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowPert(), "0.7", "1.0")),

                new StrategyEnum("21031", "上影线长度_00_03", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.00", "0.03")),
                new StrategyEnum("21032", "上影线长度_03_06", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.03", "0.06")),
                new StrategyEnum("21034", "上影线长度_06_10", (StockDetail t0) -> isInRangeNotEquals(t0.getUpShadowLen(), "0.06", "0.10")),

                new StrategyEnum("21040", "总长度_00_03", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0", "0.03")),
                new StrategyEnum("21042", "总长度_03_06", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.03", "0.06")),
                new StrategyEnum("21044", "总长度_06_09", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.06", "0.09")),
                new StrategyEnum("21046", "总长度_09_12", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.09", "0.12")),
                new StrategyEnum("21048", "总长度_12_16", (StockDetail t0) -> isInRangeNotEquals(t0.getAllLen(), "0.12", "0.16")),


                new StrategyEnum("21060", "区间60_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0", "0.3")),
                new StrategyEnum("21062", "区间60_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.3", "0.6")),
                new StrategyEnum("21064", "区间60_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.6", "0.9")),
                new StrategyEnum("21066", "区间60_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition60(), "0.9", "1.0")),

                new StrategyEnum("21070", "区间40_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0", "0.3")),
                new StrategyEnum("21072", "区间40_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.3", "0.6")),
                new StrategyEnum("21074", "区间40_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.6", "0.9")),
                new StrategyEnum("21076", "区间40_90_10", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition40(), "0.9", "1.0")),

                new StrategyEnum("21080", "区间20_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0", "0.3")),
                new StrategyEnum("21082", "区间20_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.3", "0.6")),
                new StrategyEnum("21084", "区间20_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.6", "0.9")),
                new StrategyEnum("21086", "区间20_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition20(), "0.9", "1.0")),


                new StrategyEnum("21090", "上升缺口_00_02", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.00", "0.02");
                }),

                new StrategyEnum("21092", "上升缺口_02_04", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRangeNotEquals(space, "0.02", "0.04");
                }),
                new StrategyEnum("21094", "上升缺口大于_04", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, "0.04");
                }),

                new StrategyEnum("21100", "下影线长度_00_03", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0", "0.03")),
                new StrategyEnum("21102", "下影线长度_03_06", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.03", "0.06")),
                new StrategyEnum("21104", "下影线长度_06_09", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.06", "0.09")),
                new StrategyEnum("21106", "下影线长度_09_12", (StockDetail t0) -> isInRangeNotEquals(t0.getLowShadowLen(), "0.9", "0.12")),
                new StrategyEnum("21107", "下影线长度大于_12", (StockDetail t0) -> moreThan(t0.getLowShadowLen(), "0.12")),


                new StrategyEnum("21120", "比前一天缩量_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(), BigDecimal.ZERO,
                        multiply(t0.getT1().getDealQuantity(), "0.3"))),
                new StrategyEnum("21122", "比前一天缩量_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.3"), multiply(t0.getT1().getDealQuantity(), "0.6"))),
                new StrategyEnum("21124", "比前一天缩量_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "0.6"), multiply(t0.getT1().getDealQuantity(), "0.9"))),


                new StrategyEnum("21130", "比前一天放量_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1"), multiply(t0.getT1().getDealQuantity(), "1.3"))),
                new StrategyEnum("21132", "比前一天放量_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.3"), multiply(t0.getT1().getDealQuantity(), "1.6"))),
                new StrategyEnum("21134", "比前一天放量_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.6"), multiply(t0.getT1().getDealQuantity(), "1.9"))),
                new StrategyEnum("21136", "比前一天放量_90_120", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "1.9"), multiply(t0.getT1().getDealQuantity(), "2.2"))),
                new StrategyEnum("21138", "比前一天放量_120_150", (StockDetail t0) -> isInRangeNotEquals(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.2"), multiply(t0.getT1().getDealQuantity(), "2.5"))),
                new StrategyEnum("21148", "比前一天放量_150", (StockDetail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), "2.5"))),

                new StrategyEnum("21150", "区间10_00_30", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0", "0.3")),
                new StrategyEnum("21152", "区间10_30_60", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.3", "0.6")),
                new StrategyEnum("21154", "区间10_60_90", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.6", "0.9")),
                new StrategyEnum("21156", "区间10_90_100", (StockDetail t0) -> isInRangeNotEquals(t0.getPosition10(), "0.9", "1.0"))







        ));
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
