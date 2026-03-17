package com.mmwwtt.stock.enums;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.demo.common.BaseEnum;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.StrategyWin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.lessThan;
import static com.mmwwtt.stock.common.CommonUtils.multiply;

@AllArgsConstructor
@Getter
public enum FilterFildEnum implements BaseEnum {
    RISE5_MAX_MIDDLE("rise5MaxMiddle", "最大五日涨幅中位数",
            Detail::getNext5MaxPricePert,
            StrategyWin::setRise5MaxMiddle,
            StrategyWin::getRise5MaxMiddle,
            new QueryWrapper<StrategyWin>().apply("rise5_max_middle>0.14  and rise5_max_middle<0.15"),
            (StrategyWin win) -> {
                if (win.getDateCnt() < 80 || lessThan(win.getRise5MaxMiddle(), 0.025)) {
                    return true;
                }
                int level = win.getStrategyCodeSet().size();
                return lessThan(win.getRise5MaxMiddle(), multiply(win.getParentLowLimit(), 1.02))
                        || (level == 2 && lessThan(win.getRise5MaxMiddle(), 0.075))
                        || (level == 3 && lessThan(win.getRise5MaxMiddle(), 0.085))
                        || (level == 4 && lessThan(win.getRise5MaxMiddle(), 0.095))
                        || (level == 5 && lessThan(win.getRise5MaxMiddle(), 0.105))
                        || (level == 6 && lessThan(win.getRise5MaxMiddle(), 0.11))
                        || (level == 7 && lessThan(win.getRise5MaxMiddle(), 0.12));
            }),
    RISE5_MAX_AVG("rise5MaxAvg", "最大五日涨幅平均数",
            Detail::getNext5MaxPricePert,
            StrategyWin::setRise5MaxAvg,
            StrategyWin::getRise5MaxMiddle,
            new QueryWrapper<StrategyWin>().apply("rise5_max_avg>0.14  and rise5_max_avg<0.15"),
            (StrategyWin win) -> {
                if (win.getDateCnt() < 80 || lessThan(win.getRise5MaxAvg(), 0.05)) {
                    return true;
                }
                int level = win.getStrategyCodeSet().size();
                return lessThan(win.getRise5MaxAvg(), multiply(win.getParentLowLimit(), 1.01))
                        || (level == 2 && lessThan(win.getRise5MaxAvg(), 0.08))
                        || (level == 3 && lessThan(win.getRise5MaxAvg(), 0.09))
                        || (level == 4 && lessThan(win.getRise5MaxAvg(), 0.10))
                        || (level == 5 && lessThan(win.getRise5MaxAvg(), 0.11))
                        || (level == 6 && lessThan(win.getRise5MaxAvg(), 0.115))
                        || (level == 7 && lessThan(win.getRise5MaxAvg(), 0.12));
            }),

    ;
    private final String code;
    private final String desc;

    /**
     * getter setter方法
     */
    private final Function<Detail, Double> detailGetter;
    private final BiConsumer<StrategyWin, Double> winSetter;
    private final Function<StrategyWin, Double> winGetter;

    /**
     * 需要填充策略数据的查询sql
     */
    private final QueryWrapper<StrategyWin> wapper;

    /**
     * 策略过滤方法
     */
    private final Function<StrategyWin, Boolean> func;
}