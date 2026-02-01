package com.mmwwtt.stock.service.strategy_new;

import com.mmwwtt.stock.enums.StrategyEnum;
import lombok.Data;

import java.util.List;

@Data
public class StrategyNew {

    private StrategyEnum strategyEnum;
    private List<StrategyNew> childList;
}
