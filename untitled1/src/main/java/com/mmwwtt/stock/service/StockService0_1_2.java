package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 当天是红
 */
@Service
public class StockService0_1_2 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            StockDetail stockDetail = list.get(i);
            StockDetail before1 = list.get(i + 1);
            StockDetail before2 = list.get(i + 2);
            if (stockDetail.getPricePert().compareTo(BigDecimal.ZERO) > 0
                    && before1.getPricePert().compareTo(BigDecimal.ZERO) > 0
                    && before2.getPricePert().compareTo(BigDecimal.ZERO) > 0) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s    %s", this.getClass().getSimpleName(), "红三兵");
    }

    public int getDayNum() {
        return 5;
    }
}
