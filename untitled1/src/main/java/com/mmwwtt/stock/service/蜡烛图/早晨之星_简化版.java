package com.mmwwtt.stock.service.蜡烛图;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 早晨之星（简化版）
 * 1. 第1根大阴（实体≥2%且收盘<开盘）
 * 2. 第2根星线（isTenStar=true）
 * 3. 第3根大阳（实体≥2%且收盘>第1根开盘）
 * 4. 第3日放量（量>20日均量）
 */
@Service
public class 早晨之星_简化版 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            // 1. 大阴：实体≥2% 且为阴线
            boolean bigBear = t2.getEntityPert().compareTo(new BigDecimal("2")) >= 0 && t2.getIsDown();

            // 2. 星线
            boolean star = Boolean.TRUE.equals(t1.getIsTenStar());

            // 3. 大阳：实体≥2% 且收盘>第1根开盘（完全反包）
            boolean bigBull = t0.getEntityPert().compareTo(new BigDecimal("2")) >= 0 && t0.getIsUp()
                    && t0.getEndPrice().compareTo(t2.getStartPrice()) > 0;

            // 4. 第3日放量 > 20日均量
            boolean volUp = t0.getDealQuantity().compareTo(t0.getTwentyDayLine()) > 0;

            if (bigBear && star && bigBull && volUp) {
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
