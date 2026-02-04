package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.common.Constants;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.add;
import static com.mmwwtt.stock.common.CommonUtils.moreThan;

@Service
public class 下影线和实体Strategy {
    static {
        Constants.STRATEGY_LIST.addAll(Arrays.asList(


                new StockStrategy("下影线和实体 10%<下影线 20日向上 ", (StockDetail t0) -> {
                    return moreThan(add(t0.getLowShadowLen(), t0.getEntityLen()), 0.1)
                            && t0.getTwentyIsUp();
                }),

                new StockStrategy("下影线和实体 10%<下影线 40日向上 ", (StockDetail t0) -> {
                    return moreThan(add(t0.getLowShadowLen(), t0.getEntityLen()), 0.1)
                            && t0.getFortyIsUp();
                }),

                new StockStrategy("下影线和实体 10%<下影线 60日向上 ", (StockDetail t0) -> {
                    return moreThan(add(t0.getLowShadowLen(), t0.getEntityLen()), 0.1)
                            && t0.getSixtyIsUp();
                }),

                new StockStrategy("下影线和实体 10%<总和 40日向下", (StockDetail t0) -> {
                    return moreThan(add(t0.getLowShadowLen(), t0.getEntityLen()), 0.1)
                            && !t0.getFortyIsUp();
                })

        ));
    }
}
