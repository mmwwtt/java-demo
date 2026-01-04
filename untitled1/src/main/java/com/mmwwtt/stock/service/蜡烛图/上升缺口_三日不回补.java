package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 红三兵
 */
@Service
public class 上升缺口_三日不回补 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);
            StockDetail t4 = list.get(i + 4);

            //  缺口日： 最低点比前一天的最高点高，形成缺口
            //  3日不会回补， 随后3天都不破缺口日前一天的最低点
            boolean gapUp = t3.getLowPrice().compareTo(t4.getHighPrice()) > 0
                    && t2.getLowPrice().compareTo(t4.getHighPrice()) > 0
                    && t1.getLowPrice().compareTo(t4.getHighPrice()) > 0
                    && t0.getLowPrice().compareTo(t4.getHighPrice()) > 0;

            //  缺口日放量 ≥ 20 日均量
            boolean volBreak = t3.getDealQuantity().compareTo(t3.getFiveDayDealQuantity()) >= 0;
            if (gapUp && volBreak) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 61;
    }
}
