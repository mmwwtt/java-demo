package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.entity.Detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;

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
                new StrategyEnum("30004", "二连红",
                        (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyEnum("30005", "三连红", (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyEnum("30006", "是十字星", Detail::getIsTenStar),
                new StrategyEnum("30007", "多头排列_5日线_大于10_大于20", (Detail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyEnum("30010", "区间5向上", Detail::getFiveIsUp),
                new StrategyEnum("30011", "区间10向上", Detail::getTenIsUp),
                new StrategyEnum("30012", "区间20向上", Detail::getTwentyIsUp),
                new StrategyEnum("30013", "区间40向上", Detail::getFortyIsUp),
                new StrategyEnum("30014", "区间60向上", Detail::getSixtyIsUp),

                new StrategyEnum("30015", "区间5向下", (Detail t0) -> !t0.getFiveIsUp()),
                new StrategyEnum("30016", "区间10向下", (Detail t0) -> !t0.getTenIsUp()),
                new StrategyEnum("30017", "区间20向下", (Detail t0) -> !t0.getTwentyIsUp()),
                new StrategyEnum("30018", "区间40向下", (Detail t0) -> !t0.getFortyIsUp()),
                new StrategyEnum("30019", "区间60向下", (Detail t0) -> !t0.getSixtyIsUp()),


                new StrategyEnum("30001", "dif<-0.3676", "dif", (Detail t0) -> lessThan(t0.getDif(), -0.3676)),
                new StrategyEnum("30002", "-0.3676<dif<-0.1765", "dif", (Detail t0) -> isInRange(t0.getDif(), -0.3676, -0.1765)),
                new StrategyEnum("30003", "-0.1765<dif<-0.0950", "dif", (Detail t0) -> isInRange(t0.getDif(), -0.1765, -0.0950)),
                new StrategyEnum("30004", "-0.0950<dif<-0.0477", "dif", (Detail t0) -> isInRange(t0.getDif(), -0.0950, -0.0477)),
                new StrategyEnum("30005", "-0.0477<dif<-0.0150", "dif", (Detail t0) -> isInRange(t0.getDif(), -0.0477, -0.0150)),
                new StrategyEnum("30006", "-0.0150<dif<0.0127", "dif", (Detail t0) -> isInRange(t0.getDif(), -0.0150, 0.0127)),
                new StrategyEnum("30007", "0.0127<dif<0.0430", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.0127, 0.0430)),
                new StrategyEnum("30008", "0.0430<dif<0.0794", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.0430, 0.0794)),
                new StrategyEnum("30009", "0.0794<dif<0.1275", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.0794, 0.1275)),
                new StrategyEnum("30010", "0.1275<dif<0.1940", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.1275, 0.1940)),
                new StrategyEnum("30011", "0.1940<dif<0.2928", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.1940, 0.2928)),
                new StrategyEnum("30012", "0.2928<dif<0.4497", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.2928, 0.4497)),
                new StrategyEnum("30013", "0.4497<dif<0.7047", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.4497, 0.7047)),
                new StrategyEnum("30014", "0.7047<dif<1.1793", "dif", (Detail t0) -> isInRange(t0.getDif(), 0.7047, 1.1793)),
                new StrategyEnum("30015", "1.1793<dif<2.2348", "dif", (Detail t0) -> isInRange(t0.getDif(), 1.1793, 2.2348)),
                new StrategyEnum("30016", "2.2348<dif", "dif", (Detail t0) -> moreThan(t0.getDif(), 2.2348)),


                new StrategyEnum("30101", "dea<-0.3210", "dea", (Detail t0) -> lessThan(t0.getDea(), -0.3210)),
                new StrategyEnum("30102", "-0.3210<dea<-0.1499", "dea", (Detail t0) -> isInRange(t0.getDea(), -0.3210, -0.1499)),
                new StrategyEnum("30103", "-0.1499<dea<-0.0784", "dea", (Detail t0) -> isInRange(t0.getDea(), -0.1499, -0.0784)),
                new StrategyEnum("30104", "-0.0784<dea<-0.0365", "dea", (Detail t0) -> isInRange(t0.getDea(), -0.0784, -0.0365)),
                new StrategyEnum("30105", "-0.0365<dea<-0.0070", "dea", (Detail t0) -> isInRange(t0.getDea(), -0.0365, -0.0070)),
                new StrategyEnum("30106", "-0.0070<dea<0.0193", "dea", (Detail t0) -> isInRange(t0.getDea(), -0.0070, 0.0193)),
                new StrategyEnum("30107", "0.0193<dea<0.0488", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.0193, 0.0488)),
                new StrategyEnum("30108", "0.0488<dea<0.0853", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.0488, 0.0853)),
                new StrategyEnum("30109", "0.0853<dea<0.1338", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.0853, 0.1338)),
                new StrategyEnum("30110", "0.1338<dea<0.2017", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.1338, 0.2017)),
                new StrategyEnum("30111", "0.2017<dea<0.3029", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.2017, 0.3029)),
                new StrategyEnum("30112", "0.3029<dea<0.4616", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.3029, 0.4616)),
                new StrategyEnum("30113", "0.4616<dea<0.7156", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.4616, 0.7156)),
                new StrategyEnum("30114", "0.7156<dea<1.1766", "dea", (Detail t0) -> isInRange(t0.getDea(), 0.7156, 1.1766)),
                new StrategyEnum("30115", "1.1766<dea<2.1718", "dea", (Detail t0) -> isInRange(t0.getDea(), 1.1766, 2.1718)),
                new StrategyEnum("30116", "2.1718<dea", "dea", (Detail t0) -> moreThan(t0.getDea(), 2.1718)),

                new StrategyEnum("30201", "macd<-0.7436", "macd", (Detail t0) -> lessThan(t0.getMacd(), -0.7436)),
                new StrategyEnum("30202", "-0.7436<macd<-0.4107", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.7436, -0.4107)),
                new StrategyEnum("30203", "-0.4107<macd<-0.2592", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.4107, -0.2592)),
                new StrategyEnum("30204", "-0.2592<macd<-0.1683", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.2592, -0.1683)),
                new StrategyEnum("30205", "-0.1683<macd<-0.1087", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.1683, -0.1087)),
                new StrategyEnum("30206", "-0.1087<macd<-0.0673", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.1087, -0.0673)),
                new StrategyEnum("30207", "-0.0673<macd<-0.0375", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.0673, -0.0375)),
                new StrategyEnum("30208", "-0.0375<macd<-0.0145", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.0375, -0.0145)),
                new StrategyEnum("30209", "-0.0145<macd<0.0043", "macd", (Detail t0) -> isInRange(t0.getMacd(), -0.0145, 0.0043)),
                new StrategyEnum("30210", "0.0043<macd<0.0234", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.0043, 0.0234)),
                new StrategyEnum("30211", "0.0234<macd<0.0476", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.0234, 0.0476)),
                new StrategyEnum("30212", "0.0476<macd<0.0830", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.0476, 0.0830)),
                new StrategyEnum("30213", "0.0830<macd<0.1426", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.0830, 0.1426)),
                new StrategyEnum("30214", "0.1426<macd<0.2683", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.1426, 0.2683)),
                new StrategyEnum("30215", "0.2683<macd<0.6589", "macd", (Detail t0) -> isInRange(t0.getMacd(), 0.2683, 0.6589)),
                new StrategyEnum("30216", "0.6589<macd", "macd", (Detail t0) -> moreThan(t0.getMacd(), 0.6589)),




                //macd相关
                //DIF 快线
                //DEA 慢线
                new StrategyEnum("20020", "DIF线上穿DEA线_金叉", (Detail t0) -> {
                    Detail t1 = t0.getT1();
                    Detail t2 = t0.getT2();
                    Detail t3 = t0.getT3();
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),

                new StrategyEnum("20040", "WR威廉指标_上穿负80_脱离超卖区", (Detail t0) -> lessThan(t0.getWr(), -80.0)
                        && moreThan(t0.getT1().getWr(), -80.0)
                        && moreThan(t0.getT2().getWr(), -80.0)),
                new StrategyEnum("20041", "WR威廉指标_负80以下_在超卖区", (Detail t0) -> lessThan(t0.getWr(), -80.0)),

                new StrategyEnum("20100", "上穿过5日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFiveDayLine())),
                new StrategyEnum("20101", "上穿过10日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTenDayLine())),
                new StrategyEnum("20102", "上穿过20日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("20103", "上穿过40日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getFortyDayLine())),
                new StrategyEnum("20104", "上穿过60日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getT1().getHighPrice(), t0.getSixtyDayLine())),

                new StrategyEnum("20105", "下穿过5日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getFiveDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFiveDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFiveDayLine())),
                new StrategyEnum("20106", "下穿过10日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getTenDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTenDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTenDayLine())),
                new StrategyEnum("20107", "下穿过20日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getTwentyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getTwentyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getTwentyDayLine())),
                new StrategyEnum("20108", "下穿过40日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getFortyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getFortyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getFortyDayLine())),
                new StrategyEnum("20109", "下穿过60日线", (Detail t0) -> moreThan(t0.getHighPrice(), t0.getSixtyDayLine())
                        && lessThan(t0.getLowPrice(), t0.getSixtyDayLine())
                        && moreThan(t0.getT1().getLowPrice(), t0.getSixtyDayLine())),


                new StrategyEnum("21002", "下影线占比10_40", "dowShadowPert", (Detail t0) -> isInRange(t0.getDownShadowPert(), 0.1, 0.4)),
                new StrategyEnum("21004", "下影线占比40_70", "dowShadowPert", (Detail t0) -> isInRange(t0.getDownShadowPert(), 0.4, 0.7)),
                new StrategyEnum("21006", "下影线占比70_100", "dowShadowPert", (Detail t0) -> isInRange(t0.getDownShadowPert(), 0.7, 1.0)),

                new StrategyEnum("21022", "上影线占比10_40", "upShadowPert", (Detail t0) -> isInRange(t0.getUpShadowPert(), 0.1, 0.4)),
                new StrategyEnum("21024", "上影线占比40_70", "upShadowPert", (Detail t0) -> isInRange(t0.getUpShadowPert(), 0.4, 0.7)),
                new StrategyEnum("21028", "上影线占比70_100", "upShadowPert", (Detail t0) -> isInRange(t0.getUpShadowPert(), 0.7, 1.0)),

                new StrategyEnum("21031", "上影线长度_00_03", "upshadowLen", (Detail t0) -> isInRange(t0.getUpShadowLen(), 0.00, 0.03)),
                new StrategyEnum("21032", "上影线长度_03_06", "upshadowLen", (Detail t0) -> isInRange(t0.getUpShadowLen(), 0.03, 0.06)),
                new StrategyEnum("21034", "上影线长度_06_10", "upshadowLen", (Detail t0) -> isInRange(t0.getUpShadowLen(), 0.06, 0.10)),

                new StrategyEnum("21040", "总长度_00_03", "allLen", (Detail t0) -> isInRange(t0.getAllLen(), 0.0, 0.03)),
                new StrategyEnum("21042", "总长度_03_06", "allLen", (Detail t0) -> isInRange(t0.getAllLen(), 0.03, 0.06)),
                new StrategyEnum("21044", "总长度_06_09", "allLen", (Detail t0) -> isInRange(t0.getAllLen(), 0.06, 0.09)),
                new StrategyEnum("21046", "总长度_09_12", "allLen", (Detail t0) -> isInRange(t0.getAllLen(), 0.09, 0.12)),
                new StrategyEnum("21048", "总长度_12_16", "allLen", (Detail t0) -> isInRange(t0.getAllLen(), 0.12, 0.16)),


                new StrategyEnum("21060", "区间60_00_30", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.0, 0.3)),
                new StrategyEnum("21062", "区间60_30_60", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.3, 0.6)),
                new StrategyEnum("21064", "区间60_60_90", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.6, 0.9)),
                new StrategyEnum("21066", "区间60_90_100", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.9, 1.0)),

                new StrategyEnum("21070", "区间40_00_30", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.0, 0.3)),
                new StrategyEnum("21072", "区间40_30_60", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.3, 0.6)),
                new StrategyEnum("21074", "区间40_60_90", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.6, 0.9)),
                new StrategyEnum("21076", "区间40_90_100", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.9, 1.0)),

                new StrategyEnum("21080", "区间20_00_30", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.0, 0.3)),
                new StrategyEnum("21082", "区间20_30_60", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.3, 0.6)),
                new StrategyEnum("21084", "区间20_60_90", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.6, 0.9)),
                new StrategyEnum("21086", "区间20_90_100", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.9, 1.0)),


                new StrategyEnum("21090", "上升缺口_00_02", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.00, 0.02);
                }),

                new StrategyEnum("21092", "上升缺口_02_04", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.02, 0.04);
                }),
                new StrategyEnum("21094", "上升缺口大于_04", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, 0.04);
                }),

                new StrategyEnum("21100", "下影线长度_00_03", "downShowLen", (Detail t0) -> isInRange(t0.getDownShadowLen(), 0.0, 0.03)),
                new StrategyEnum("21102", "下影线长度_03_06", "downShowLen", (Detail t0) -> isInRange(t0.getDownShadowLen(), 0.03, 0.06)),
                new StrategyEnum("21104", "下影线长度_06_12", "downShowLen", (Detail t0) -> isInRange(t0.getDownShadowLen(), 0.06, 0.12)),
                new StrategyEnum("21107", "下影线长度大于_12", "downShowLen", (Detail t0) -> moreThan(t0.getDownShadowLen(), 0.12)),


                new StrategyEnum("21120", "比前一天缩量_00_30", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(), 0.0,
                        multiply(t0.getT1().getDealQuantity(), 0.3))),
                new StrategyEnum("21122", "比前一天缩量_30_60", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.3), multiply(t0.getT1().getDealQuantity(), 0.6))),
                new StrategyEnum("21124", "比前一天缩量_60_90", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.6), multiply(t0.getT1().getDealQuantity(), 0.9))),


                new StrategyEnum("21130", "比前一天放量_00_30", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1), multiply(t0.getT1().getDealQuantity(), 1.3))),
                new StrategyEnum("21132", "比前一天放量_30_60", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.3), multiply(t0.getT1().getDealQuantity(), 1.6))),
                new StrategyEnum("21134", "比前一天放量_60_90", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.6), multiply(t0.getT1().getDealQuantity(), 1.9))),
                new StrategyEnum("21136", "比前一天放量_90_120", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.9), multiply(t0.getT1().getDealQuantity(), 2.2))),
                new StrategyEnum("21138", "比前一天放量_120_150", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.2), multiply(t0.getT1().getDealQuantity(), 2.5))),
                new StrategyEnum("21148", "比前一天放量_150", "moreDeal", (Detail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.5))),

                new StrategyEnum("21150", "区间10_00_30", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.0, 0.3)),
                new StrategyEnum("21152", "区间10_30_60", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.3, 0.6)),
                new StrategyEnum("21154", "区间10_60_90", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.6, 0.9)),
                new StrategyEnum("21156", "区间10_90_100", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.9, 1.0)),

                // RSI相对强弱
                new StrategyEnum("21200", "RSI_超卖_小于30", (Detail t0) -> t0.getRsi() != null && lessThan(t0.getRsi(), 30.0)),
                new StrategyEnum("21201", "RSI_30_50", (Detail t0) -> t0.getRsi() != null && isInRange(t0.getRsi(), 30.0, 50.0)),
                new StrategyEnum("21202", "RSI_50_70", (Detail t0) -> t0.getRsi() != null && isInRange(t0.getRsi(), 50.0, 70.0)),
                new StrategyEnum("21203", "RSI_超买_大于70", (Detail t0) -> t0.getRsi() != null && moreThan(t0.getRsi(), 70.0)),

                // 乖离率BIAS
                new StrategyEnum("21210", "BIAS5_负偏离_小于负5", "bias5", (Detail t0) -> t0.getBias5() != null && lessThan(t0.getBias5(), -5.0)),
                new StrategyEnum("21211", "BIAS5_负5_0", "bias5", (Detail t0) -> t0.getBias5() != null && isInRange(t0.getBias5(), -5.0, 0.0)),
                new StrategyEnum("21212", "BIAS5_0_5", "bias5", (Detail t0) -> t0.getBias5() != null && isInRange(t0.getBias5(), 0.0, 5.0)),
                new StrategyEnum("21213", "BIAS5_正偏离_大于5", "bias5", (Detail t0) -> t0.getBias5() != null && moreThan(t0.getBias5(), 5.0)),
                new StrategyEnum("21214", "BIAS20_负偏离_小于负8", "bias20", (Detail t0) -> t0.getBias20() != null && lessThan(t0.getBias20(), -8.0)),
                new StrategyEnum("21215", "BIAS20_负8_0", "bias20", (Detail t0) -> t0.getBias20() != null && isInRange(t0.getBias20(), -8.0, 0.0)),
                new StrategyEnum("21216", "BIAS20_0_8", "bias20", (Detail t0) -> t0.getBias20() != null && isInRange(t0.getBias20(), 0.0, 8.0)),
                new StrategyEnum("21217", "BIAS20_正偏离_大于8", "bias20", (Detail t0) -> t0.getBias20() != null && moreThan(t0.getBias20(), 8.0)),

                // 均线排列强度
                new StrategyEnum("21220", "均线多头排列_1", "maAlignBullScore", (Detail t0) -> Objects.equals(1, t0.getMaAlignBullScore())),
                new StrategyEnum("21221", "均线多头排列_2", "maAlignBullScore", (Detail t0) -> Objects.equals(2, t0.getMaAlignBullScore())),
                new StrategyEnum("21222", "均线空头排列_3", "maAlignBullScore", (Detail t0) -> Objects.equals(3, t0.getMaAlignBullScore())),
                new StrategyEnum("21223", "均线空头排列_4", "maAlignBullScore", (Detail t0) -> Objects.equals(4, t0.getMaAlignBullScore())),

                // 布林带位置
                new StrategyEnum("21230", "布林带_跌破下轨_小于0", "boll", (Detail t0) -> t0.getBollPosition() != null && lessThan(t0.getBollPosition(), 0.0)),
                new StrategyEnum("21231", "布林带_下轨附近_0_0.2", "boll", (Detail t0) -> t0.getBollPosition() != null && isInRange(t0.getBollPosition(), 0.0, 0.2)),
                new StrategyEnum("21232", "布林带_中轨附近_0.4_0.6", "boll", (Detail t0) -> t0.getBollPosition() != null && isInRange(t0.getBollPosition(), 0.4, 0.6)),
                new StrategyEnum("21233", "布林带_上轨附近_0.8_1", "boll", (Detail t0) -> t0.getBollPosition() != null && isInRange(t0.getBollPosition(), 0.8, 1.0)),
                new StrategyEnum("21234", "布林带_突破上轨_大于1", "boll", (Detail t0) -> t0.getBollPosition() != null && moreThan(t0.getBollPosition(), 1.0)),

                // 20日均线斜率
                new StrategyEnum("21240", "均线20斜率_向下_小于负0.01", "ma20Slope", (Detail t0) -> t0.getMa20Slope() != null && lessThan(t0.getMa20Slope(), -0.01)),
                new StrategyEnum("21241", "均线20斜率_走平_负0.01_0.01", "ma20Slope", (Detail t0) -> t0.getMa20Slope() != null && isInRange(t0.getMa20Slope(), -0.01, 0.01)),
                new StrategyEnum("21242", "均线20斜率_向上_大于0.01", "ma20Slope", (Detail t0) -> t0.getMa20Slope() != null && moreThan(t0.getMa20Slope(), 0.01)),

                // 20日波动率
                new StrategyEnum("21250", "波动率20_低_小于0.02", "volatility", (Detail t0) -> t0.getVolatility20() != null && lessThan(t0.getVolatility20(), 0.02)),
                new StrategyEnum("21251", "波动率20_中_0.02_0.04", "volatility", (Detail t0) -> t0.getVolatility20() != null && isInRange(t0.getVolatility20(), 0.02, 0.04)),
                new StrategyEnum("21252", "波动率20_高_大于0.04", "volatility", (Detail t0) -> t0.getVolatility20() != null && moreThan(t0.getVolatility20(), 0.04)),

                // 量价背离
                new StrategyEnum("21260", "量价背离_价涨量缩", (Detail t0) -> Integer.valueOf(1).equals(t0.getVolumePriceDivergence())),
                new StrategyEnum("21261", "量价背离_价跌量增", (Detail t0) -> Integer.valueOf(-1).equals(t0.getVolumePriceDivergence())),

                // ATR波动率(需结合lastPrice判断相对波动)
                new StrategyEnum("21270", "ATR14_低波动_小于1pct", "atr", (Detail t0) -> t0.getAtr14() != null && t0.getLastPrice() != null
                        && lessThan(divide(t0.getAtr14(), t0.getLastPrice()), 0.01)),
                new StrategyEnum("21271", "ATR14_中波动_1_3pct", "atr", (Detail t0) -> t0.getAtr14() != null && t0.getLastPrice() != null
                        && isInRange(divide(t0.getAtr14(), t0.getLastPrice()), 0.01, 0.03)),
                new StrategyEnum("21272", "ATR14_高波动_大于3pct", "atr", (Detail t0) -> t0.getAtr14() != null && t0.getLastPrice() != null
                        && moreThan(divide(t0.getAtr14(), t0.getLastPrice()), 0.03))
        );
    }
}
