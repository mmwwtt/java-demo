package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 下影线长度Strategy {
    static {
        StockCalcService.STRATEGY_LIST.addAll(
                Arrays.asList(
                        new StockStrategy("下影线 8%<下影线 且红", (StockDetail t0) -> {
                            return moreThan(t0.getLowShadowLen(), "0.08") && t0.getIsRed();
                        }),

                        new StockStrategy("下影线 7%<下影线<8% 且红", (StockDetail t0) -> {
                            return isInRange(t0.getLowShadowLen(), "0.07", "0.08") && t0.getIsRed();
                        }),

                        new StockStrategy("下影线 6%<下影线<7% 且红", (StockDetail t0) -> {
                            return isInRange(t0.getLowShadowLen(), "0.06", "0.07") && t0.getIsRed();
                        }),

                        new StockStrategy("下影线 5%<下影线<6% 且红", (StockDetail t0) -> {
                            return isInRange(t0.getLowShadowLen(), "0.05", "0.06") && t0.getIsRed();
                        }),

                        new StockStrategy("下影线 4%<下影线<5% 且红", (StockDetail t0) -> {
                            return isInRange(t0.getLowShadowLen(), "0.04", "0.05") && t0.getIsRed();
                        }),


                        new StockStrategy("下影线 20区间的 0-10%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 20区间的 0-10%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 20区间的 0-10%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        }),

                        new StockStrategy("下影线 20区间的 10-20%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 20区间的 10-20%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 20区间的 10-20%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition20(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        }),


                        new StockStrategy("下影线 40区间的 0-10%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 40区间的 0-10%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 40区间的 0-10%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        }),

                        new StockStrategy("下影线 40区间的 10-20%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 40区间的 10-20%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 40区间的 10-20%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition40(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        }),


                        new StockStrategy("下影线 60区间的 0-10%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 60区间的 0-10%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0","0.1")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 60区间的 0-10%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        }),

                        new StockStrategy("下影线 60区间的 10-20%， 下影线0.04-0.1", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.1");
                        }),

                        new StockStrategy("下影线 60区间的 10-20%， 下影线0.04-0.05", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.04", "0.05");
                        }),

                        new StockStrategy("下影线 60区间的 10-20%， 下影线0.03-0.04", (StockDetail t0) -> {
                            return isInRange(t0.getPosition60(), "0.1","0.2")
                                    && isInRange(t0.getLowShadowLen(), "0.03", "0.04");
                        })
                ));
    }
}
