package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.common.BaseEnum;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.lessThan;
import static com.mmwwtt.stock.common.CommonUtils.multiply;

@AllArgsConstructor
@Getter
public enum FilterFildEnum implements BaseEnum {

    RISE1_MAX_MIDDLE("rise3MaxMiddle", "最大3日涨幅中位数",
            Detail::getRise1,
            StrategyL1::getRise1Middle,
            Strategy::getRise1Middle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.0) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.0)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },
            "pert > 0.06"
    ),

    RISE3_MAX_MIDDLE("rise3MaxMiddle", "最大3日涨幅中位数",
            Detail::getRise3Max,
            StrategyL1::getRise3MaxMiddle,
            Strategy::getRise3MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.05) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.06) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.07) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.08) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.09) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.10) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.105) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.11) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.115)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.12)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.125)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.13)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.135)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },
            "pert > 0.098"
    ),


    RISE5_MAX_MIDDLE("rise5MaxMiddle", "最大五日涨幅中位数",
            Detail::getRise5Max,
            StrategyL1::getRise5MaxMiddle,
            Strategy::getRise5MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.06) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.077) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.087) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.098) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.105) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.115) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.12) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.125) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.13)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.135)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.145)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.15)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.155)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },
            "pert > 0.155"
    ),


    RISE10_MAX_MIDDLE("rise10MaxMiddle", "最大10日涨幅中位数",
            Detail::getRise10Max,
            StrategyL1::getRise10MaxMiddle,
            Strategy::getRise10MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.095) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.12) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.135) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.145) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.155) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.165) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.17) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.18) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.19)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.20)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.21)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.22)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.23)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.24)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },
            "pert > 0.20"
    ),


    ;
    private final String code;
    private final String desc;

    /**
     * getter
     */
    private final Function<Detail, Double> detailGetter;
    private final Function<StrategyL1, Double> strategyL1Getter;
    private final Function<Strategy, Double> strategyGetter;

    /**
     * 策略过滤，是否符合 ture则保留， false则抛弃
     */
    private final Function<StrategyTmp, Boolean> isConformity;

    private final String dfsAfterSql;

}