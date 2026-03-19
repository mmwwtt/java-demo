package com.mmwwtt.stock.test;

import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.impl.CalcCommonService;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonDataService.*;

/**
 * DFS结果验证
 */
@SpringBootTest
@Slf4j
public class DFSVerifyTest {

    @Resource
    private StrategyServiceImpl strategyService;

    @Resource
    private CalcCommonService calcCommonService;

    private static List<Strategy> strategies;

    @PostConstruct
    public void init() {
        String sql = "rise5_max_middle>0.11 and is_active=true";
        strategies = strategyService.getBySql(sql)
                .stream()
                .peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" "))))
                .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed())
                .toList();
    }

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260315", strategies, false, 1.2);
    }

    @Test
    @DisplayName("验证策略预测-5max")
    public void verifyPredictResByFiveMax() {
        verifyPredictResByFiveMaxDetail();
    }


    public void verifyPredictResByFiveMaxDetail() {
        List<Double> fiveMaxDateAvgList = new ArrayList<>();
        List<Double> fiveDateAvgList = new ArrayList<>();
        //日期    详情列表   详情-权重
        Map<String, Pair<List<Detail>, Map<Integer, Double>>> dataToDetailsMap = new ConcurrentHashMap<>();

        codeToDetailMap.entrySet().parallelStream().forEach(entry -> {
            for (Detail detail : entry.getValue()) {
                if (detail.getDealDate().compareTo(calcEndDate) <= 0) {
                    break;
                }
                if (Objects.isNull(detail.getRise5Max())
                        || Objects.isNull(detail.getT10())
                        || Objects.isNull(detail.getT10().getSixtyDayLine())
                        || moreThan(detail.getRise0(), 0.097)) {
                    continue;
                }
                for (Strategy strategyWin : strategies) {
                    List<StrategyEnum> strategyEnums = strategyWin.getStrategyCodeSet().stream()
                            .map(l1CodeToEnumMap::get).toList();
                    boolean res = strategyEnums.stream()
                            .allMatch(strategyEnum -> strategyEnum.getFilterFunc().apply(detail));
                    if (res) {
                        Pair<List<Detail>, Map<Integer, Double>> pair =
                                dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> Pair.of(new ArrayList<>(), new HashMap<>()));
                        //详情id-权重map
                        Map<Integer, Double> detailIdToWeightMap = pair.getRight();
                        if(!detailIdToWeightMap.containsKey(detail.getDetailId())) {
                            pair.getLeft().add(detail);
                        }
                        detailIdToWeightMap.merge(detail.getDetailId(), 1.0, (a, b) -> a + 1.0);
                    }
                }
            }
        });

        for (String date : predictDateList) {
            Pair<List<Detail>, Map<Integer, Double>> pair = dataToDetailsMap.getOrDefault(date, null);
            if (Objects.isNull(pair)) {
                continue;
            }
            List<Detail> details = pair.getLeft();
            Map<Integer, Double> detailIdToWeightMap = pair.getRight();
            if (CollectionUtils.isEmpty(details)) {
                continue;
            }
            List<Double> rise5Maxs = details
                    .stream().map(item -> item.getRise5Max() * detailIdToWeightMap.get(item.getDetailId()))
                    .toList();
            List<Double> rise5s = details.stream()
                    .map(item -> item.getRise5() * detailIdToWeightMap.get(item.getDetailId()))
                    .toList();

            double rise5MaxsDateAvg = divide(sum(rise5Maxs) , sum( detailIdToWeightMap.values().stream().toList()));
            double rise5sDateAvg = divide(sum(rise5s) , sum( detailIdToWeightMap.values().stream().toList()));
            log.info("日期：{}   五日平均涨幅：{}%   五日最高平均涨幅：{}%  \n",
                    date, String.format("%.3f", rise5sDateAvg * 100),String.format("%.3f", rise5MaxsDateAvg * 100));

            fiveMaxDateAvgList.add(rise5MaxsDateAvg);
            fiveDateAvgList.add(rise5sDateAvg);
        }
        log.info("平均5日最高涨幅 {}%", String.format("%.3f", getAverage(fiveMaxDateAvgList) * 100));
        log.info("平均5日涨幅 {}%", String.format("%.3f", getAverage(fiveDateAvgList) * 100));
    }
}
