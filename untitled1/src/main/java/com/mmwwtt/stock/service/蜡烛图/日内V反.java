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
 * 日内 V 反（长下影+阳实体）
 * 条件：
 *  1. 下影占比 > 65%
 *  2. 实体占比 ≥ 1% 且为阳线
 *  3. 全天振幅 ≥ 4%
 *  4. 成交量 > 10 日均量
 */
@Service
public class 日内V反 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i < list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            StockDetail t2 = list.get(i + 2);
            StockDetail t3 = list.get(i + 3);
            boolean longShadow = t0.getLowShadowPert().compareTo(new BigDecimal("0.65")) > 0;

            // 2. 阳实体且≥1%
            boolean solidBull = t0.getIsUp() &&
                    t0.getEntityPert().compareTo(new BigDecimal("1")) >= 0;

            // 3. 振幅 ≥ 4%
            BigDecimal amplitude = t0.getHighPrice()
                    .subtract(t0.getLowPrice())
                    .divide(t0.getLowPrice(), 4, RoundingMode.HALF_UP);
            boolean highWave = amplitude.compareTo(new BigDecimal("0.04")) >= 0;

            // 4. 放量
            boolean volUp = t0.getDealQuantity().compareTo(t0.getTenDayLine()) > 0;

            if (longShadow && solidBull && highWave && volUp) {
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
