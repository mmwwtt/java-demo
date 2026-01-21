package com.mmwwtt.stock.controller;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import com.mmwwtt.stock.service.StockGuiUitls;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.mmwwtt.stock.common.CommonUtils.*;

@RestController
@RequestMapping("/stock")
@SpringBootTest
public class StockController {

    @Resource
    private StockCalcService stockCalcService;

    @GetMapping("/calc1")
    public Boolean getCur1StockList() throws ExecutionException, InterruptedException {
        stockCalcService.startCalc1();
        return true;
    }

    @Test
    @DisplayName("根据所有策略计算胜率")
    @GetMapping("/calc2")
    public void startCalc2() throws ExecutionException, InterruptedException {
        stockCalcService.calcByStrategy(StockCalcService.STRATEGY_LIST);
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    @GetMapping("/calc3")
    public void startCalc3() throws ExecutionException, InterruptedException {
        StockStrategy strategy = new StockStrategy("test_"+getTimeStr(), (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return moreThan(t0.getLowShadowPert(), "0.6")
                    && t0.getIsRed()
                    && moreThan(t0.getAllLen(), "0.08")
                    && lessThan(t0.getEndPrice(), multiply(t0.getTenDayLine(), "0.9"));
        });
        Map<String, List<StockDetail>> resMap = stockCalcService.calcByStrategy(List.of(strategy));
        resMap.forEach((strategyName, resList) -> {
            resList.stream().filter(item -> item.getNext1().getIsDown()).limit(1000).forEach(item -> {
                try {
                    StockGuiUitls.genDetailImage(item, strategyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }


    @Test
    @DisplayName("测试单个策略-自定义")
    @GetMapping("/calc4")
    public void startCalc4() throws ExecutionException, InterruptedException {
        StockStrategy strategy = new StockStrategy("test_", (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            BigDecimal space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
            return moreThan(t0.getLowPrice(), t0.getT1().getHighPrice())
                    && lessThan(t0.getDealQuantity(), multiply(t0.getT1().getDealQuantity(), "0.8"))
                    && moreThan(space, "0.01");
        });
        stockCalcService.calcByStrategy(List.of(strategy));
    }


    @Test
    @DisplayName("测试单个策略")
    @GetMapping("/calc5")
    public void startCalc5() throws ExecutionException, InterruptedException {
        StockStrategy strategy = StockCalcService.getStrategy("");
        stockCalcService.calcByStrategy(List.of(strategy));
    }


    @Test
    @DisplayName("测试单个策略-大类")
    @GetMapping("/calc6")
    public void startCalc6() throws ExecutionException, InterruptedException {
        List<StockStrategy> strategyList = StockCalcService.getStrategyList("日内V反");
        stockCalcService.calcByStrategy(strategyList);
    }

}
