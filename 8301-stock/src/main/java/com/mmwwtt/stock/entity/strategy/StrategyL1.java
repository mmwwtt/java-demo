package com.mmwwtt.stock.entity.strategy;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Comparator;
import java.util.List;

import static com.mmwwtt.stock.service.impl.CommonService.idToDetailMap;

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


    public StrategyL1(String strategyCode, List<Integer> detailIds) {
        this.strategyCode = strategyCode;
        detailIds.sort(Comparator.comparing(Integer::intValue));
        JSONArray array = new JSONArray(detailIds.size());
        array.addAll(detailIds);
        this.detailIds = array;
        this.details = detailIds.stream().map(item -> idToDetailMap.get(item)).toList();
        this.cnt = detailIds.size();
    }
}
