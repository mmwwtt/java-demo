package com.mmwwtt.stock.vo;

import com.mmwwtt.stock.entity.StrategyWin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DfsTask {
    private Set<String> parentStrategyCodeSet;
    private BigDecimal parentFiveMaxPercRate;
    private int[] parentDetails;
    private int curIdx;
    private Function<StrategyWin, Boolean> isNotFunc;
}
