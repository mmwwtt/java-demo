package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 放量突破 20 日最高（动量）
 * 当日量>20 日均量×1.8
 */
@Service
public class 放量突破20日最高 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            // 1. 收盘突破 20 日最高
            boolean priceBreak = t0.getEndPrice().compareTo(t0.getTwentyDayHigh()) > 0;

            // 2. 放量 > 20 日均量 × 1.8
            boolean volBreak = t0.getDealQuantity()
                    .compareTo(t0.getTwentyDayLine()
                            .multiply(BigDecimal.valueOf(1.8))) > 0;

            if (priceBreak && volBreak) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 25;
    }
}
