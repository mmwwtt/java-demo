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
public enum FilterFieldEnum implements BaseEnum {


    RISE5_MAX_MIDDLE_025("rise5MaxMiddle025", "最大5日涨幅中位数 l1域值0.025",
            13, 30,
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
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001)) || tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.065) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.080) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.09) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.095) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.10) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.105) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),
    RISE3_MAX_MIDDLE_50_DAY("rise3MaxMiddle50Day", "最大3日涨幅中位数",
            15, 70,
            Detail::getRise3Max,
            Detail::getRise3Min,
            StrategyL1::getRise3MaxMiddle,
            StrategyL1::getRise3MinMiddle,
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
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001)) ||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.07) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.08) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.9) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.10) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),

    RISE3_MAX_MIDDLE_40_DAY("rise3MaxMiddle40Day", "最大3日涨幅中位数",
            15, 70,
            Detail::getRise3Max,
            Detail::getRise3Min,
            StrategyL1::getRise3MaxMiddle,
            StrategyL1::getRise3MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 70)
                        || (level == 3 && tmp.getDateCnt() < 70)
                        || (level == 4 && tmp.getDateCnt() < 70)
                        || (level == 5 && tmp.getDateCnt() < 60)
                        || (level == 6 && tmp.getDateCnt() < 60)
                        || (level == 7 && tmp.getDateCnt() < 60)
                        || (level == 8 && tmp.getDateCnt() < 50)
                        || (level == 9 && tmp.getDateCnt() < 50)
                        || (level == 10 && tmp.getDateCnt() < 50)
                        || (level == 11 && tmp.getDateCnt() < 40)
                        || (level == 12 && tmp.getDateCnt() < 40)
                        || (level == 13 && tmp.getDateCnt() < 40)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001))||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.07) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.08) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.9) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.10) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),

    RISE3_MAX_MIDDLE_30_DAY("rise3MaxMiddle30Day", "最大3日涨幅中位数",
            15, 70,
            Detail::getRise3Max,
            Detail::getRise3Min,
            StrategyL1::getRise3MaxMiddle,
            StrategyL1::getRise3MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 60)
                        || (level == 3 && tmp.getDateCnt() < 60)
                        || (level == 4 && tmp.getDateCnt() < 60)
                        || (level == 5 && tmp.getDateCnt() < 50)
                        || (level == 6 && tmp.getDateCnt() < 50)
                        || (level == 7 && tmp.getDateCnt() < 50)
                        || (level == 8 && tmp.getDateCnt() < 40)
                        || (level == 9 && tmp.getDateCnt() < 40)
                        || (level == 10 && tmp.getDateCnt() < 40)
                        || (level == 11 && tmp.getDateCnt() < 30)
                        || (level == 12 && tmp.getDateCnt() < 30)
                        || (level == 13 && tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001)) ||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.07) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.08) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.9) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.10) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),


    RISE3_MIDDLE_30_DAY("rise3Middle30Day", "3日涨幅中位数",
            13, 70,
            Detail::getRise3,
            Detail::getRise3Min,
            StrategyL1::getRise3Middle,
            StrategyL1::getRise3MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 60)
                        || (level == 3 && tmp.getDateCnt() < 60)
                        || (level == 4 && tmp.getDateCnt() < 60)
                        || (level == 5 && tmp.getDateCnt() < 50)
                        || (level == 6 && tmp.getDateCnt() < 50)
                        || (level == 7 && tmp.getDateCnt() < 50)
                        || (level == 8 && tmp.getDateCnt() < 40)
                        || (level == 9 && tmp.getDateCnt() < 40)
                        || (level == 10 && tmp.getDateCnt() < 40)
                        || (level == 11 && tmp.getDateCnt() < 30)
                        || (level == 12 && tmp.getDateCnt() < 30)
                        || (level == 13 && tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001)) || tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.007) && tmp.getMiddle() + tmp.getMinMiddle() * 0.5 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.010) && tmp.getMiddle() + tmp.getMinMiddle() * 0.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.015) && tmp.getMiddle() + tmp.getMinMiddle() * 0.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.020) && tmp.getMiddle() + tmp.getMinMiddle() * 0.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.025) && tmp.getMiddle() + tmp.getMinMiddle() * 1.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.030) && tmp.getMiddle() + tmp.getMinMiddle() * 1.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.035) && tmp.getMiddle() + tmp.getMinMiddle() * 1.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.04) && tmp.getMiddle() + tmp.getMinMiddle() * 1.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.05) && tmp.getMiddle() + tmp.getMinMiddle() * 1.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.055) && tmp.getMiddle() + tmp.getMinMiddle() * 1.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),


    RISE1_MAX_MIDDLE_50_DAY("rise1MaxMiddle50Day", "最大1日涨幅中位数",
            13, 70,
            Detail::getRise1Max,
            Detail::getRise1Min,
            StrategyL1::getRise1MaxMiddle,
            StrategyL1::getRise1MinMiddle,
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
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001))||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.025) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.035) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.055) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.065) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
    ),

    RISE1_MAX_MIDDLE_40_DAY("rise1MaxMiddle40Day", "最大1日涨幅中位数",
            13, 70,
            Detail::getRise1Max,
            Detail::getRise1Min,
            StrategyL1::getRise1MaxMiddle,
            StrategyL1::getRise1MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 70)
                        || (level == 3 && tmp.getDateCnt() < 70)
                        || (level == 4 && tmp.getDateCnt() < 70)
                        || (level == 5 && tmp.getDateCnt() < 60)
                        || (level == 6 && tmp.getDateCnt() < 60)
                        || (level == 7 && tmp.getDateCnt() < 60)
                        || (level == 8 && tmp.getDateCnt() < 50)
                        || (level == 9 && tmp.getDateCnt() < 50)
                        || (level == 10 && tmp.getDateCnt() < 50)
                        || (level == 11 && tmp.getDateCnt() < 40)
                        || (level == 12 && tmp.getDateCnt() < 40)
                        || (level == 13 && tmp.getDateCnt() < 40)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001))||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.025) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.035) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.055) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.065) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }),


    RISE1_MAX_MIDDLE_30_DAY("rise1MaxMiddle30Day", "最大1日涨幅中位数",
            13, 70,
            Detail::getRise1Max,
            Detail::getRise1Min,
            StrategyL1::getRise1MaxMiddle,
            StrategyL1::getRise1MinMiddle,
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if ((level == 2 && tmp.getDateCnt() < 60)
                        || (level == 3 && tmp.getDateCnt() < 60)
                        || (level == 4 && tmp.getDateCnt() < 60)
                        || (level == 5 && tmp.getDateCnt() < 50)
                        || (level == 6 && tmp.getDateCnt() < 50)
                        || (level == 7 && tmp.getDateCnt() < 50)
                        || (level == 8 && tmp.getDateCnt() < 40)
                        || (level == 9 && tmp.getDateCnt() < 40)
                        || (level == 10 && tmp.getDateCnt() < 40)
                        || (level == 11 && tmp.getDateCnt() < 30)
                        || (level == 12 && tmp.getDateCnt() < 30)
                        || (level == 13 && tmp.getDateCnt() < 30)) {
                    return false;
                }
                return true;
            },

            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getMiddle(), multiply(tmp.getParentMiddle(), 1.001))||tmp.getMinMiddle() < -0.1) {
                    return false;
                }
                switch (level) {
                    case 2:
                        if (lessThan(tmp.getMiddle(), 0.025) && tmp.getMiddle() + tmp.getMinMiddle() * 1.55 < 0) {
                            return false;
                        }
                        break;
                    case 3:
                        if (lessThan(tmp.getMiddle(), 0.035) && tmp.getMiddle() + tmp.getMinMiddle() * 1.7 < 0) {
                            return false;
                        }
                        break;
                    case 4:
                        if (lessThan(tmp.getMiddle(), 0.045) && tmp.getMiddle() + tmp.getMinMiddle() * 1.8 < 0) {
                            return false;
                        }
                        break;
                    case 5:
                        if (lessThan(tmp.getMiddle(), 0.055) && tmp.getMiddle() + tmp.getMinMiddle() * 1.9 < 0) {
                            return false;
                        }
                        break;
                    case 6:
                        if (lessThan(tmp.getMiddle(), 0.06) && tmp.getMiddle() + tmp.getMinMiddle() * 2.0 < 0) {
                            return false;
                        }
                        break;
                    case 7:
                        if (lessThan(tmp.getMiddle(), 0.065) && tmp.getMiddle() + tmp.getMinMiddle() * 2.1 < 0) {
                            return false;
                        }
                        break;
                    case 8:
                        if (lessThan(tmp.getMiddle(), 0.11) && tmp.getMiddle() + tmp.getMinMiddle() * 2.2 < 0) {
                            return false;
                        }
                        break;
                    case 9:
                        if (lessThan(tmp.getMiddle(), 0.115) && tmp.getMiddle() + tmp.getMinMiddle() * 2.3 < 0) {
                            return false;
                        }
                        break;
                    case 10:
                        if (lessThan(tmp.getMiddle(), 0.12) && tmp.getMiddle() + tmp.getMinMiddle() * 2.4 < 0) {
                            return false;
                        }
                        break;
                    case 11:
                        if (lessThan(tmp.getMiddle(), 0.125) && tmp.getMiddle() + tmp.getMinMiddle() * 2.5 < 0) {
                            return false;
                        }
                        break;
                    case 12:
                        if (lessThan(tmp.getMiddle(), 0.13) && tmp.getMiddle() + tmp.getMinMiddle() * 2.6 < 0) {
                            return false;
                        }
                        break;
                    case 13:
                        if (lessThan(tmp.getMiddle(), 0.135) && tmp.getMiddle() + tmp.getMinMiddle() * 2.7 < 0) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            });
    private final String code;
    private final String desc;
    private final int levelLimit;
    private final int topLimit;
    private final Function<Detail, Double> detailMiddleGetter;
    private final Function<Detail, Double> detailMinMiddleGetter;
    private final Function<StrategyL1, Double> l1MiddleGetter;
    private final Function<StrategyL1, Double> l1MinMiddleGetter;
    /**
     * 策略过滤，是否符合 ture则保留， false则抛弃
     */
    private final Function<StrategyTmp, Boolean> isCntConf;
    private final Function<StrategyTmp, Boolean> isConformity;


}