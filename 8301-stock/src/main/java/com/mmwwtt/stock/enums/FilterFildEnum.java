package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.common.BaseEnum;
import com.mmwwtt.stock.entity.Detail;
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

    RISE5_MAX_MIDDLE_025("rise5MaxMiddle025", "最大五日涨幅中位数 l1域值0.025",
            10,
            Detail::getRise5Max,
            Detail::getRise5Min,
            StrategyL1::getRise5MaxMiddle,
            StrategyL1::getRise5MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMaxMiddle(), multiply(tmp.getParentMaxMiddle(), 1.001))
                        || (level == 2 && (lessThan(tmp.getMaxMiddle(), 0.040) || tmp.getDateCnt() < 60 || tmp.getMinMiddle() < -0.040))
                        || (level == 3 && (lessThan(tmp.getMaxMiddle(), 0.055) || tmp.getDateCnt() < 60 || tmp.getMinMiddle() < -0.038))
                        || (level == 4 && (lessThan(tmp.getMaxMiddle(), 0.065) || tmp.getDateCnt() < 60 || tmp.getMinMiddle() < -0.038))
                        || (level == 5 && (lessThan(tmp.getMaxMiddle(), 0.075) || tmp.getDateCnt() < 55 || tmp.getMinMiddle() < -0.038))
                        || (level == 6 && (lessThan(tmp.getMaxMiddle(), 0.085) || tmp.getDateCnt() < 55 || tmp.getMinMiddle() < -0.038))
                        || (level == 7 && (lessThan(tmp.getMaxMiddle(), 0.09) || tmp.getDateCnt() < 55 || tmp.getMinMiddle() < -0.038))
                        || (level == 8 && (lessThan(tmp.getMaxMiddle(), 0.095) || tmp.getDateCnt() < 50 || tmp.getMinMiddle() < -0.038))
                        || (level == 9 && (lessThan(tmp.getMaxMiddle(), 0.10) || tmp.getDateCnt() < 50 || tmp.getMinMiddle() < -0.038))
                        || (level == 10 && (lessThan(tmp.getMaxMiddle(), 0.105) || tmp.getDateCnt() < 50 || tmp.getMinMiddle() < -0.038))
                ) {
                    return false;
                }
                return true;
            }
    ),
    RISE5_MAX_MIDDLE_0251("rise5MaxMiddle025", "最大五日涨幅中位数 l1域值0.025",
            10,
            Detail::getRise5Max,
            Detail::getRise5Min,
            StrategyL1::getRise5MaxMiddle,
            StrategyL1::getRise5MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMaxMiddle(), multiply(tmp.getParentMaxMiddle(), 1.001))) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (tmp.getDetailCnt() < 60 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.070) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.55 < 0)) {
                            return false;
                        }
                        break;
                    case 3:
                        if (tmp.getDetailCnt() < 60 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.09) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.7 < 0)) {
                            return false;
                        }
                        break;
                    case 4:
                        if (tmp.getDetailCnt() < 60 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.10) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.0 < 0)) {
                            return false;
                        }
                        break;
                    case 5:
                        if (tmp.getDetailCnt() < 60 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.11) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.4 < 0)) {
                            return false;
                        }
                        break;
                    case 6:
                        if (tmp.getDetailCnt() < 55 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.115) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.5 < 0)) {
                            return false;
                        }
                        break;
                    case 7:
                        if (tmp.getDetailCnt() < 55 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.12) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.6 < 0)) {
                            return false;
                        }
                        break;
                    case 8:
                        if (tmp.getDetailCnt() < 55 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.125) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.7 < 0)) {
                            return false;
                        }
                        break;
                    case 9:
                        if (tmp.getDetailCnt() < 55 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.13) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.8 < 0)) {
                            return false;
                        }
                        break;
                    case 10:
                        if (tmp.getDetailCnt() < 50 || tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.14) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 3.0 < 0)) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
    ),
    ;
    private final String code;
    private final String desc;
    private final int levelLimit;
    private final Function<Detail, Double> detailMaxGetter;
    private final Function<Detail, Double> detailMinGetter;
    private final Function<StrategyL1, Double> l1MaxGetter;
    private final Function<StrategyL1, Double> l1MinGetter;
    /**
     * 策略过滤，是否符合 ture则保留， false则抛弃
     */
    private final Function<StrategyTmp, Boolean> isConformity;

}