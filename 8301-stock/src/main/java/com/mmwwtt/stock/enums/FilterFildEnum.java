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


    RISE5_MAX_MIDDLE_025("rise5MaxMiddle025", "最大5日涨幅中位数 l1域值0.025",
            13,
            Detail::getRise5Max,
            Detail::getRise5Min,
            StrategyL1::getRise5MaxMiddle,
            StrategyL1::getRise5MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 80)
                        || (level == 3 && tmp.getDateCnt() < 80)
                        || (level == 4 && tmp.getDateCnt() < 80)
                        || (level == 5 && tmp.getDateCnt() < 70)
                        || (level == 6 && tmp.getDateCnt() < 70)
                        || (level == 7 && tmp.getDateCnt() < 70)
                        || (level == 8 && tmp.getDateCnt() < 60)
                        || (level == 9 && tmp.getDateCnt() < 60)
                        || (level == 10 && tmp.getDateCnt() < 60)
                        || (level == 11 && tmp.getDateCnt() < 50)
                        || (level == 12 && tmp.getDateCnt() < 50)
                        || (level == 13 && tmp.getDateCnt() < 50)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMaxMiddle(), multiply(tmp.getParentMaxMiddle(), 1.001))) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.065) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.55 < 0)) {
                            return false;
                        }
                        break;
                    case 3:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.080) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.7 < 0)) {
                            return false;
                        }
                        break;
                    case 4:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.09) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.8 < 0)) {
                            return false;
                        }
                        break;
                    case 5:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.095) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 1.9 < 0)) {
                            return false;
                        }
                        break;
                    case 6:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.10) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.0 < 0)) {
                            return false;
                        }
                        break;
                    case 7:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.105) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.1 < 0)) {
                            return false;
                        }
                        break;
                    case 8:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.11) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.2 < 0)) {
                            return false;
                        }
                        break;
                    case 9:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.115) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.3 < 0)) {
                            return false;
                        }
                        break;
                    case 10:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.12) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.4 < 0)) {
                            return false;
                        }
                        break;
                    case 11:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.125) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.5 < 0)) {
                            return false;
                        }
                        break;
                    case 12:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.13) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.6 < 0)) {
                            return false;
                        }
                        break;
                    case 13:
                        if (tmp.getMinMiddle() < -0.1
                                || (lessThan(tmp.getMaxMiddle(), 0.135) && tmp.getMaxMiddle() + tmp.getMinMiddle() * 2.7 < 0)) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
    );
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
    private final Function<StrategyTmp, Boolean> isCntConf;
    private final Function<StrategyTmp, Boolean> isConformity;


}