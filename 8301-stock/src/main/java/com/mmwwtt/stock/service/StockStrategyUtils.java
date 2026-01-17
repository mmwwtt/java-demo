package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
public class StockStrategyUtils {
    public final static List<StockStrategy> STRATEGY_LIST = new ArrayList<>();

    static {
        STRATEGY_LIST.add(new StockStrategy("十字星", StockDetail::getIsTenStar));

        STRATEGY_LIST.add(new StockStrategy("二连红", (StockDetail t0) -> {
            if (Objects.isNull(t0.getT1())) {
                return false;
            }
            return t0.getIsUp() && t0.getT1().getIsUp();
        }));

        STRATEGY_LIST.add(new StockStrategy("啥也不做", (StockDetail t0) -> true));


        STRATEGY_LIST.add(new StockStrategy("当天是红", StockDetail::getIsUp));


        STRATEGY_LIST.add(new StockStrategy("当天是绿", StockDetail::getIsDown));


        STRATEGY_LIST.add(new StockStrategy("比前一天放量", (StockDetail t0) -> {
            return moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("连续2天放量，且当天是红", (StockDetail t0) -> {
            return moreThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && moreThan(t0.getT1().getDealQuantity(), t0.getT2().getDealQuantity())
                    && t0.getIsUp();
        }));


        STRATEGY_LIST.add(new StockStrategy("缩量红", (StockDetail t0) -> {
            return lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsUp();
        }));


        STRATEGY_LIST.add(new StockStrategy("缩量绿", (StockDetail t0) -> {
            return lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsDown();
        }));


        STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量 二连红", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(),t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(),t0.getT1().getDealQuantity())
                    && t0.getIsUp()
                    && t0.getT1().getIsUp();
        }));


        STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量红", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsUp();
        }));


        STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大 缩量", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("涨幅成交比扩大", (StockDetail t0) -> {
            return moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 成交量超过5日线", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 成交量超过5日线 三日不回补", (StockDetail t0) -> {
            return moreThan(t0.getT3().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT2().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT1().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 成交量超过5日线 三日不回补 5日线>20日线", (StockDetail t0) -> {
            return moreThan(t0.getT3().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT2().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT1().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity())
                    && moreThan(t0.getFiveDayDealQuantity(), t0.getTwentyDayDealQuantity());
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 成交量超过5日线 三日不回补 5日线>20日线 涨幅成交比扩大 二连红", (StockDetail t0) -> {
            return moreThan(t0.getT3().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT2().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getT1().getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getLowPrice(), t0.getT4().getHighPrice())
                    && moreThan(t0.getDealQuantity(), t0.getFiveDayDealQuantity())
                    && moreThan(t0.getFiveDayDealQuantity(), t0.getTwentyDayDealQuantity())
                    && moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && t0.getIsUp() && t0.getT1().getIsUp();
        }));


        STRATEGY_LIST.add(new StockStrategy("双针探底_简化版", (StockDetail t0) -> {
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


        STRATEGY_LIST.add(new StockStrategy("红三兵", (StockDetail t0) -> {
            return t0.getIsUp() && t0.getT1().getIsUp() && t0.getT2().getIsUp()
                    && moreThan(t0.getT1().getEntityLen(), t0.getT2().getEntityLen())
                    && moreThan(t0.getEntityLen(), t0.getT1().getEntityLen());
        }));


        STRATEGY_LIST.add(new StockStrategy("红三兵加强版", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            // 2. 实体逐级放大
            boolean entityGrow = moreThan(t1.getEntityLen(), t2.getEntityLen())
                    && moreThan(t0.getEntityLen(), t1.getEntityLen());

            // 3. 成交量逐级放大
            boolean volGrow = moreThan(t0.getDealQuantity(), t1.getDealQuantity())
                    && moreThan(t1.getDealQuantity(), t2.getDealQuantity());

            // 4. 最新收盘站上 5 日线
            boolean above5 = moreThan(t0.getEndPrice(), t0.getFiveDayLine());

            // 5. 第三根（最新）实体占比 ≥ 1%
            boolean bigEnough = moreThan(t0.getPricePert(), "0.01");

            return t0.getIsUp() && t0.getT1().getIsUp() && t0.getT2().getIsUp()
                    && moreThan(t0.getT1().getEntityLen(), t0.getT2().getEntityLen())
                    && moreThan(t0.getEntityLen(), t0.getT1().getEntityLen())
                    && entityGrow && volGrow && above5 && bigEnough;
        }));


        STRATEGY_LIST.add(new StockStrategy("放量突破20日最高", (StockDetail t0) -> {
            // 1. 收盘突破 20 日最高
            boolean priceBreak = moreThan(t0.getEndPrice(), t0.getTwentyDayHigh());
            // 2. 放量 > 20 日均量 × 1.8
            boolean volBreak = moreThan(t0.getDealQuantity(), multiply(t0.getTwentyDayLine(), "1.8"));
            return priceBreak && volBreak;
        }));


        STRATEGY_LIST.add(new StockStrategy("平底突破", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();

            // 1. 连续3日最低价差 ≤ 1%
            BigDecimal maxLow = max(t1.getLowPrice(), t2.getLowPrice(), t3.getLowPrice());
            BigDecimal minLow = min(t1.getLowPrice(), t2.getLowPrice(), t3.getLowPrice());
            boolean flat = lessThan(divide(maxLow.subtract(minLow), minLow), "0.01");

            // 2. 今日收盘 > 3 日最高收盘价（突破）
            BigDecimal maxClose3 = max(t1.getEndPrice(), t2.getEndPrice(), t3.getEndPrice());
            boolean breakClose = moreThan(t0.getEndPrice(), maxClose3);

            // 3. 今日放量 > 10 日均量（确认）
            boolean volUp = moreThan(t0.getDealQuantity(), t0.getTenDayLine());

            return flat && breakClose && volUp;
        }));


        STRATEGY_LIST.add(new StockStrategy("底部阳包阴", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();

            // 1. 前阴后阳

            // 2. 实体完全包裹：今日开盘≤前日收盘 且 今日收盘≥前日开盘
            boolean cover = lessThan(t0.getStartPrice(), t1.getEndPrice())
                    && moreThan(t0.getEndPrice(), t1.getStartPrice());

            // 3. 放量确认
            boolean volUp = moreThan(t0.getDealQuantity(), t1.getDealQuantity());

            return t1.getIsDown() && t0.getIsUp() && cover && volUp;
        }));


        STRATEGY_LIST.add(new StockStrategy("日内V反", (StockDetail t0) -> {
            boolean longShadow = moreThan(t0.getLowShadowPert(), "0.65");

            // 2. 阳实体且≥1%
            boolean solidBull = t0.getIsUp() &&
                    moreThan(t0.getEntityPert(), "0.01");

            // 3. 振幅 ≥ 4%
            BigDecimal amplitude = divide(t0.getHighPrice().subtract(t0.getLowPrice()), t0.getLowPrice());
            boolean highWave = moreThan(amplitude, "0.04");

            // 4. 放量
            boolean volUp = moreThan(t0.getDealQuantity(), t0.getTenDayLine());

            return longShadow && solidBull && highWave && volUp;
        }));


        STRATEGY_LIST.add(new StockStrategy("早晨之星简化版", (StockDetail t0) -> {
            if (Objects.isNull(t0.getT1())
                    || Objects.isNull(t0.getT2())) {
                return false;
            }
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            // 1. 大阴：实体≥2% 且为阴线
            boolean bigBear = moreThan(t2.getEntityPert(), "0.02") && t2.getIsDown();

            // 3. 大阳：实体≥2% 且收盘>第1根开盘（完全反包）
            boolean bigBull = moreThan(t0.getEntityPert(), "0.02") && t0.getIsUp()
                    && moreThan(t0.getEndPrice(), t2.getStartPrice());

            // 4. 第3日放量 > 20日均量
            boolean volUp = moreThan(t0.getDealQuantity(), t0.getTwentyDayLine());

            return bigBear && t1.getIsTenStar() && bigBull && volUp;
        }));


        STRATEGY_LIST.add(new StockStrategy("缩量3连阴后首阳", (StockDetail t0) -> {
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


        STRATEGY_LIST.add(new StockStrategy("前一天是 缩量十字星 次日放量阳", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            // 缩量十字星 前一天是十字星 且缩量(小于5日线 60%)
            boolean flag1 = lessThan(t1.getDealQuantity(), multiply(t1.getFiveDayDealQuantity(), "0.6")) &&t1.getIsTenStar();

            // 放量： 当日放量阳线 量是前一天1.5倍
            boolean flag2 = moreThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "1.5"));

            // 吞噬： 收盘价比前一天最高价
            boolean flag3 = moreThan(t0.getEndPrice(), t1.getHighPrice());

            return flag1 && flag2 && flag3;
        }));

        STRATEGY_LIST.add(new StockStrategy("大前天是 缩量十字星 次日放量阳", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            boolean flag1 = lessThan(t2.getDealQuantity(), multiply(t2.getFiveDayDealQuantity(), "0.6")) &&t2.getIsTenStar();
            boolean flag2 = moreThan(t1.getDealQuantity(), multiply(t2.getDealQuantity(), "1.5"));
            boolean flag3 = moreThan(t1.getEndPrice(), t2.getHighPrice());

            return flag1 && flag2 && flag3;
        }));


        STRATEGY_LIST.add(new StockStrategy("缩量回踩10日线后放量", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            /* 1. 连续 2-3 日缩量（量逐日递减） */
            boolean shrink = lessThan(t1.getDealQuantity(), t2.getDealQuantity())
                    && lessThan(t2.getDealQuantity(), t3.getDealQuantity())
                    && lessThan(t1.getDealQuantity(), multiply(t1.getTenDayLine(), "0.8"));

            /* 2. 回踩 10 日线：曾触碰但未跌破（最低 ≤ 10 日线 ≤ 最高） */
            boolean touch10 = lessThan(t1.getLowPrice(), t1.getTenDayLine())
                    && moreThan(t1.getHighPrice(), t1.getTenDayLine());

            /* 3. 今日放量阳线 */
            boolean upToday = t0.getIsUp()
                    && moreThan(t0.getDealQuantity(), multiply(t1.getDealQuantity(), "1.5"));

            /* 4. 今日收盘重新站上 10 日线 */
            boolean above10 = moreThan(t0.getEndPrice(), t0.getTenDayLine());

            return shrink && touch10 && upToday && above10;
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice());
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口 连续2", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && moreThan(t0.getT1().getLowPrice(), t0.getT2().getHighPrice());
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 且缩量", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity());
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口 且缩量百分之20", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "1.2"));
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口 且缩量 涨幅>7%", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && moreThan(t0.getPricePert(), "0.07");
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口 且缩量 且4%<涨幅＜7%", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && moreThan(t0.getPricePert(), "0.04")
                    && lessThan(t0.getPricePert(), "0.07");
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 且缩量 且0%<涨幅<4%", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), t0.getT1().getDealQuantity())
                    && moreThan(t0.getPricePert(), "0.0")
                    && lessThan(t0.getPricePert(), "0.04");
        }));


        STRATEGY_LIST.add(new StockStrategy("上升缺口 且涨幅成交比扩大", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity());
        }));

        STRATEGY_LIST.add(new StockStrategy("上升缺口 且涨幅成交比扩大 涨幅<7%", (StockDetail t0) -> {
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && moreThan(t0.getPertDivisionQuantity(), t0.getT1().getPertDivisionQuantity())
                    && lessThan(t0.getPricePert(), "0.07");
        }));


        STRATEGY_LIST.add(new StockStrategy("5日线 突破 10日线", (StockDetail t0) -> {
            return moreThan(t0.getFiveDayLine(), t0.getTenDayLine())
                    && lessThan(t0.getT1().getFiveDayLine(), t0.getT1().getTenDayLine());
        }));

        STRATEGY_LIST.add(new StockStrategy("5日线 突破 20日线", (StockDetail t0) -> {
            return moreThan(t0.getFiveDayLine(), t0.getTwentyDayLine())
                    && lessThan(t0.getT1().getFiveDayLine(), t0.getT1().getTwentyDayLine());
        }));

        STRATEGY_LIST.add(new StockStrategy("金叉", (StockDetail t0) -> {
            // 金叉日大于五日均量1.2倍
            boolean flag1 = moreThan(t0.getDealQuantity(), multiply(t0.getFiveDayDealQuantity(), "1.2"));

            //五日线 和 10日线都向上
            boolean flag1_1 = moreThan(t0.getFiveDayLine(), t0.getT1().getFiveDayLine())
                    && moreThan(t0.getTenDayLine(), t0.getT1().getTenDayLine());

            //5日线破10日线
            boolean flag2 = moreThan(t0.getFiveDayLine(), t0.getTenDayLine())
                    && lessThan(t0.getT1().getFiveDayLine(), t0.getT1().getTenDayLine());
            //较60日线涨幅小于25%

            boolean flag3 = lessThan(t0.getEndPrice(), multiply(t0.getSixtyDayLine(), "1.25"));
            return flag1 && flag1_1 && flag2 && flag3;
        }));
    }

    public static StockStrategy getStrategy(String name) {
        return STRATEGY_LIST.stream().filter(item -> Objects.equals(item.getStrategyName(), name)).findFirst().orElse(null);
    }

}
