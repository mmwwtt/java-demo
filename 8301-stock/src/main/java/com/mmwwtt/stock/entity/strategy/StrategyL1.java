package com.mmwwtt.stock.entity.strategy;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.stock.enums.StrategyEnum;
import lombok.*;

import java.util.Comparator;
import java.util.List;

import static com.mmwwtt.stock.service.impl.CommonDataService.idToDetailMap;

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


    public StrategyL1(StrategyEnum strategyEnum, List<Integer> detailIds) {
        this.strategyCode = strategyEnum.getCode();
        this.type = strategyEnum.getType();
        detailIds.sort(Comparator.comparing(Integer::intValue));
        JSONArray array = new JSONArray(detailIds.size());
        array.addAll(detailIds);
        this.detailIds = array;
        this.details = detailIds.stream().map(item -> idToDetailMap.get(item)).toList();
        this.cnt = detailIds.size();
    }

}
