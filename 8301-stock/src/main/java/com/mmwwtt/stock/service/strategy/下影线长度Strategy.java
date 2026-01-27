package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.isInRange;
import static com.mmwwtt.stock.common.CommonUtils.moreThan;

/**
 * 长下影线往往表示下跌见底后 有希望反弹
 */
@Service
public class 下影线长度Strategy {
    static {
        StockCalcService.STRATEGY_LIST.addAll(Arrays.asList(

                new StockStrategy("下影线 5%<下影线<6%", (StockDetail t0) -> {
                    return isInRange(t0.getLowShadowLen(), 0.05, 0.06);
                }),

                new StockStrategy("下影线 6%<下影线<10%", (StockDetail t0) -> {
                    return isInRange(t0.getLowShadowLen(), 0.06, 0.10);
                }),

                new StockStrategy("下影线 前一天是绿， 5%<下影线<6%", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    return t1.getIsGreen()
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.06);
                }),
                new StockStrategy("下影线 前一天是绿， 6%<下影线<7%", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    return t1.getIsGreen()
                            && isInRange(t0.getLowShadowLen(), 0.06, 0.07);
                }),
                new StockStrategy("下影线 前一天是绿， 7%<下影线<10%", (StockDetail t0) -> {
                    StockDetail t1 = t0.getT1();
                    return t1.getIsGreen()
                            && isInRange(t0.getLowShadowLen(), 0.07, 0.10);
                }),


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


                new StockStrategy("下影线 20区间的 0-10%， 下影线0.02-0.03 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.02, 0.03)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 20区间的 0-10%， 下影线0.03-0.04 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.03, 0.04)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 20区间的 10-20%， 下影线0.04-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 20区间的 20-30%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 20区间的 30-40%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 20区间的 40-50%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.4, 0.5)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),


                new StockStrategy("下影线 40区间的 0-10%， 下影线0.02-0.03 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.02, 0.03)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 40区间的 0-10%， 下影线0.03-0.04 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.03, 0.04)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 40区间的 10-20%， 下影线0.04-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 40区间的 20-30%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 40区间的 30-40%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),


                new StockStrategy("下影线 60区间的 10-20%， 下影线0.04-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 60区间的 20-30%， 下影线0.04-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsRed();
                }),

                new StockStrategy("下影线 60区间的 30-40%， 下影线0.05-0.10 且红", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsRed();
                }),


                new StockStrategy("下影线 20区间的 0-10%， 下影线0.02-0.03  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.02, 0.03)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 20区间的 0-10%， 下影线0.03-0.04  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.03, 0.04)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 20区间的 10-20%， 下影线0.04-0.10  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 20区间的 20-30%， 下影线0.05-0.10  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 20区间的 30-40%， 下影线0.05-0.10  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 20区间的 40-50%， 下影线0.05-0.10  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition20(), 0.4, 0.5)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                }),


                new StockStrategy("下影线 40区间的 0-10%， 下影线0.02-0.03  且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.02, 0.03)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 40区间的 0-10%， 下影线0.03-0.04 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.0, 0.1)
                            && isInRange(t0.getLowShadowLen(), 0.03, 0.04)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 40区间的 10-20%， 下影线0.04-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 40区间的 20-30%， 下影线0.05-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 40区间的 30-40%， 下影线0.05-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                }),


                new StockStrategy("下影线 60区间的 10-20%， 下影线0.04-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.1, 0.2)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 60区间的 20-30%， 下影线0.04-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.2, 0.3)
                            && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                            && t0.getIsGreen();
                }),

                new StockStrategy("下影线 60区间的 30-40%， 下影线0.05-0.10 且绿", (StockDetail t0) -> {
                    return isInRange(t0.getPosition40(), 0.3, 0.4)
                            && isInRange(t0.getLowShadowLen(), 0.05, 0.10)
                            && t0.getIsGreen();
                })
        ));

//        List<StockStrategy> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                String leftI = multiply(i, 0.1).toString();
//                String rightI = multiply(i + 1, 0.1).toString();
//                String leftJ = multiply(j, 0.01).toString();
//                String rightJ = multiply(j + 1, 0.01).toString();
//                String name20 = String.format("下影线 20区间的 %s-%s， 下影线%s-%s", leftI, rightI, leftJ, rightJ);
//                list.add(new StockStrategy(name20, (StockDetail t0) -> {
//                    return isInRange(t0.getPosition20(), leftI, rightI)
//                            && isInRange(t0.getLowShadowLen(), leftJ, rightJ);
//                }));
//
//                String name40 = String.format("下影线 40区间的 %s-%s， 下影线%s-%s", leftI, rightI, leftJ, rightJ);
//                list.add(new StockStrategy(name40, (StockDetail t0) -> {
//                    return isInRange(t0.getPosition40(), leftI, rightI)
//                            && isInRange(t0.getLowShadowLen(), leftJ, rightJ);
//                }));
//
//                String name60 = String.format("下影线 60区间的 %s-%s， 下影线%s-%s", leftI, rightI, leftJ, rightJ);
//                list.add(new StockStrategy(name60, (StockDetail t0) -> {
//                    return isInRange(t0.getPosition60(), leftI, rightI)
//                            && isInRange(t0.getLowShadowLen(), leftJ, rightJ);
//                }));
//            }
//        }
//        StockCalcService.STRATEGY_LIST.addAll(list);
    }
}
