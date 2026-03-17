package com.mmwwtt.stock.test;

import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CalcCommonService;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.*;

/**
 * DFS结果验证
 */
@SpringBootTest
@Slf4j
public class DFSVerifyTest {

    @Resource
    private StrategyWinServiceImpl strategyWinService;

    @Resource
    private CalcCommonService calcCommonService;

    private static List<StrategyWin> winList;

    @PostConstruct
    public void init() {
        String sql = "rise5_max_middle>0.14  and rise5_max_middle<15 ";
        winList = strategyWinService.getStrategyWin(sql)
                .stream()
                .peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" "))))
                .sorted(Comparator.comparing(StrategyWin::getRise5MaxMiddle).reversed())
                .toList();
    }

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260315", winList, false, 1.2);
    }

    @Test
    @DisplayName("验证策略预测-5max")
    public void verifyPredictResByFiveMax() {
        verifyPredictResByFiveMaxDetail();
    }


    public void verifyPredictResByFiveMaxDetail() {
        List<Double> fiveMaxDateAvgList = new ArrayList<>();
        List<Double> fiveDateAvgList = new ArrayList<>();
        Map<String, List<StockDetail>> dataToDetailsMap = new ConcurrentHashMap<>();

        codeToDetailMap.entrySet().parallelStream().forEach(entry -> {
            for (StockDetail detail : entry.getValue()) {
                if (detail.getDealDate().compareTo(calcEndDate) <= 0) {
                    break;
                }
                if (Objects.isNull(detail.getNext5MaxPricePert())
                        || Objects.isNull(detail.getT10())
                        || Objects.isNull(detail.getT10().getSixtyDayLine())
                        || moreThan(detail.getPricePert(), 0.097)) {
                    continue;
                }
                for (StrategyWin strategyWin : winList) {
                    List<StrategyEnum> strategyEnums = strategyWin.getStrategyCodeSet().stream()
                            .map(StrategyEnum.codeToEnumMap::get).toList();
                    boolean res = strategyEnums.stream()
                            .allMatch(strategyEnum -> strategyEnum.getRunFunc().apply(detail));
                    if (res) {
                        dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> new ArrayList<>()).add(detail);
                        break;
                    }
                }
            }
        });

        for (String date : predictDateList) {
            List<StockDetail> resList = dataToDetailsMap.getOrDefault(date, Collections.emptyList());
            if (CollectionUtils.isEmpty(resList)) {
                continue;
            }
            List<Double> next5Maxs = resList.stream().map(StockDetail::getNext5MaxPricePert).toList();
            List<Double> next5s = resList.stream()
                    .map(item -> getRise(item.getNext5().getEndPrice(), item.getEndPrice()))
                    .toList();

            double fiveMaxDateAvg = getAverage(next5Maxs);
            fiveMaxDateAvgList.add(fiveMaxDateAvg);
            fiveDateAvgList.add(getAverage(next5s));
            log.info("日期：{}    涨幅：{}%\n", date, String.format("%.3f", fiveMaxDateAvg * 100));
        }
        log.info("平均5日最高涨幅 {}%", String.format("%.3f", getAverage(fiveMaxDateAvgList) * 100));
        log.info("平均5日涨幅 {}%", String.format("%.3f", getAverage(fiveDateAvgList) * 100));
    }
}
