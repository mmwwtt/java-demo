package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class 日内V反Strategy {
    static {
        //实体>1%
        //振幅>4%
        //放量
        //下影线 占6成

        StockCalcService.STRATEGY_LIST.addAll(Arrays.asList(
                new StockStrategy("日内V反 振幅5-6", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && isInRange(t0.getAllLen(), "0.05", "0.06");
                }),

                new StockStrategy("日内V反 振幅6-7", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && isInRange(t0.getAllLen(), "0.06", "0.07");
                }),

                new StockStrategy("日内V反 振幅7-8", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && isInRange(t0.getAllLen(), "0.07", "0.08");
                }),

                new StockStrategy("日内V反 振幅8-9", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && isInRange(t0.getAllLen(), "0.08", "0.09");
                }),

                new StockStrategy("日内V反 振幅9以上", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && moreThan(t0.getAllLen(), "0.09");
                }),

                new StockStrategy("日内V反 振幅5-6 收盘价低于10日线", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && isInRange(t0.getAllLen(), "0.05", "0.06")
                            && lessThan(t0.getEndPrice(), t0.getTenDayLine());
                }),

                new StockStrategy("日内V反 振幅8 收盘价低于10日线", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && moreThan(t0.getAllLen(), "0.08")
                            && lessThan(t0.getEndPrice(), t0.getTenDayLine());
                }),

                new StockStrategy("日内V反 振幅8 收盘价低于10日线*0.9", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && moreThan(t0.getAllLen(), "0.08")
                            && lessThan(t0.getEndPrice(), multiply(t0.getTenDayLine(), "0.9"));
                }),
                new StockStrategy("日内V反 8%<总长 且低于10日线", (StockDetail t0) -> {
                    return moreThan(t0.getLowShadowPert(), "0.6")
                            && t0.getIsRed()
                            && moreThan(t0.getAllLen(), "0.08")
                            && lessThan(t0.getPosition40(), 0.4)
                            && lessThan(t0.getEndPrice(), t0.getTenDayLine());
                })
        ));
    }
}
