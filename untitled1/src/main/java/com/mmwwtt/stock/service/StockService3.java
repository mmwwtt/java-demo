package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 涨幅成交比 比前一天大
 */
@Service
public class StockService3 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            StockDetail before = list.get(i + 1);
            StockDetail stockDetail = list.get(i);
            if (stockDetail.getPertDivisionQuantity().compareTo(before.getPertDivisionQuantity()) > 0) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s    %s", this.getClass().getSimpleName(), "涨幅成交比 比前一天大");
    }

    public int getDayNum() {
        return 5;
    }
}
