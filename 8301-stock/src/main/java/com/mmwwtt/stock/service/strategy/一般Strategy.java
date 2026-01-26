package com.mmwwtt.stock.service.strategy;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.common.CommonUtils.divide;
import static com.mmwwtt.stock.common.CommonUtils.max;
import static com.mmwwtt.stock.common.CommonUtils.min;
import static com.mmwwtt.stock.common.CommonUtils.multiply;

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
                    && t1.getIsGreen();
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


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("十字星", StockDetail::getIsTenStar));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("二连红", (StockDetail t0) -> {
            if (Objects.isNull(t0.getT1())) {
                return false;
            }
            return t0.getIsUp() && t0.getT1().getIsUp();
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("连续2天放量，且当天是红", (StockDetail t0) -> {
            return moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && moreThan(t0.getT1().getDealQuantity(), t0.getT2().getDealQuantity())
                    && t0.getIsUp();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量 二连红", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsUp()
                    && t0.getT1().getIsUp();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量红", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsUp();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity());
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            return moreThan(t0.getPertDivisionQuantity(), t1.getPertDivisionQuantity())
                    && t0.getIsDown() && t1.getIsDown();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("双针探底_简化版", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            // 1. 连续两根长下影：下影占比 > 60%
            boolean longShadow1 = moreThan(t1.getLowShadowPert(), "0.6");
            boolean longShadow2 = moreThan(t2.getLowShadowPert(), "0.6");

            // 2. 两根下影K线实体小（防止大阴棒）
            boolean smallBody1 = lessThan(t1.getEntityPert(), "0.02");
            boolean smallBody2 = lessThan(t2.getEntityPert(), "0.02");

            // 4. 第3日收盘 > 前两日最高（确认反转）
            BigDecimal maxHigh12 = t1.getHighPrice().max(t2.getHighPrice());
            boolean breakHigh = moreThan(t0.getEndPrice(), maxHigh12);

            // 5. 放量确认（今日量 > 10日均量）
            boolean volUp = moreThan(t0.getDealQuantity(), t0.getTenDayLine());

            return longShadow1 && longShadow2 && smallBody1 && smallBody2
                    && t0.getIsUp() && breakHigh && volUp;
        }));



        StockCalcService.STRATEGY_LIST.add(new StockStrategy("放量突破20日最高", (StockDetail t0) -> {
            // 1. 收盘突破 20 日最高
            boolean priceBreak = moreThan(t0.getEndPrice(), t0.getTwentyHigh());
            // 2. 放量 > 20 日均量 × 1.8
            boolean volBreak = moreThan(t0.getDealQuantity(), multiply(t0.getTwentyDayDealQuantity(), "1.8"));
            return priceBreak && volBreak;
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("平底突破", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();

            // 1. 连续3日最低价差 ≤ 1%
            BigDecimal maxLow = max(t1.getLowPrice(), t2.getLowPrice(), t3.getLowPrice());
            BigDecimal minLow = min(t1.getLowPrice(), t2.getLowPrice(), t3.getLowPrice());
            boolean flat = lessThan(divide(subtract(maxLow,minLow), minLow), "0.01");

            // 2. 今日收盘 > 3 日最高收盘价（突破）
            BigDecimal maxClose3 = max(t1.getEndPrice(), t2.getEndPrice(), t3.getEndPrice());
            boolean breakClose = moreThan(t0.getEndPrice(), maxClose3);

            // 3. 今日放量 > 10 日均量（确认）
            boolean volUp = moreThan(t0.getDealQuantity(), t0.getTenDayLine());

            return flat && breakClose && volUp;
        }));

        // 大阴：实体≥2% 且为阴线
        // 十字星
        // 大阳：实体≥2% 且收盘>第1根开盘（完全反包）
        StockCalcService.STRATEGY_LIST.add(new StockStrategy("早晨之星(启明星)", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            return moreThan(t2.getEntityPert(), "0.3") && t2.getIsDown()
                    && t0.getIsUp()
                    && t1.getIsTenStar();
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量3连阴后首阳", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            // 1. 三连阴
            boolean threeBear = t1.getIsDown() && t2.getIsDown() && t3.getIsDown();

            // 2. 成交量逐日递减
            boolean volFade = lessThan(t1.getDealQuantity(), t2.getDealQuantity())
                    && lessThan(t2.getDealQuantity(), t3.getDealQuantity());

            // 4. 第4日放量 > 第3日量
            boolean volUp = moreThan(t0.getDealQuantity(), t1.getDealQuantity());

            return threeBear && volFade && t0.getIsUp() && volUp;
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("前一天是 缩量十字星 次日放量阳", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            // 缩量十字星 前一天是十字星 且缩量(小于5日线 60%)
            boolean flag1 = lessThan(t1.getDealQuantity(), multiply(t1.getFiveDayDealQuantity(), "0.6")) && t1.getIsTenStar();

            // 放量： 当日放量阳线 量是前一天1.5倍
            boolean flag2 = moreThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "1.5"));

            // 吞噬： 收盘价比前一天最高价
            boolean flag3 = moreThan(t0.getEndPrice(), t1.getHighPrice());

            return flag1 && flag2 && flag3;
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量回踩10日线后放量", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            /* 1. 连续 2-3 日缩量（量逐日递减） */
            boolean shrink = lessThan(t1.getDealQuantity(), t2.getDealQuantity())
                    && lessThan(t2.getDealQuantity(), t3.getDealQuantity());

            /* 2. 回踩 10 日线：曾触碰但未跌破（最低 ≤ 10 日线 ≤ 最高） */
            boolean touch10 = lessThan(t1.getLowPrice(), t1.getTenDayLine())
                    && moreThan(t1.getEndPrice(), t1.getTenDayLine());

            /* 3. 今日放量阳线 */
            boolean upToday = t0.getIsUp()
                    && moreThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "1.5"));

            return shrink && touch10 && upToday;
        }));


        StockCalcService.STRATEGY_LIST.add(new StockStrategy("5日线 突破 10日线", (StockDetail t0) -> {
            return moreThan(t0.getFiveDayLine(), t0.getTenDayLine())
                    && lessThan(t0.getT1().getFiveDayLine(), t0.getT1().getTenDayLine());
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("5日线 突破 20日线", (StockDetail t0) -> {
            return moreThan(t0.getFiveDayLine(), t0.getTwentyDayLine())
                    && lessThan(t0.getT1().getFiveDayLine(), t0.getT1().getTwentyDayLine());
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("金叉", (StockDetail t0) -> {
            // 金叉日大于五日均量1.2倍
            boolean flag1 = moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity());

            //5日线破10日线
            boolean flag2 = moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())
                    && lessThan(t0.getT1().getTenDayLine(), t0.getT1().getTwentyDayLine());
            return flag1 && flag2;
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量上涨 2天 且macd<-1", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return moreThan(t0.getPricePert(), "0.01")
                    && moreThan(t1.getPricePert(), "0.01")
                    && lessThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "0.8"))
                    && lessThan(t1.getDealQuantity(), multiply(t2.getDealQuantity(), "0.8"))
                    && lessThan(t0.getMacd(), "-1");
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("缩量上涨 3天", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return moreThan(t0.getPricePert(), "0.005")
                    && moreThan(t1.getPricePert(), "0.005")
                    && moreThan(t2.getPricePert(), "0.005")
                    && lessThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "0.8"))
                    && lessThan(t1.getDealQuantity(), multiply(t2.getDealQuantity(), "0.8"))
                    && lessThan(t2.getDealQuantity(), multiply(t3.getDealQuantity(), "0.8"));
        }));

        StockCalcService.STRATEGY_LIST.add(new StockStrategy("WR 低于 -80， 且在上升", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            return lessThan(t0.getWr(), t1.getWr())
                    && lessThan(t1.getWr(), t2.getWr())
                    && lessThan(t0.getWr(), "-80");
        }));


        StockStrategy strategy = new StockStrategy("阴线，但最低价 比前一天开盘价高", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return t0.getIsGreen()
                    && moreThan(t0.getLowPrice(), t1.getStartPrice());
        });
    }


}
