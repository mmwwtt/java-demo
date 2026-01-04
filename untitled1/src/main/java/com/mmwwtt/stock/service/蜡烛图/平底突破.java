package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 平底突破：连续3日最低价差≤1% 且第4日放量向上突破
 */
@Service
public class 平底突破 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);


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

            if( flat && breakClose && volUp) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 15;
    }
}
