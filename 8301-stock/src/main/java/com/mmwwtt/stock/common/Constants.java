package com.mmwwtt.stock.common;

import com.mmwwtt.stock.entity.StockStrategy;
import lombok.Data;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
public class Constants {

    /**
     * 股票
     */
    public static final String STOCK = "stock";

    /**
     * 股票代码
     */
    public static final String STOCK_CODE = "stockCode";

    /**
     * 分时级别
     */
    public static final String TIME_LEVEL="timeLevel";

    /**
     * 除权方式
     */
    public static final String EXCLUDE_RIGHT = "excludeRight";

    /**
     * 通行证
     */
    public static final String LICENCE= "licence";

    /**
     * 最大条数
     */
    public static final String MAX_SIZE = "maxSize";

    /**
     * 开始日期
     */
    public static final String START_DATA = "startData";

    /**
     * 结束日期
     */
    public static final String END_DATA = "endData";

    /**
     * 必盈的通行证licence
     */
    public static final String BI_YING_LICENCE = "868DA37C-9636-4050-9DC6-443B40E82AFC";

    /**
     * 获得股票列表的url
     */
    public static final String STOCK_LIST_URL = "http://api.biyingapi.com/hslt/list/{licence}";

    /**
     * 获取历史数据的url
     */
    public static final String HISTORY_DATA_URL= "https://api.biyingapi.com/hsstock/history/{stockCode}/{timeLevel}/" +
            "{excludeRight}/{licence}?st={startData}&et={endData}&lt={maxSize}";

    /**
     * 获取实时数据的url(交易中)
     */
    public static final String ON_TIME_DATA_URL= "https://api.biyingapi.com/hsrl/ssjy/{stockCode}/{licence}";

    public static final String REAL_TIME_URL = "https://api.biyingapi.com/hsstock/real/time/{stockCode}/{licence}";

    public static final Double TOLERANCE = 0.00001;

    public static final MathContext MC = new MathContext(4, RoundingMode.HALF_UP);




    public static final List<StockStrategy> STRATEGY_LIST = new ArrayList<>();

    public static StockStrategy getStrategy(String name) {
        return STRATEGY_LIST.stream().filter(item -> item.getStrategyName().startsWith(name)).findFirst().orElse(null);
    }

    public static List<StockStrategy> getStrategyList(String name) {
        return STRATEGY_LIST.stream().filter(item -> item.getStrategyName().startsWith(name)).toList();
    }
}
