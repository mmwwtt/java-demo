package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 上升缺口Strategy {
    static {
        StockCalcService.STRATEGY_LIST.addAll(Arrays.asList(
                new StockStrategy("上升缺口", (StockDetail t0) -> {
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice());
                }),


                new StockStrategy("上升缺口 且放量", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                            && moreThan(space, "0.0");
                }),
                new StockStrategy("上升缺口 且缩量", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                            && moreThan(space, "0.0");
                }),

                new StockStrategy("上升缺口 量(1-1.1)", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && isInRange(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "1"), multiply(t0.getT1().getDealQuantity(), "1.1"))
                            && moreThan(space, "0.0");
                }),

                new StockStrategy("上升缺口 量(0.9-1)", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && isInRange(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.9"), multiply(t0.getT1().getDealQuantity(), "1"))
                            && moreThan(space, "0.0");
                }),

                new StockStrategy("上升缺口 量(0.8-0.9)", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && isInRange(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"), multiply(t0.getT1().getDealQuantity(), "0.9"))
                            && moreThan(space, "0.0");
                }),


                new StockStrategy("上升缺口 量(0.8以下)", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                            && moreThan(space, "0.0");
                }),

                new StockStrategy("上升缺口 量(0.8以下)  0<缺口<0.005", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                            && isInRange(space, "0", "0.005");
                }),

                new StockStrategy("上升缺口 量(0.8以下)  0.005<缺口<0.01", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                            && isInRange(space, "0.005", "0.01");
                }),

                new StockStrategy("上升缺口 量(0.8以下)  0.01<缺口<0.02", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                            && isInRange(space, "0.01", "0.02");
                }),

                new StockStrategy("上升缺口 量(0.8以下)  0.02<缺口", (StockDetail t0) -> {
                    BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                            && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                            && moreThan(space, "0.02");
                })
        ));
    }
}
