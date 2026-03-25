package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.stock.entity.Detail;
import lombok.*;

import java.util.function.Function;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "strategy_l1_t", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyL1 extends BaseStrategy {

    /**
     * 符合策略的详情数量统计
     */
    private Integer cnt;

    /**
     * 策略类别 ， 类型有只且相等则表示不会存在交集 (null则表示无类型)
     */
    private String type;

    @TableField(exist = false)
    private Function<Detail, Boolean> filterFunc;


    public StrategyL1(String strategyCode, String name, Function<Detail, Boolean> filterFunc) {
        this(strategyCode, name, null, filterFunc);
    }

    public StrategyL1(String strategyCode, String name, String type, Function<Detail, Boolean> filterFunc) {
        this.strategyCode = strategyCode;
        this.name = name;
        this.type = type;
        this.filterFunc = filterFunc;
    }

    public void fillOtherData() {
        super.fillOtherData();
        this.cnt = detailIdArray.size();
    }

}
