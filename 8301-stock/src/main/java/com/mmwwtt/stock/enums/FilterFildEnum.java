package com.mmwwtt.stock.enums;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.lessThan;
import static com.mmwwtt.stock.common.CommonUtils.multiply;

@AllArgsConstructor
@Getter
public enum FilterFildEnum implements BaseEnum {
    RISE5_MAX_MIDDLE("rise5MaxMiddle", "最大五日涨幅中位数",
            Detail::getNext5MaxPricePert,
            new QueryWrapper<StrategyTmp>().apply("rise5_max_middle>0.14  and rise5_max_middle<0.15"),
            (StrategyTmp tmp) -> {
                if (tmp.getDateCnt() < 80 || lessThan(tmp.getPert(), 0.025)) {
                    return false;
                }
                int level = tmp.getStrategyCodeSet().size();
                if( lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.02))
                        || (level == 2 && lessThan(tmp.getPert(), 0.075))
                        || (level == 3 && lessThan(tmp.getPert(), 0.085))
                        || (level == 4 && lessThan(tmp.getPert(), 0.095))
                        || (level == 5 && lessThan(tmp.getPert(), 0.105))
                        || (level == 6 && lessThan(tmp.getPert(), 0.11))
                        || (level == 7 && lessThan(tmp.getPert(), 0.12))) {
                    return false;
                };
                return true;
            }),
    RISE5_MAX_AVG("rise5MaxAvg", "最大五日涨幅平均数",
            Detail::getNext5MaxPricePert,
            new QueryWrapper<StrategyTmp>().apply("rise5_max_avg>0.14  and rise5_max_avg<0.15"),
            (StrategyTmp tmp) -> {
                if (tmp.getDateCnt() < 80 || lessThan(tmp.getPert(), 0.05)) {
                    return false;
                }
                int level = tmp.getStrategyCodeSet().size();
                if(lessThan(tmp.getPert(), multiply(tmp.getParentPert(), 1.01))
                        || (level == 2 && lessThan(tmp.getPert(), 0.08))
                        || (level == 3 && lessThan(tmp.getPert(), 0.09))
                        || (level == 4 && lessThan(tmp.getPert(), 0.10))
                        || (level == 5 && lessThan(tmp.getPert(), 0.11))
                        || (level == 6 && lessThan(tmp.getPert(), 0.115))
                        || (level == 7 && lessThan(tmp.getPert(), 0.12))) {
                    return false;
                };
                return true;
            }),

    ;
    private final String code;
    private final String desc;

    /**
     * getter setter方法
     */
    private final Function<Detail, Double> detailGetter;

    /**
     * 需要填充策略数据的查询sql
     */
    private final QueryWrapper<StrategyTmp> wapper;

    /**
     * 策略过滤，是否符合 ture则保留， false则抛弃
     */
    private final Function<StrategyTmp, Boolean> isConformity;
}