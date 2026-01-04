package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 缩量三连阴后首阳（反向情绪）
 * 1. 连续3日阴线（isDown=true）
 * 2. 3日成交量逐日递减
 * 3. 第4日阳线（isUp=true）
 * 4. 第4日量>第3日量（放量确认）
 */
@Service
public class 缩量三连阴后首阳 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);
            // 1. 三连阴
            boolean threeBear = t1.getIsDown() && t2.getIsDown() && t3.getIsDown();

            // 2. 成交量逐日递减
            boolean volFade = t1.getDealQuantity().compareTo(t2.getDealQuantity()) < 0
                    && t2.getDealQuantity().compareTo(t3.getDealQuantity()) < 0;

            // 3. 第4日阳线
            boolean firstBull = t0.getIsUp();

            // 4. 第4日放量 > 第3日量
            boolean volUp = t0.getDealQuantity().compareTo(t1.getDealQuantity()) > 0;

            if (threeBear && volFade && firstBull && volUp) {
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
