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
public class 缩量十字星_次日放量阳线_启明星变体 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);

            // 1. 前一日缩量十字星
            boolean doji = t1.getIsTenStar();
            boolean shrink = t1.getDealQuantity()
                    .compareTo(t1.getFiveDayDealQuantity().multiply(BigDecimal.valueOf(0.6))) < 0; // < 5日均量*0.6

            // 2. 当日放量阳线
            boolean up = t0.getIsUp();
            boolean volUp = t0.getDealQuantity()
                    .compareTo(t1.getDealQuantity().multiply(BigDecimal.valueOf(1.5))) > 0; // > 前日*1.5

            // 3. 收盘吞噬前日最高（确认反转）
            boolean cover = t0.getEndPrice().compareTo(t1.getHighPrice()) > 0;

            if (doji && shrink && up && volUp && cover) {
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
