package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class MacdStrategy {

    static {
        StockCalcService.STRATEGY_LIST.addAll(Arrays.asList(
                new StockStrategy("macd 小于 -2", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return lessThan(t0.getMacd(), "-2");
                }),
                new StockStrategy("macd -2 到 -1", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "-2", "-1");
                }),


                new StockStrategy("macd -1 到 0", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "-1", "0");
                }),

                new StockStrategy("macd 0-1", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "0", "1");
                }),
                new StockStrategy("macd 1-2", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "1", "2");
                }),

                new StockStrategy("macd 2-3", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "2", "3");
                }),

                new StockStrategy("macd  大于3", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return moreThan(t0.getMacd(), "3");
                }),


                new StockStrategy("macd 2-3  高于5日线", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "2", "3")
                            && moreThan(t0.getEndPrice(), t0.getFiveDayLine());
                }),

                new StockStrategy("macd 2-3  低于5日线", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "2", "3")
                            && lessThan(t0.getEndPrice(), t0.getFiveDayLine());
                }),

                new StockStrategy("macd 2-3  缩量", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "2", "3")
                            && lessThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "0.8"));
                }),

                new StockStrategy("macd 2-3  比前一天macd大", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    StockDetail t3 = t0.getT3();
                    return isInRange(t0.getMacd(), "2", "3")
                            && moreThan(t0.getMacd(), t1.getMacd());
                })

        ));
    }
}
