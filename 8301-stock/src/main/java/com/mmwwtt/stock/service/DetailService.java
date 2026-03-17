package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.vo.DetailQueryVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface DetailService {

    /**
     * 获取指定代码的股票列表
     */
    List<Detail> getDetail(DetailQueryVO queryVO);

    /**
     * 获得股票 交易列表
     */
    Map<String, List<Detail>> getCodeToDetailMap(Integer limit) throws ExecutionException, InterruptedException;

    /**
     * 获得股票 当天交易数据
     */
    Map<String, Detail> getCodeToCurDetailMap(String curDate) throws ExecutionException, InterruptedException;

}
