package com.mmwwtt.stock.service.interfaces;

import com.mmwwtt.stock.entity.Detail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface DetailService {
    /**
     * 获得股票 当天交易数据
     */
    Map<String, Detail> getCodeToCurDetailMap(String curDate) throws ExecutionException, InterruptedException;

    /**
     * 获取指定代码的股票列表
     */
    List<Detail> getBySql(String sql);

}
