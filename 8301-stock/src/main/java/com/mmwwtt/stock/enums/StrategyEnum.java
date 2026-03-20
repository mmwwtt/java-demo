package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.entity.Detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;

/**
 * 第一位   0开头是零散策略   1开头是区间策略    2开头是根据代码生成的
 * 第二,三位   策略类型
 * 第三，四位， 具体策略编码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEnum {

    private String code;
    private String desc;
    private String type;
    private Function<Detail, Boolean> filterFunc;

    public static List<StrategyEnum> baseStrategys;

    public StrategyEnum(String code, String desc, Function<Detail, Boolean> filterFunc) {
        this(code, desc, null, filterFunc);
    }

    static {
        baseStrategys = Arrays.asList(
                new StrategyEnum("00004", "二连红",
                        (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyEnum("00005", "三连红", (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyEnum("00006", "是十字星", Detail::getIsTenStar),
                new StrategyEnum("00007", "多头排列_5日线_大于10_大于20", (Detail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyEnum("00010", "区间5向上", Detail::getFiveIsUp),
                new StrategyEnum("00011", "区间10向上", Detail::getTenIsUp),
                new StrategyEnum("00012", "区间20向上", Detail::getTwentyIsUp),
                new StrategyEnum("00013", "区间40向上", Detail::getFortyIsUp),
                new StrategyEnum("00014", "区间60向上", Detail::getSixtyIsUp),

                new StrategyEnum("00015", "区间5向下", (Detail t0) -> !t0.getFiveIsUp()),
                new StrategyEnum("00016", "区间10向下", (Detail t0) -> !t0.getTenIsUp()),
                new StrategyEnum("00017", "区间20向下", (Detail t0) -> !t0.getTwentyIsUp()),
                new StrategyEnum("00018", "区间40向下", (Detail t0) -> !t0.getFortyIsUp()),
                new StrategyEnum("00019", "区间60向下", (Detail t0) -> !t0.getSixtyIsUp()),

                // 金叉: 当天 DIF 上穿 DEA，且前3天持续 DIF<DEA，避免今天死叉明天金叉的反复跳动
                new StrategyEnum("00020", "DIF线上穿DEA线_金叉", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    if (Objects.isNull(t1) || Objects.isNull(t2) || Objects.isNull(t3)
                            || Objects.isNull(t3.getDif()) || Objects.isNull(t3.getDea())) {
                        return false;
                    }
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),

                // 上穿过: 前3天持续在均线下方，避免今天下穿明天上穿的反复跳动
                new StrategyEnum("00021", "上穿过5日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFiveDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyEnum("00022", "上穿过10日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTenDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyEnum("00023", "上穿过20日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTwentyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyEnum("00024", "上穿过40日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFortyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyEnum("00025", "上穿过60日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getSixtyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),

                // 下穿过: 前3天持续在均线上方，避免今天上穿明天下穿的反复跳动
                new StrategyEnum("00026", "下穿过5日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFiveDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyEnum("00027", "下穿过10日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTenDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyEnum("00028", "下穿过20日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTwentyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyEnum("00029", "下穿过40日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFortyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyEnum("00030", "下穿过60日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getSixtyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),

                // 均线排列强度
                new StrategyEnum("00040", "均线多头排列_1", "maAlignBullScore", (Detail t0) -> Objects.equals(1, t0.getMaAlignBullScore())),
                new StrategyEnum("00041", "均线多头排列_2", "maAlignBullScore", (Detail t0) -> Objects.equals(2, t0.getMaAlignBullScore())),
                new StrategyEnum("00042", "均线空头排列_3", "maAlignBullScore", (Detail t0) -> Objects.equals(3, t0.getMaAlignBullScore())),
                new StrategyEnum("00043", "均线空头排列_4", "maAlignBullScore", (Detail t0) -> Objects.equals(4, t0.getMaAlignBullScore())),


                // 量价背离
                new StrategyEnum("00050", "量价背离_价涨量缩", (Detail t0) -> Integer.valueOf(1).equals(t0.getVolumePriceDivergence())),
                new StrategyEnum("00051", "量价背离_价跌量增", (Detail t0) -> Integer.valueOf(-1).equals(t0.getVolumePriceDivergence())),


                new StrategyEnum("10000", "区间60_0_20", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.0, 0.2)),
                new StrategyEnum("10001", "区间60_10_30", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.1, 0.3)),
                new StrategyEnum("10002", "区间60_20_40", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.2, 0.4)),
                new StrategyEnum("10003", "区间60_30_50", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.3, 0.5)),
                new StrategyEnum("10004", "区间60_40_60", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.4, 0.6)),
                new StrategyEnum("10005", "区间60_50_70", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.5, 0.7)),
                new StrategyEnum("10006", "区间60_60_80", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.6, 0.8)),
                new StrategyEnum("10007", "区间60_70_90", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.7, 0.9)),
                new StrategyEnum("10008", "区间60_80_100", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.8, 1.0)),


                new StrategyEnum("10100", "区间40_00_20", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.0, 0.2)),
                new StrategyEnum("10101", "区间40_10_30", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.1, 0.3)),
                new StrategyEnum("10102", "区间40_20_40", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.2, 0.4)),
                new StrategyEnum("10103", "区间40_30_50", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.3, 0.5)),
                new StrategyEnum("10104", "区间40_40_60", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.4, 0.6)),
                new StrategyEnum("10105", "区间40_50_70", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.5, 0.7)),
                new StrategyEnum("10106", "区间40_60_80", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.6, 0.8)),
                new StrategyEnum("10107", "区间40_70_90", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.7, 0.9)),
                new StrategyEnum("10108", "区间40_80_100", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.8, 1.0)),


                new StrategyEnum("10200", "区间20_00_20", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.0, 0.2)),
                new StrategyEnum("10201", "区间20_10_30", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.1, 0.3)),
                new StrategyEnum("10202", "区间20_20_40", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.2, 0.4)),
                new StrategyEnum("10203", "区间20_30_50", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.3, 0.5)),
                new StrategyEnum("10204", "区间20_40_60", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.4, 0.6)),
                new StrategyEnum("10205", "区间20_50_70", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.5, 0.7)),
                new StrategyEnum("10206", "区间20_60_80", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.6, 0.8)),
                new StrategyEnum("10207", "区间20_70_90", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.7, 0.9)),
                new StrategyEnum("10208", "区间20_80_100", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.8, 1.0)),


                new StrategyEnum("10300", "区间10_00_20", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.0, 0.2)),
                new StrategyEnum("10301", "区间10_10_30", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.1, 0.3)),
                new StrategyEnum("10302", "区间10_20_40", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.2, 0.4)),
                new StrategyEnum("10303", "区间10_30_50", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.3, 0.5)),
                new StrategyEnum("10304", "区间10_40_60", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.4, 0.6)),
                new StrategyEnum("10305", "区间10_50_70", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.5, 0.7)),
                new StrategyEnum("10306", "区间10_60_80", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.6, 0.8)),
                new StrategyEnum("10307", "区间10_70_90", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.7, 0.9)),
                new StrategyEnum("10308", "区间10_80_100", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.8, 1.0)),


                new StrategyEnum("10400", "上升缺口_00_02", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.00, 0.02);
                }),

                new StrategyEnum("10401", "上升缺口_01_03", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.01, 0.03);
                }),
                new StrategyEnum("10402", "上升缺口_02_04", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.02, 0.04);
                }),
                new StrategyEnum("10403", "上升缺口_03_05", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.03, 0.05);
                }),
                new StrategyEnum("10404", "上升缺口大于_05", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, 0.05);
                }),


                new StrategyEnum("10501", "比前一天缩量_00_20", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(), 0.0,
                        multiply(t0.getT1().getDealQuantity(), 0.2))),
                new StrategyEnum("10502", "比前一天缩量_10_30", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.1), multiply(t0.getT1().getDealQuantity(), 0.3))),
                new StrategyEnum("10503", "比前一天缩量_20_40", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.2), multiply(t0.getT1().getDealQuantity(), 0.4))),
                new StrategyEnum("10504", "比前一天缩量_30_50", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.3), multiply(t0.getT1().getDealQuantity(), 0.5))),
                new StrategyEnum("10505", "比前一天缩量_40_60", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.4), multiply(t0.getT1().getDealQuantity(), 0.6))),
                new StrategyEnum("10506", "比前一天缩量_50_70", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.5), multiply(t0.getT1().getDealQuantity(), 0.7))),
                new StrategyEnum("10507", "比前一天缩量_60_80", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.6), multiply(t0.getT1().getDealQuantity(), 0.8))),
                new StrategyEnum("10508", "比前一天缩量_70_90", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.7), multiply(t0.getT1().getDealQuantity(), 0.9))),
                new StrategyEnum("10509", "比前一天缩量_80_100", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.8), multiply(t0.getT1().getDealQuantity(), 1.0))),


                new StrategyEnum("10600", "比前一天放量_00_30", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1), multiply(t0.getT1().getDealQuantity(), 1.3))),
                new StrategyEnum("10601", "比前一天放量_10_40", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.1), multiply(t0.getT1().getDealQuantity(), 1.4))),
                new StrategyEnum("10602", "比前一天放量_20_50", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.2), multiply(t0.getT1().getDealQuantity(), 1.5))),
                new StrategyEnum("10603", "比前一天放量_30_60", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.3), multiply(t0.getT1().getDealQuantity(), 1.6))),
                new StrategyEnum("10604", "比前一天放量_40_70", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.4), multiply(t0.getT1().getDealQuantity(), 1.7))),
                new StrategyEnum("10600", "比前一天放量_50_80", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.5), multiply(t0.getT1().getDealQuantity(), 1.8))),
                new StrategyEnum("10601", "比前一天放量_60_90", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.6), multiply(t0.getT1().getDealQuantity(), 1.9))),
                new StrategyEnum("10602", "比前一天放量_70_100", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.7), multiply(t0.getT1().getDealQuantity(), 2.0))),
                new StrategyEnum("10603", "比前一天放量_80_110", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.8), multiply(t0.getT1().getDealQuantity(), 2.1))),
                new StrategyEnum("10604", "比前一天放量_90_120", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.9), multiply(t0.getT1().getDealQuantity(), 2.2))),
                new StrategyEnum("10601", "比前一天放量_100_130", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.0), multiply(t0.getT1().getDealQuantity(), 2.3))),
                new StrategyEnum("10602", "比前一天放量_110_140", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.1), multiply(t0.getT1().getDealQuantity(), 2.4))),
                new StrategyEnum("10603", "比前一天放量_120_150", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.2), multiply(t0.getT1().getDealQuantity(), 2.5))),
                new StrategyEnum("10604", "比前一天放量_130_160", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.3), multiply(t0.getT1().getDealQuantity(), 2.6))),
                new StrategyEnum("10605", "比前一天放量大于160", "moreDeal", (Detail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.6)))

        );
    }
}
