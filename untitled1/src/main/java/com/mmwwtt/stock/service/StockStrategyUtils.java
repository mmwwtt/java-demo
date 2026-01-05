package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class StockStrategyUtils {
    public final static List<Pair<String, Function<StockDetail, Boolean>>> STRATEGY_LIST = new ArrayList<>();

    static{
        STRATEGY_LIST.add(Pair.of("十字星", StockStrategyUtils::strategy0));
        STRATEGY_LIST.add(Pair.of("二连红", StockStrategyUtils::strategy1));
        STRATEGY_LIST.add(Pair.of("啥也不做", StockStrategyUtils::strategy2));
        STRATEGY_LIST.add(Pair.of("当天是红", StockStrategyUtils::strategy3));
        STRATEGY_LIST.add(Pair.of("当天是绿", StockStrategyUtils::strategy4));
        STRATEGY_LIST.add(Pair.of("比前一天放量", StockStrategyUtils::strategy5));
    }

    public static boolean strategy0(StockDetail stockDetail) {
        return stockDetail.getIsTenStar();
    }

    public static boolean strategy1(StockDetail stockDetail) {
        if(Objects.isNull(stockDetail.getT1())) {
            return false;
        }
        return stockDetail.getIsUp() && stockDetail.getT1().getIsUp();
    }

    public static boolean strategy2(StockDetail stockDetail) {
       return true;
    }

    public static boolean strategy3(StockDetail stockDetail) {
        return stockDetail.getIsUp();
    }

    public static boolean strategy4(StockDetail stockDetail) {
        return stockDetail.getIsDown();
    }

    public static boolean strategy5(StockDetail stockDetail) {
        if(Objects.isNull(stockDetail.getT1())) {
            return false;
        }
        return stockDetail.getDealQuantity().compareTo(stockDetail.getT1().getDealQuantity()) > 0;
    }
}
