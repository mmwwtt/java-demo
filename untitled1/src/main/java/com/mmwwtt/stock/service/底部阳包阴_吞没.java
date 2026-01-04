package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 红三兵
 */
@Service
public class 底部阳包阴_吞没 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);

            // 1. 前阴后阳

            // 2. 实体完全包裹：今日开盘≤前日收盘 且 今日收盘≥前日开盘
            boolean cover = t0.getStartPrice().compareTo(t1.getEndPrice()) <= 0
                    && t0.getEndPrice().compareTo(t1.getStartPrice()) >= 0;

            // 3. 放量确认
            boolean volUp = t0.getDealQuantity().compareTo(t1.getDealQuantity()) > 0;

            if (t1.getIsDown() && t0.getIsUp() && cover && volUp) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 6;
    }
}
