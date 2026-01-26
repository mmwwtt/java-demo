package com.mmwwtt.stock.service.strategy.不成功;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 阳线Strategy {
    static {
        StockCalcService.STRATEGY_LIST.addAll(
                Arrays.asList(

                        new StockStrategy("阳线 小阳线", (StockDetail t0) -> {
                            return isInRange(t0.getPricePert(), "0.00", "0.03");
                        }),
                        new StockStrategy("阳线 中阳线", (StockDetail t0) -> {
                            return isInRange(t0.getPricePert(), "0.03", "0.06");
                        }),


                        new StockStrategy(" 阳线 大阳线", (StockDetail t0) -> {
                            return isInRange(t0.getPricePert(), "0.06", "0.10");
                        }),


                        new StockStrategy("阳线 小阳线(光头光脚)", (StockDetail t0) -> {
                            return isInRange(t0.getPricePert(), "0.00", "0.03")
                                    && isEquals(t0.getHighPrice(), t0.getEndPrice())
                                    && isEquals(t0.getLowPrice(), t0.getStartPrice());
                        }),
                        new StockStrategy("阳线 中阳线(光头光脚)", (StockDetail t0) -> {
                            return isInRange(t0.getPricePert(), "0.03", "0.06")
                                    && isEquals(t0.getHighPrice(), t0.getEndPrice())
                                    && isEquals(t0.getLowPrice(), t0.getStartPrice());
                        }),


                        new StockStrategy(" 阳线 大阳线(光头光脚)", (StockDetail t0) -> {

                            return isInRange(t0.getPricePert(), "0.06", "0.09")
                                    && isEquals(t0.getHighPrice(), t0.getEndPrice())
                                    && isEquals(t0.getLowPrice(), t0.getStartPrice());
                        })

                )
        );
    }
}
