package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 红三兵
 */
@Service
public class 缩量回踩10日线后再放量 implements StockService {


    //todo 没符合条件的数据
    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);

            /* 1. 连续 2-3 日缩量（量逐日递减） */
            boolean shrink = t1.getDealQuantity().compareTo(t2.getDealQuantity()) < 0
                    && t2.getDealQuantity().compareTo(t3.getDealQuantity()) < 0
                    && t1.getDealQuantity().compareTo(t1.getTenDayLine().multiply(BigDecimal.valueOf(0.8))) < 0;

            /* 2. 回踩 10 日线：曾触碰但未跌破（最低 ≤ 10 日线 ≤ 最高） */
            boolean touch10 = t1.getLowPrice().compareTo(t1.getTenDayLine()) <= 0
                    && t1.getHighPrice().compareTo(t1.getTenDayLine()) >= 0;

            /* 3. 今日放量阳线 */
            boolean upToday = t0.getIsUp()
                    && t0.getDealQuantity().compareTo(t1.getDealQuantity().multiply(BigDecimal.valueOf(1.5))) > 0;

            /* 4. 今日收盘重新站上 10 日线 */
            boolean above10 = t0.getEndPrice().compareTo(t0.getTenDayLine()) > 0;

            if (shrink && touch10 && upToday && above10) {
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
