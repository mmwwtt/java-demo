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

    RISE3_MIDDLE_50_DAY("rise3Middle50Day", "3日涨幅中位数(50天)",
            13, 100,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 80, 0.007, 1.50),
                    new Cond(3, 77, 0.010, 1.50),
                    new Cond(4, 74, 0.015, 1.50),
                    new Cond(5, 71, 0.020, 1.50),
                    new Cond(6, 68, 0.025, 1.50),
                    new Cond(7, 65, 0.030, 1.50),
                    new Cond(8, 62, 0.035, 1.50),
                    new Cond(9, 59, 0.040, 1.50),
                    new Cond(10, 56, 0.043, 1.50),
                    new Cond(11, 53, 0.046, 1.50),
                    new Cond(12, 50, 0.049, 1.50),
                    new Cond(13, 50, 0.052, 1.50))
    ),

    RISE3_MIDDLE_30_DAY("rise3Middle30Day", "3日涨幅中位数(30天)",
            13, 200,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 60, 0.007, 1.50),
                    new Cond(3, 57, 0.010, 1.50),
                    new Cond(4, 54, 0.015, 1.50),
                    new Cond(5, 51, 0.020, 1.50),
                    new Cond(6, 48, 0.025, 1.50),
                    new Cond(7, 45, 0.030, 1.50),
                    new Cond(8, 42, 0.035, 1.50),
                    new Cond(9, 39, 0.040, 1.50),
                    new Cond(10, 36, 0.043, 1.50),
                    new Cond(11, 33, 0.046, 1.50),
                    new Cond(12, 30, 0.049, 1.50),
                    new Cond(13, 30, 0.052, 1.50))
    ),

    RISE3_MIDDLE_30_DAY_1("rise3Middle30Day", "3日涨幅中位数(30天)",
            13, 200,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 60, 0.008, 1.50),
                    new Cond(3, 57, 0.013, 1.50),
                    new Cond(4, 54, 0.018, 1.50),
                    new Cond(5, 51, 0.023, 1.50),
                    new Cond(6, 48, 0.028, 1.50),
                    new Cond(7, 45, 0.033, 1.50),
                    new Cond(8, 42, 0.038, 1.50),
                    new Cond(9, 39, 0.040, 1.50),
                    new Cond(10, 36, 0.043, 1.50),
                    new Cond(11, 33, 0.046, 1.50),
                    new Cond(12, 30, 0.049, 1.50),
                    new Cond(13, 30, 0.052, 1.50))
    ),

    RISE5_MIDDLE_30_DAY("rise5Middle30Day", "5日涨幅中位数(30天)",
            13, 200,
            Detail::getRise5,
            Detail::getRise5Min,
            StrategyL1::getRise5Middle,
            StrategyL1::getRise5MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 60, 0.015, 1.50),
                    new Cond(3, 57, 0.020, 1.50),
                    new Cond(4, 54, 0.025, 1.50),
                    new Cond(5, 51, 0.030, 1.50),
                    new Cond(6, 48, 0.035, 1.50),
                    new Cond(7, 45, 0.040, 1.50),
                    new Cond(8, 42, 0.045, 1.50),
                    new Cond(9, 39, 0.050, 1.50),
                    new Cond(10, 36, 0.053, 1.50),
                    new Cond(11, 33, 0.056, 1.50),
                    new Cond(12, 30, 0.059, 1.50),
                    new Cond(13, 30, 0.062, 1.50))
    ),


    TMP("tmp", "5日涨幅中位数(30天)",
            13, 200,
            Detail::getRise5,
            Detail::getRise5Min,
            StrategyL1::getRise5Middle,
            StrategyL1::getRise5MinMiddle,
            Arrays.asList(new Cond(), new Cond(),
                    new Cond(2, 60, 0.008, 1.50),
                    new Cond(3, 57, 0.015, 1.50),
                    new Cond(4, 54, 0.020, 1.50),
                    new Cond(5, 51, 0.025, 1.50),
                    new Cond(6, 48, 0.030, 1.50),
                    new Cond(7, 45, 0.035, 1.50),
                    new Cond(8, 42, 0.040, 1.50),
                    new Cond(9, 39, 0.045, 1.50),
                    new Cond(10, 36, 0.050, 1.50),
                    new Cond(11, 33, 0.055, 1.50),
                    new Cond(12, 30, 0.060, 1.50),
                    new Cond(13, 30, 0.062, 1.50))
    ),


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
        if (moreThan(tmp.getMiddle(), cond.getMiddleLimit() - 0.015)
                && tmp.getMiddle() + tmp.getMinMiddle() * cond.getMinMiddleLimit() > 0) {
            return true;
        }
//        if (tmp.getMiddle() + tmp.getMinMiddle() * cond.getMinMiddleLimit() > 0) {
//            return true;
//        }
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