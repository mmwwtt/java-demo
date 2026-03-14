package com.mmwwtt.stock.vo;

import com.mmwwtt.stock.entity.StrategyWin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DfsTask {
    private int[] parentDetailIds;
    private StrategyWin parentWin;
    private int curIdx;
    private Function<StrategyWin, Boolean> isNotFunc;
}
