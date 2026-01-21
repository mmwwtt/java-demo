package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 底部阳包阴Strategy {
    static {
        //前一天绿， 当天是红，将前一天完全包裹  放量1.5倍  下影线占4层以上
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("底部阳包阴(看涨吞没) 放量1.5", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return t1.getIsDown() && t0.getIsUp()
                    && lessThan(t0.getStartPrice(), t1.getEndPrice())
                    && moreThan(t0.getEndPrice(), t1.getStartPrice())
                    && moreThan(t0.getDealQuantity(), multiply(t0.getFiveDayDealQuantity(), "1.5"))
                    && moreThan(t0.getLowShadowPert(), "0.4");
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("底部阳包阴 放量 0.5<下影线", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return t1.getIsDown() && t0.getIsUp()
                    && lessThan(t0.getStartPrice(), t1.getEndPrice())
                    && moreThan(t0.getEndPrice(), t1.getStartPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity())
                    && moreThan(t0.getLowShadowPert(), "0.5");
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("底部阳包阴 放量 0.4<下影线", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return t1.getIsDown() && t0.getIsUp()
                    && lessThan(t0.getStartPrice(), t1.getEndPrice())
                    && moreThan(t0.getEndPrice(), t1.getStartPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity())
                    && moreThan(t0.getLowShadowPert(), "0.4");
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("底部阳包阴 缩量", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return t1.getIsDown() && t0.getIsUp()
                    && lessThan(t0.getStartPrice(), t1.getEndPrice())
                    && moreThan(t0.getEndPrice(), t1.getStartPrice())
                    && lessThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity())
                    && moreThan(t0.getLowShadowPert(), "0.4");
        }));
    }
}
