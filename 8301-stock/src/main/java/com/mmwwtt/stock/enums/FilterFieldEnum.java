package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.common.BaseEnum;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;

@AllArgsConstructor
@Getter
public enum FilterFieldEnum implements BaseEnum {

//
//    RISE5_MAX_MIDDLE_50DAY("rise5MaxMiddle50Day", "最大5日涨幅中位数(50天)",
//            13, 30,
//            Detail::getRise5Max,
//            Detail::getRise5Min,
//            StrategyL1::getRise5MaxMiddle,
//            StrategyL1::getRise5MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 80, 0.065, 1.55),
//                    new Cond(3, 80, 0.080, 1.70),
//                    new Cond(4, 80, 0.090, 1.80),
//                    new Cond(5, 70, 0.095, 1.90),
//                    new Cond(6, 70, 0.100, 2.00),
//                    new Cond(7, 70, 0.105, 2.10),
//                    new Cond(8, 60, 0.110, 2.20),
//                    new Cond(9, 60, 0.115, 2.30),
//                    new Cond(10, 60, 0.120, 2.40),
//                    new Cond(11, 50, 0.125, 2.50),
//                    new Cond(12, 50, 0.130, 2.60),
//                    new Cond(13, 50, 0.135, 2.70))
//    ),
//    RISE3_MAX_MIDDLE_50_DAY("rise3MaxMiddle50Day", "最大3日涨幅中位数(50天)",
//            5, 70,
//            Detail::getRise3Max,
//            Detail::getRise3Min,
//            StrategyL1::getRise3MaxMiddle,
//            StrategyL1::getRise3MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 80, 0.025, 2.10),
//                    new Cond(3, 80, 0.035, 1.80),
//                    new Cond(4, 80, 0.040, 1.80),
//                    new Cond(5, 70, 0.080, 3.20),
//                    new Cond(6, 70, 0.090, 3.30),
//                    new Cond(7, 70, 0.100, 3.40),
//                    new Cond(8, 60, 0.110, 3.50),
//                    new Cond(9, 60, 0.115, 3.60),
//                    new Cond(10, 60, 0.120, 3.70),
//                    new Cond(11, 50, 0.125, 3.80),
//                    new Cond(12, 50, 0.130, 3.90),
//                    new Cond(13, 50, 0.135, 4.00))
//    ),
//
//    RISE3_MAX_MIDDLE_40_DAY("rise3MaxMiddle40Day", "最大3日涨幅中位数(40天)",
//            15, 70,
//            Detail::getRise3Max,
//            Detail::getRise3Min,
//            StrategyL1::getRise3MaxMiddle,
//            StrategyL1::getRise3MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 70, 0.045, 1.55),
//                    new Cond(3, 70, 0.060, 1.70),
//                    new Cond(4, 70, 0.070, 1.80),
//                    new Cond(5, 60, 0.080, 1.90),
//                    new Cond(6, 60, 0.090, 2.00),
//                    new Cond(7, 60, 0.100, 2.10),
//                    new Cond(8, 50, 0.110, 2.20),
//                    new Cond(9, 50, 0.115, 2.30),
//                    new Cond(10, 50, 0.120, 2.40),
//                    new Cond(11, 40, 0.125, 2.50),
//                    new Cond(12, 40, 0.130, 2.60),
//                    new Cond(13, 40, 0.135, 2.70))
//    ),
//    RISE3_MAX_MIDDLE_30_DAY("rise3MaxMiddle30Day", "最大3日涨幅中位数(30天)",
//            15, 70,
//            Detail::getRise3Max,
//            Detail::getRise3Min,
//            StrategyL1::getRise3MaxMiddle,
//            StrategyL1::getRise3MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 60, 0.045, 1.55),
//                    new Cond(3, 60, 0.060, 1.70),
//                    new Cond(4, 60, 0.070, 1.80),
//                    new Cond(5, 50, 0.090, 1.90),
//                    new Cond(6, 50, 0.090, 2.00),
//                    new Cond(7, 50, 0.100, 2.10),
//                    new Cond(8, 40, 0.110, 2.20),
//                    new Cond(9, 40, 0.115, 2.30),
//                    new Cond(10, 40, 0.120, 2.40),
//                    new Cond(11, 30, 0.125, 2.50),
//                    new Cond(12, 30, 0.130, 2.60),
//                    new Cond(13, 30, 0.135, 2.70))
//    ),
//



    RISE3_MIDDLE_50_DAY("rise3Middle50Day", "3日涨幅中位数(50天)",
            13, 100,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 80, 0.007, 0.9),
                    new Cond(3, 80, 0.010, 0.90),
                    new Cond(4, 80, 0.015, 0.90),
                    new Cond(5, 70, 0.020, 0.90),
                    new Cond(6, 70, 0.035, 0.90),
                    new Cond(7, 70, 0.030, 1.00),
                    new Cond(8, 60, 0.035, 1.10),
                    new Cond(9, 60, 0.040, 1.20),
                    new Cond(10, 60, 0.045, 1.30),
                    new Cond(11, 50, 0.050, 1.40),
                    new Cond(12, 50, 0.055, 1.50),
                    new Cond(13, 50, 0.060, 1.60))
    ),


    RISE3_MIDDLE_50_DAY_TMP("rise3Middle50Day", "3日涨幅中位数(50天)",
            13, 100,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 60, 0.007, 0.9),
                    new Cond(3, 60, 0.010, 0.90),
                    new Cond(4, 60, 0.015, 0.90),
                    new Cond(5, 50, 0.020, 0.90),
                    new Cond(6, 50, 0.035, 0.90),
                    new Cond(7, 50, 0.030, 1.00),
                    new Cond(8, 40, 0.035, 1.10),
                    new Cond(9, 40, 0.040, 1.20),
                    new Cond(10, 40, 0.045, 1.30),
                    new Cond(11, 30, 0.050, 1.40),
                    new Cond(12, 30, 0.055, 1.50),
                    new Cond(13, 30, 0.060, 1.60))
    ),

//
//    RISE3_MIDDLE_40_DAY("rise3Middle40Day", "3日涨幅中位数(40天)",
//            13, 70,
//            Detail::getRise3,
//            Detail::getRise3Min,
//            StrategyL1::getRise3Middle,
//            StrategyL1::getRise3MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 70, 0.007, 1.60),
//                    new Cond(3, 70, 0.010, 1.70),
//                    new Cond(4, 70, 0.015, 1.80),
//                    new Cond(5, 60, 0.020, 1.90),
//                    new Cond(6, 60, 0.025, 2.00),
//                    new Cond(7, 60, 0.030, 2.10),
//                    new Cond(8, 50, 0.035, 2.20),
//                    new Cond(9, 50, 0.040, 2.30),
//                    new Cond(10, 50, 0.045, 2.40),
//                    new Cond(11, 40, 0.050, 2.50),
//                    new Cond(12, 40, 0.055, 2.60),
//                    new Cond(13, 40, 0.060, 2.70))
//    ),
//
//
//    RISE3_MIDDLE_30_DAY("rise3Middle30Day", "3日涨幅中位数(30天)",
//            13, 100,
//            Detail::getRise3,
//            Detail::getRise3Min,
//            StrategyL1::getRise3Middle,
//            StrategyL1::getRise3MinMiddle,
//            Arrays.asList(new Cond(), new Cond(),
//                    new Cond(2, 70, 0.007, 1.60),
//                    new Cond(3, 70, 0.010, 1.70),
//                    new Cond(4, 70, 0.015, 1.80),
//                    new Cond(5, 60, 0.020, 1.90),
//                    new Cond(6, 60, 0.025, 2.00),
//                    new Cond(7, 60, 0.030, 2.10),
//                    new Cond(8, 50, 0.035, 2.20),
//                    new Cond(9, 50, 0.040, 2.30),
//                    new Cond(10, 50, 0.045, 2.40),
//                    new Cond(11, 40, 0.050, 2.50),
//                    new Cond(12, 40, 0.055, 2.60),
//                    new Cond(13, 40, 0.060, 2.70))
//    ),


    ;
    private final String code;
    private final String desc;
    private final int levelLimit;
    private final int topLimit;
    private final Function<Detail, Double> detailMiddleGetter;
    private final Function<Detail, Double> detailMinMiddleGetter;
    private final Function<StrategyL1, Double> l1MiddleGetter;
    private final Function<StrategyL1, Double> l1MinMiddleGetter;
    /**
     * 用于策略过滤的条件
     */
    private final List<Cond> conds;


    /**
     * 策略过滤，是否符合 true则保留， false则抛弃
     * 日期过滤
     */
    public boolean checkDateCnt(StrategyTmp tmp) {
        int level = tmp.getStrategyCodeSet().size();
        Cond cond = conds.get(level);
        return tmp.getDateCnt() >= cond.getDateCntLimit();
    }

    /**
     * 策略过滤，是否符合 true则保留， false则抛弃
     * 阈值过滤
     */
    public boolean checkLimit(StrategyTmp tmp) {
        int level = tmp.getStrategyCodeSet().size();
        Cond cond = conds.get(level);
        if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001)) || tmp.getMinMiddle() < -0.1) {
            return false;
        }
        if (moreThan(tmp.getMiddle(), cond.getMiddleLimit())) {
            return true;
        }
//        if (moreThan(tmp.getMiddle(), cond.getMiddleLimit() - 0.015)
//                && tmp.getMiddle() + tmp.getMinMiddle() * cond.getMinMiddleLimit() > 0) {
//            return true;
//        }
        if (tmp.getMiddle() + tmp.getMinMiddle() * cond.getMinMiddleLimit() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 条件阈值
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Cond {
        private Integer level;
        private Integer DateCntLimit;
        private Double middleLimit;
        private Double minMiddleLimit;
    }
}