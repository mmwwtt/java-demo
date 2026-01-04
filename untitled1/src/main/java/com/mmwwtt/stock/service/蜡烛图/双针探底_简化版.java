package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 双针探底简化版：
 * 连续2根长下影（下影占比>60%）且收盘为阳线或小幅阴线（不能大阴）+
 * 第3日阳线上攻且收盘高于前两日最高
 */
@Service
public class 双针探底_简化版 implements StockService {


    //todo 没符合条件的数据
    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);

            // 1. 连续两根长下影：下影占比 > 60%
            boolean longShadow1 = t1.getLowShadowPert().compareTo(new BigDecimal("0.6")) > 0;
            boolean longShadow2 = t2.getLowShadowPert().compareTo(new BigDecimal("0.6")) > 0;

            // 2. 两根下影K线实体小（防止大阴棒）
            boolean smallBody1 = t1.getEntityPert().compareTo(new BigDecimal("2")) <= 0;
            boolean smallBody2 = t2.getEntityPert().compareTo(new BigDecimal("2")) <= 0;

            // 3. 第3日阳线上攻
            boolean up3 = t0.getIsUp();

            // 4. 第3日收盘 > 前两日最高（确认反转）
            BigDecimal maxHigh12 = t1.getHighPrice().max(t2.getHighPrice());
            boolean breakHigh = t0.getEndPrice().compareTo(maxHigh12) > 0;

            // 5. 放量确认（今日量 > 10日均量）
            boolean volUp = t0.getDealQuantity().compareTo(t0.getTenDayLine()) > 0;

            if(longShadow1 && longShadow2 && smallBody1 && smallBody2 &&
                    up3 && breakHigh && volUp) {
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
