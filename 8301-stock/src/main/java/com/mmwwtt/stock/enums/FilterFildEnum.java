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

    DATE50_RISE5_MAX_MIDDLE("50rise5MaxMiddle", "最大五日涨幅中位数",
            Detail::getRise5Max,
            StrategyL1::getRise5MaxMiddle,
            Strategy::getRise5MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.070) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.091) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.098) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.103) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.11) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.115) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.117) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.120) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.125)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.13)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.135)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.145)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            }
    ),

    RISE5_AVG("rise5Avg", "最大五日涨幅平均数",
            Detail::getRise5,
            StrategyL1::getRise5Avg,
            Strategy::getRise5Avg,
            (StrategyTmp tmp) -> {

                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.01) || tmp.getDateCnt() < 80))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.02) || tmp.getDateCnt() < 70))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.03) || tmp.getDateCnt() < 65))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.04) || tmp.getDateCnt() < 65))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.045) || tmp.getDateCnt() < 65))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.05) || tmp.getDateCnt() < 60))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.055) || tmp.getDateCnt() < 60))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.06) || tmp.getDateCnt() < 55))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.07)) || tmp.getDateCnt() < 50)
                        || (level == 11 && (lessThan(tmp.getPert(), 0.13)) || tmp.getDateCnt() < 45)
                        || (level == 12 && (lessThan(tmp.getPert(), 0.135)) || tmp.getDateCnt() < 40)
                        || (level == 13 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 35)
                        || (level == 14 && (lessThan(tmp.getPert(), 0.14)) || tmp.getDateCnt() < 30)
                        || (level == 15 && (lessThan(tmp.getPert(), 0.145)) || tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            }),

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

}