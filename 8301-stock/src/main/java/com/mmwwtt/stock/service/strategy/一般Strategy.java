package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import static com.mmwwtt.stock.common.CommonUtils.lessThan;
import static com.mmwwtt.stock.common.CommonUtils.moreThan;

@Service
public class 一般Strategy {
    static {
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("啥也不做", (StockDetail t0) -> true));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("当天是红", StockDetail::getIsUp));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("当天是绿", StockDetail::getIsDown));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("比前一天放量", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return moreThan(t0.getDealQuantity(), t1.getDealQuantity());
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("放量红", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsRed()
                    &&t1.getIsGreen();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("放量绿", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsGreen()
                    && t1.getIsRed();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量红", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return lessThan(t0.getDealQuantity(), t1.getDealQuantity())
                    && t0.getIsRed()
                    && t1.getIsGreen();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量绿", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsGreen()
                    && t1.getIsRed();
        }));



    }
}
