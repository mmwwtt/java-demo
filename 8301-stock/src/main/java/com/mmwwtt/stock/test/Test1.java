package com.mmwwtt.stock.test;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CalcCommonService;
import com.mmwwtt.stock.service.impl.CommonService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static com.mmwwtt.stock.common.CommonUtils.add;
import static com.mmwwtt.stock.common.CommonUtils.divide;

@SpringBootTest
public class Test1 {

    @Resource
    private CommonService commonService;
    @Resource
    private CalcCommonService calcCommonService;

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260213", false, 1.2);
    }

    @Test
    @DisplayName("验证策略预测")
    public void verifyPredictRes() throws InterruptedException, ExecutionException {
        Map<String, Map<StrategyWin, List<StockDetail>>> resMap = calcCommonService.verifyPredictRes();
        resMap.forEach((date, map) -> {
            System.out.println("\n\n日期：" + date + "\n");
            int all = 0;
            int win = 0;
            for (Map.Entry<StrategyWin, List<StockDetail>> entry : map.entrySet()) {
                List<StockDetail> details = entry.getValue();
                all += details.size();
                win += (int) details.stream().filter(item -> item.getNext1().getIsUp()).count();
            }
            System.out.println(divide(win, all));
        });
        return;
    }

    @Test
    @DisplayName("验证策略预测-5max")
    public void verifyPredictResByFiveMax() throws InterruptedException, ExecutionException {
        Map<String, Map<StrategyWin, List<StockDetail>>> resMap = calcCommonService.verifyPredictRes();
        resMap.forEach((date, map) -> {
            System.out.println("\n\n日期：" + date + "\n");
            int all = 0;
            AtomicReference<BigDecimal> count = new AtomicReference<>(BigDecimal.ZERO);
            for (Map.Entry<StrategyWin, List<StockDetail>> entry : map.entrySet()) {
                List<StockDetail> details = entry.getValue();
                all += details.size();
                details.stream().filter(item -> Objects.nonNull(item.getNext5MaxPricePert()))
                        .forEach(item -> count.set(add(count.get(), item.getNext5MaxPricePert())));
            }
            System.out.println(divide(count.get(), all));
        });
        return;
    }


    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        commonService.buildStrateResultLevel1();
    }
}
