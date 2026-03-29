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

    RISE5_MAX_MIDDLE_03("rise5MaxMiddle03", "最大五日涨幅中位数 l1域值0.03",
            Detail::getRise5Max,
            StrategyL1::getRise5MaxMiddle,
            Strategy::getRise5MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.001))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.07) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.055))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.075) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.05))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.08) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.09) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.10) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.11) || tmp.getDateCnt() < 50 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.12) || tmp.getDateCnt() < 50 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.125) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.13) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 11 && (lessThan(tmp.getPert(), 0.135) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 12 && (lessThan(tmp.getPert(), 0.14) || tmp.getDateCnt() < 40 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 13 && (lessThan(tmp.getPert(), 0.145) || tmp.getDateCnt() < 40 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 14 && (lessThan(tmp.getPert(), 0.15) || tmp.getDateCnt() < 40 || tmp.getRise5MinMiddle() < -0.035))
                        || (level == 15 && (lessThan(tmp.getPert(), 0.155) || tmp.getDateCnt() < 40 || tmp.getRise5MinMiddle() < -0.035))) {
                    return false;
                }
                return true;
            },
            "pert > 0.17"
    ),
    RISE5_MAX_MIDDLE_025("rise5MaxMiddle025", "最大五日涨幅中位数 l1域值0.025",
            Detail::getRise5Max,
            StrategyL1::getRise5MaxMiddle,
            Strategy::getRise5MaxMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.001))
                        || (level == 2 && (lessThan(tmp.getPert(), 0.06) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.065))
                        || (level == 3 && (lessThan(tmp.getPert(), 0.07) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.055))
                        || (level == 4 && (lessThan(tmp.getPert(), 0.08) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.05))
                        || (level == 5 && (lessThan(tmp.getPert(), 0.09) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.05))
                        || (level == 6 && (lessThan(tmp.getPert(), 0.10) || tmp.getDateCnt() < 55 || tmp.getRise5MinMiddle() < -0.05))
                        || (level == 7 && (lessThan(tmp.getPert(), 0.11) || tmp.getDateCnt() < 50 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 8 && (lessThan(tmp.getPert(), 0.12) || tmp.getDateCnt() < 50 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 9 && (lessThan(tmp.getPert(), 0.125) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 10 && (lessThan(tmp.getPert(), 0.13) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 11 && (lessThan(tmp.getPert(), 0.135) || tmp.getDateCnt() < 45 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 12 && (lessThan(tmp.getPert(), 0.14) || tmp.getDateCnt() < 40 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 13 && (lessThan(tmp.getPert(), 0.145) || tmp.getDateCnt() < 35 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 14 && (lessThan(tmp.getPert(), 0.15) || tmp.getDateCnt() < 30 || tmp.getRise5MinMiddle() < -0.04))
                        || (level == 15 && (lessThan(tmp.getPert(), 0.155) || tmp.getDateCnt() < 30 || tmp.getRise5MinMiddle() < -0.04))) {
                    return false;
                }
                return true;
            },
            "pert > 0.17"
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