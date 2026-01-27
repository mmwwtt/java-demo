package com.mmwwtt.stock.service.strategy.不成功;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 三羊开泰Strategy {
    static {
        StockCalcService.STRATEGY_LIST.addAll(Arrays.asList(
                new StockStrategy("三羊开泰", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    StockDetail t2 = t0.getT2();
                    return isInRange(t0.getPricePert(), "0.03", "0.10")
                            && lessThan(t0.getHighPrice(), multiply(t0.getEndPrice(), "1.1"))
                            && moreThan(t0.getLowPrice(), multiply(t0.getStartPrice(), "0.9"))

                            && isInRange(t1.getPricePert(), "0.03", "0.10")
                            && lessThan(t1.getHighPrice(), multiply(t0.getEndPrice(), "1.1"))
                            && moreThan(t1.getLowPrice(), multiply(t0.getStartPrice(), "0.9"))

                            && isInRange(t2.getPricePert(), "0.03", "0.10")
                            && lessThan(t2.getHighPrice(), multiply(t0.getEndPrice(), "1.1"))
                            && moreThan(t2.getLowPrice(), multiply(t0.getStartPrice(), "0.9"))

                            && lessThan(t0.getPosition40(), "0.4");
                })

        ));
    }
}
