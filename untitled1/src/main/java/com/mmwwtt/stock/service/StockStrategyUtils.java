package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class StockStrategyUtils {
    public final static List<Pair<String, Function<StockDetail, Boolean>>> STRATEGY_LIST = new ArrayList<>();

    static {
        STRATEGY_LIST.add(Pair.of("十字星", StockStrategyUtils::strategy0));
        STRATEGY_LIST.add(Pair.of("二连红", StockStrategyUtils::strategy1));
        STRATEGY_LIST.add(Pair.of("啥也不做", StockStrategyUtils::strategy2));
        STRATEGY_LIST.add(Pair.of("当天是红", StockStrategyUtils::strategy3));
        STRATEGY_LIST.add(Pair.of("当天是绿", StockStrategyUtils::strategy4));
        STRATEGY_LIST.add(Pair.of("比前一天放量", StockStrategyUtils::strategy5));
        STRATEGY_LIST.add(Pair.of("连续两天缩量且 当天是阳线", StockStrategyUtils::strategy6));
        STRATEGY_LIST.add(Pair.of("缩量红", StockStrategyUtils::strategy7));
        STRATEGY_LIST.add(Pair.of("缩量绿", StockStrategyUtils::strategy8));
        STRATEGY_LIST.add(Pair.of("涨幅成交比扩大 缩量 二连红", StockStrategyUtils::strategy9));
        STRATEGY_LIST.add(Pair.of("涨幅成交比扩大 缩量红", StockStrategyUtils::strategy10));
        STRATEGY_LIST.add(Pair.of("涨幅成交比扩大 缩量", StockStrategyUtils::strategy11));
        STRATEGY_LIST.add(Pair.of("涨幅成交比扩大", StockStrategyUtils::strategy12));

        STRATEGY_LIST.add(Pair.of("上升缺口 成交量超过5日线", StockStrategyUtils::strategy13));
        STRATEGY_LIST.add(Pair.of("上升缺口 成交量超过5日线 三日不回补", StockStrategyUtils::strategy14));
        STRATEGY_LIST.add(Pair.of("上升缺口 成交量超过5日线 三日不回补 5日线>20日线", StockStrategyUtils::strategy15));
        STRATEGY_LIST.add(Pair.of("上升缺口 成交量超过5日线 三日不回补 5日线>20日线 涨幅成交比扩大 二连红", StockStrategyUtils::strategy16));
        STRATEGY_LIST.add(Pair.of("双针探底_简化版", StockStrategyUtils::strategy17));
        STRATEGY_LIST.add(Pair.of("红三兵", StockStrategyUtils::strategy18));
        STRATEGY_LIST.add(Pair.of("红三兵加强版", StockStrategyUtils::strategy19));
        STRATEGY_LIST.add(Pair.of("放量突破20日最高", StockStrategyUtils::strategy20));
        STRATEGY_LIST.add(Pair.of("平底突破", StockStrategyUtils::strategy21));
        STRATEGY_LIST.add(Pair.of("底部阳包阴", StockStrategyUtils::strategy22));
        STRATEGY_LIST.add(Pair.of("日内V反", StockStrategyUtils::strategy23));
        STRATEGY_LIST.add(Pair.of("早晨之星简化版", StockStrategyUtils::strategy24));
        STRATEGY_LIST.add(Pair.of("缩量3连阴后首阳", StockStrategyUtils::strategy25));
        STRATEGY_LIST.add(Pair.of("缩量十字星 次日放量阳", StockStrategyUtils::strategy26));
        STRATEGY_LIST.add(Pair.of("缩量回踩10日线后放量", StockStrategyUtils::strategy27));

    }

    public static boolean strategy0(StockDetail t0) {
        return t0.getIsTenStar();
    }


    public static boolean strategy1(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getIsUp() && t0.getT1().getIsUp();
    }


    public static boolean strategy2(StockDetail t0) {
        return true;
    }


    public static boolean strategy3(StockDetail t0) {
        return t0.getIsUp();
    }


    public static boolean strategy4(StockDetail t0) {
        return t0.getIsDown();
    }


    public static boolean strategy5(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) > 0;
    }


    public static boolean strategy6(StockDetail t0) {
        if (Objects.isNull(t0.getT1()) || Objects.isNull(t0.getT2())) {
            return false;
        }
        return t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getT1().getDealQuantity().compareTo(t0.getT2().getDealQuantity()) < 0
                && t0.getPricePert().compareTo(BigDecimal.ZERO) > 0;
    }


    public static boolean strategy7(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getIsUp();
    }


    public static boolean strategy8(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getIsDown();
    }


    public static boolean strategy9(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getPertDivisionQuantity().compareTo(t0.getT1().getPertDivisionQuantity()) > 0
                && t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getIsUp()
                && t0.getT1().getIsUp();
    }


    public static boolean strategy10(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getPertDivisionQuantity().compareTo(t0.getT1().getPertDivisionQuantity()) > 0
                && t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getIsUp();
    }


    public static boolean strategy11(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getPertDivisionQuantity().compareTo(t0.getT1().getPertDivisionQuantity()) > 0
                && t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0;
    }


    public static boolean strategy12(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getPertDivisionQuantity().compareTo(t0.getT1().getPertDivisionQuantity()) > 0;
    }


    public static boolean strategy13(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        return t0.getLowPrice().compareTo(t0.getT1().getHighPrice()) > 0
                && t0.getDealQuantity().compareTo(t0.getFiveDayDealQuantity()) >= 0;
    }


    public static boolean strategy14(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())
                || Objects.isNull(t0.getT4())) {
            return false;
        }
        return t0.getT3().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT2().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT1().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getDealQuantity().compareTo(t0.getFiveDayDealQuantity()) > 0;
    }


    public static boolean strategy15(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())
                || Objects.isNull(t0.getT4())) {
            return false;
        }
        return t0.getT3().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT2().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT1().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getDealQuantity().compareTo(t0.getFiveDayDealQuantity()) > 0
                && t0.getFiveDayDealQuantity().compareTo(t0.getTwentyDayDealQuantity()) > 0;
    }


    public static boolean strategy16(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())
                || Objects.isNull(t0.getT4())) {
            return false;
        }
        return t0.getT3().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT2().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getT1().getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getLowPrice().compareTo(t0.getT4().getHighPrice()) > 0
                && t0.getDealQuantity().compareTo(t0.getFiveDayDealQuantity()) > 0
                && t0.getFiveDayDealQuantity().compareTo(t0.getTwentyDayDealQuantity()) > 0
                && t0.getPertDivisionQuantity().compareTo(t0.getT1().getPertDivisionQuantity()) > 0
                && t0.getDealQuantity().compareTo(t0.getT1().getDealQuantity()) < 0
                && t0.getIsUp() && t0.getT1().getIsUp();
    }


    /**
     * 双针探底简化版： 连续2根长下影（下影占比>60%）且收盘为阳线或小幅阴线（不能大阴）+ 第3日阳线上攻且收盘高于前两日最高
     */
    public static boolean strategy17(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        // 1. 连续两根长下影：下影占比 > 60%
        boolean longShadow1 = t1.getLowShadowPert().compareTo(new BigDecimal("0.6")) > 0;
        boolean longShadow2 = t2.getLowShadowPert().compareTo(new BigDecimal("0.6")) > 0;

        // 2. 两根下影K线实体小（防止大阴棒）
        boolean smallBody1 = t1.getEntityPert().compareTo(new BigDecimal("0.02")) <= 0;
        boolean smallBody2 = t2.getEntityPert().compareTo(new BigDecimal("0.02")) <= 0;

        // 4. 第3日收盘 > 前两日最高（确认反转）
        BigDecimal maxHigh12 = t1.getHighPrice().max(t2.getHighPrice());
        boolean breakHigh = t0.getEndPrice().compareTo(maxHigh12) > 0;

        // 5. 放量确认（今日量 > 10日均量）
        boolean volUp = t0.getDealQuantity().compareTo(t0.getTenDayLine()) > 0;
        return longShadow1 && longShadow2 && smallBody1 && smallBody2
                && t0.getIsUp() && breakHigh && volUp;
    }


    public static boolean strategy18(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())) {
            return false;
        }
        return t0.getIsUp() && t0.getT1().getIsUp() && t0.getT2().getIsUp()
                && t0.getT1().getEntityLen().compareTo(t0.getT2().getEntityLen()) > 0
                && t0.getEntityLen().compareTo(t0.getT1().getEntityLen()) > 0;
    }


    public static boolean strategy19(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        // 2. 实体逐级放大
        boolean entityGrow = t1.getEntityLen().compareTo(t2.getEntityLen()) > 0
                && t0.getEntityLen().compareTo(t1.getEntityLen()) > 0;

        // 3. 成交量逐级放大
        boolean volGrow = t0.getDealQuantity().compareTo(t1.getDealQuantity()) > 0
                && t1.getDealQuantity().compareTo(t2.getDealQuantity()) > 0;

        // 4. 最新收盘站上 5 日线
        boolean above5 = t0.getEndPrice().compareTo(t0.getFiveDayLine()) > 0;

        // 5. 第三根（最新）实体占比 ≥ 1%
        boolean bigEnough = t0.getPricePert().compareTo(new BigDecimal("0.01")) >= 0;

        return t0.getIsUp() && t0.getT1().getIsUp() && t0.getT2().getIsUp()
                && t0.getT1().getEntityLen().compareTo(t0.getT2().getEntityLen()) > 0
                && t0.getEntityLen().compareTo(t0.getT1().getEntityLen()) > 0
                && entityGrow && volGrow && above5 && bigEnough;
    }


    public static boolean strategy20(StockDetail t0) {

        // 1. 收盘突破 20 日最高
        boolean priceBreak = t0.getEndPrice().compareTo(t0.getTwentyDayHigh()) > 0;

        // 2. 放量 > 20 日均量 × 1.8
        boolean volBreak = t0.getDealQuantity()
                .compareTo(t0.getTwentyDayLine()
                        .multiply(BigDecimal.valueOf(1.8))) > 0;

        return priceBreak && volBreak;
    }

    public static boolean strategy21(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        StockDetail t3 = t0.getT3();

        // 1. 连续3日最低价差 ≤ 1%
        BigDecimal maxLow = t1.getLowPrice().max(t2.getLowPrice()).max(t3.getLowPrice());
        BigDecimal minLow = t1.getLowPrice().min(t2.getLowPrice()).min(t3.getLowPrice());
        boolean flat = maxLow.subtract(minLow)
                .divide(minLow, 4, RoundingMode.HALF_UP)
                .compareTo(new BigDecimal("0.01")) <= 0;

        // 2. 今日收盘 > 3 日最高收盘价（突破）
        BigDecimal maxClose3 = t1.getEndPrice().max(t2.getEndPrice()).max(t3.getEndPrice());
        boolean breakClose = t0.getEndPrice().compareTo(maxClose3) > 0;

        // 3. 今日放量 > 10 日均量（确认）
        boolean volUp = t0.getDealQuantity().compareTo(t0.getTenDayLine()) > 0;

        return flat && breakClose && volUp;
    }

    public static boolean strategy22(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        StockDetail t1 = t0.getT1();

        // 1. 前阴后阳

        // 2. 实体完全包裹：今日开盘≤前日收盘 且 今日收盘≥前日开盘
        boolean cover = t0.getStartPrice().compareTo(t1.getEndPrice()) <= 0
                && t0.getEndPrice().compareTo(t1.getStartPrice()) >= 0;

        // 3. 放量确认
        boolean volUp = t0.getDealQuantity().compareTo(t1.getDealQuantity()) > 0;

        return t1.getIsDown() && t0.getIsUp() && cover && volUp;
    }

    public static boolean strategy23(StockDetail t0) {
        boolean longShadow = t0.getLowShadowPert().compareTo(new BigDecimal("0.65")) > 0;

        // 2. 阳实体且≥1%
        boolean solidBull = t0.getIsUp() &&
                t0.getEntityPert().compareTo(new BigDecimal("1")) >= 0;

        // 3. 振幅 ≥ 4%
        BigDecimal amplitude = t0.getHighPrice()
                .subtract(t0.getLowPrice())
                .divide(t0.getLowPrice(), 4, RoundingMode.HALF_UP);
        boolean highWave = amplitude.compareTo(new BigDecimal("0.04")) >= 0;

        // 4. 放量
        boolean volUp = t0.getDealQuantity().compareTo(t0.getTenDayLine()) > 0;

        return longShadow && solidBull && highWave && volUp;
    }

    public static boolean strategy24(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        // 1. 大阴：实体≥2% 且为阴线
        boolean bigBear = t2.getEntityPert().compareTo(new BigDecimal("2")) >= 0 && t2.getIsDown();

        // 3. 大阳：实体≥2% 且收盘>第1根开盘（完全反包）
        boolean bigBull = t0.getEntityPert().compareTo(new BigDecimal("2")) >= 0 && t0.getIsUp()
                && t0.getEndPrice().compareTo(t2.getStartPrice()) > 0;

        // 4. 第3日放量 > 20日均量
        boolean volUp = t0.getDealQuantity().compareTo(t0.getTwentyDayLine()) > 0;

        return bigBear && t1.getIsTenStar() && bigBull && volUp;
    }

    public static boolean strategy25(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        StockDetail t3 = t0.getT3();
        // 1. 三连阴
        boolean threeBear = t1.getIsDown() && t2.getIsDown() && t3.getIsDown();

        // 2. 成交量逐日递减
        boolean volFade = t1.getDealQuantity().compareTo(t2.getDealQuantity()) < 0
                && t2.getDealQuantity().compareTo(t3.getDealQuantity()) < 0;

        // 4. 第4日放量 > 第3日量
        boolean volUp = t0.getDealQuantity().compareTo(t1.getDealQuantity()) > 0;

        return threeBear && volFade && t0.getIsUp() && volUp;
    }


    public static boolean strategy26(StockDetail t0) {
        if (Objects.isNull(t0.getT1())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        // 1. 前一日缩量十字星
        boolean shrink = t1.getDealQuantity()
                .compareTo(t1.getFiveDayDealQuantity().multiply(BigDecimal.valueOf(0.6))) < 0; // < 5日均量*0.6

        // 2. 当日放量阳线
        boolean volUp = t0.getDealQuantity()
                .compareTo(t1.getDealQuantity().multiply(BigDecimal.valueOf(1.5))) > 0; // > 前日*1.5

        // 3. 收盘吞噬前日最高（确认反转）
        boolean cover = t0.getEndPrice().compareTo(t1.getHighPrice()) > 0;

        return t1.getIsTenStar() && shrink && t0.getIsUp() && volUp && cover;
    }


    public static boolean strategy27(StockDetail t0) {
        if (Objects.isNull(t0.getT1())
                || Objects.isNull(t0.getT2())
                || Objects.isNull(t0.getT3())) {
            return false;
        }
        StockDetail t1 = t0.getT1();
        StockDetail t2 = t0.getT2();
        StockDetail t3 = t0.getT3();
        /* 1. 连续 2-3 日缩量（量逐日递减） */
        boolean shrink = t1.getDealQuantity().compareTo(t2.getDealQuantity()) < 0
                && t2.getDealQuantity().compareTo(t3.getDealQuantity()) < 0
                && t1.getDealQuantity().compareTo(t1.getTenDayLine().multiply(BigDecimal.valueOf(0.8))) < 0;

        /* 2. 回踩 10 日线：曾触碰但未跌破（最低 ≤ 10 日线 ≤ 最高） */
        boolean touch10 = t1.getLowPrice().compareTo(t1.getTenDayLine()) <= 0
                && t1.getHighPrice().compareTo(t1.getTenDayLine()) >= 0;

        /* 3. 今日放量阳线 */
        boolean upToday = t0.getIsUp()
                && t0.getDealQuantity().compareTo(t1.getDealQuantity().multiply(BigDecimal.valueOf(1.5))) > 0;

        /* 4. 今日收盘重新站上 10 日线 */
        boolean above10 = t0.getEndPrice().compareTo(t0.getTenDayLine()) > 0;

        return shrink && touch10 && upToday && above10;
    }
}
