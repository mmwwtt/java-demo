package com.mmwwtt.stock.entity.strategy;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.stock.entity.Detail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@TableName(value = "strategy_l1_t", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyL1 extends BaseStrategy {

    public StrategyL1(String strategyCode, List<Integer> stockDetailIds) {
        this.strategyCode = strategyCode;
        stockDetailIds.sort(Comparator.comparing(Integer::intValue));
        JSONArray array = new JSONArray(stockDetailIds.size());
        array.addAll(stockDetailIds);
        this.detailIds = array;
    }

    public StrategyL1(String strategyCode, List<Detail> details) {
        this.strategyCode = strategyCode;
        this.setDetails(details);
    }
}
