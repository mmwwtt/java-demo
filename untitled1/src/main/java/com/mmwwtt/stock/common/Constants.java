package com.mmwwtt.stock.common;

import lombok.Data;

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
}
