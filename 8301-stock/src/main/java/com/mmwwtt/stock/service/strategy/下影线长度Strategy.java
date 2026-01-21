package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 下影线长度Strategy {
    static {
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("8%<下影线 且红", (StockDetail t0) -> {
            return moreThan(t0.getLowShadowLen(), "0.08")&& t0.getIsRed();
        }));
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("7%<下影线<8% 且红", (StockDetail t0) -> {
            return isInRange(t0.getLowShadowLen(), "0.07", "0.08")&& t0.getIsRed();
        }));
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("6%<下影线<7% 且红", (StockDetail t0) -> {
            return isInRange(t0.getLowShadowLen(), "0.06", "0.07")&& t0.getIsRed();
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("5%<下影线<6% 且红", (StockDetail t0) -> {
            return isInRange(t0.getLowShadowLen(), "0.05", "0.06")&& t0.getIsRed();
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("4%<下影线<5% 且红", (StockDetail t0) -> {
            return isInRange(t0.getLowShadowLen(), "0.04", "0.05")&& t0.getIsRed();
        }));
    }
}
