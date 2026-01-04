package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 连续两天缩量且 当天是阳线
 */
@Service
public class StockService6_1 implements StockService {

    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            StockDetail before2 = list.get(i + 2);
            StockDetail before1 = list.get(i + 1);
            StockDetail stockDetail = list.get(i);
            if (stockDetail.getDealQuantity().compareTo(before1.getDealQuantity()) < 0
                    &&before1.getDealQuantity().compareTo(before2.getDealQuantity()) < 0
                    && stockDetail.getPricePert().compareTo(BigDecimal.ZERO) > 0) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s    %s", this.getClass().getSimpleName(), "连续两天缩量且 当天是阳线");
    }

    public int getDayNum() {
        return 5;
    }
}
