package com.mmwwtt.stock.enums;

import com.mmwwtt.stock.common.BaseEnum;
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
            (StrategyTmp tmp) -> {
                int level = tmp.getStrategyCodeSet().size();
                if (lessThan(tmp.getRise5MaxMiddle(), multiply(tmp.getParentRise5MaxMiddle(), 1.001))
                        || (level == 2 && (lessThan(tmp.getRise5MaxMiddle(), 0.07) || tmp.getDateCnt() < 70 || tmp.getRise5MinMiddle() < -0.1))
                        || (level == 3 && (lessThan(tmp.getRise5MaxMiddle(), 0.075) || tmp.getDateCnt() < 70 || tmp.getRise5MinMiddle() < -0.06))
                        || (level == 4 && (lessThan(tmp.getRise5MaxMiddle(), 0.08) || tmp.getDateCnt() < 70 || tmp.getRise5MinMiddle() < -0.055))
                        || (level == 5 && (lessThan(tmp.getRise5MaxMiddle(), 0.085) || tmp.getDateCnt() < 65 || tmp.getRise5MinMiddle() < -0.05))
                        || (level == 6 && (lessThan(tmp.getRise5MaxMiddle(), 0.09) || tmp.getDateCnt() < 65 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 7 && (lessThan(tmp.getRise5MaxMiddle(), 0.10) || tmp.getDateCnt() < 65 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 8 && (lessThan(tmp.getRise5MaxMiddle(), 0.11) || tmp.getDateCnt() < 65 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 9 && (lessThan(tmp.getRise5MaxMiddle(), 0.12) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.045))
                        || (level == 10 && (lessThan(tmp.getRise5MaxMiddle(), 0.13) || tmp.getDateCnt() < 60 || tmp.getRise5MinMiddle() < -0.045))
                ) {
                    return false;
                }
                return true;
            }
    ),


    ;
    private final String code;
    private final String desc;


    /**
     * 策略过滤，是否符合 ture则保留， false则抛弃
     */
    private final Function<StrategyTmp, Boolean> isConformity;

}