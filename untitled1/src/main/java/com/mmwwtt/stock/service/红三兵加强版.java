package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 红三兵加强版
 */
@Service
public class 红三兵加强版 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            // 1. 三连阳
            if (!t0.getIsUp() || !t1.getIsUp() || !t2.getIsUp()) continue;

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

            if( entityGrow && volGrow && above5 && bigEnough) {
                res.add(new OneRes(list.get(i - 1)));
            };
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 5;
    }
}
