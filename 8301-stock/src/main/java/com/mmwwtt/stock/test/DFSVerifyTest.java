package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUtils;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.impl.DetailServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.CommonDataService.*;
import static com.mmwwtt.stock.test.DFSTest.fieldEnum;

/**
 * DFS结果验证
 */
@SpringBootTest
@Slf4j
public class DFSVerifyTest {

    @Resource
    private StrategyServiceImpl strategyService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private CommonDataService commonDataService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    @Test
    @DisplayName("验证策略")
    public void verifyPredictResByFiveMax() throws ExecutionException, InterruptedException {
        List<String> sqlList = Arrays.asList(
                "rise5_max_middle > 0.03 and rise5_max_middle < 0.04",
                "rise5_max_middle > 0.03 and rise5_max_middle < 0.04 and is_active = true",
                "rise5_max_middle > 0.04 and rise5_max_middle < 0.05",
                "rise5_max_middle > 0.04 and rise5_max_middle < 0.05 and is_active = true",
                "rise5_max_middle > 0.05 and rise5_max_middle < 0.06",
                "rise5_max_middle > 0.05 and rise5_max_middle < 0.06 and is_active = true",
                "rise5_max_middle > 0.06 and rise5_max_middle < 0.07",
                "rise5_max_middle > 0.06 and rise5_max_middle < 0.07 and is_active = true",
                "rise5_max_middle > 0.07 and rise5_max_middle < 0.08",
                "rise5_max_middle > 0.07 and rise5_max_middle < 0.08 and is_active = true",
                "rise5_max_middle > 0.08 and rise5_max_middle < 0.09",
                "rise5_max_middle > 0.08 and rise5_max_middle < 0.09 and is_active = true",
                "rise5_max_middle > 0.09 and rise5_max_middle < 0.10",
                "rise5_max_middle > 0.09 and rise5_max_middle < 0.10 and is_active = true",
                "rise5_max_middle > 0.10 and rise5_max_middle < 0.11",
                "rise5_max_middle > 0.10 and rise5_max_middle < 0.11 and is_active = true",
                "rise5_max_middle > 0.11 and rise5_max_middle < 0.12",
                "rise5_max_middle > 0.11 and rise5_max_middle < 0.12 and is_active = true",
                "rise5_max_middle > 0.12 and rise5_max_middle < 0.13",
                "rise5_max_middle > 0.12 and rise5_max_middle < 0.13 and is_active = true",
                "rise5_max_middle > 0.13 and rise5_max_middle < 0.14",
                "rise5_max_middle > 0.13 and rise5_max_middle < 0.14 and is_active = true",
                "rise5_max_middle > 0.14 and rise5_max_middle < 0.15",
                "rise5_max_middle > 0.14 and rise5_max_middle < 0.15 and is_active = true",
                "rise5_max_middle > 0.15",
                "rise5_max_middle > 0.15 and rise5_middle*1.3 > rise4_middle",
                "rise5_max_middle > 0.15 and is_active = true",
                "rise5_max_middle > 0.16",
                "rise5_max_middle > 0.16 and is_active = true",
                "rise4_middle < rise5_middle and rise3_middle < rise4_middle and rise5_middle > 0.01 ",
                "rise4_middle < rise5_middle and rise3_middle < rise4_middle and rise5_middle > 0.01 and is_active = true");
        commonDataService.init();
        for (String sql : sqlList) {
            String newSql = sql + " and field_enum_code = '" + fieldEnum.getCode() + "'";
            log.info("\n\n");
            log.info("条件： {}", newSql);
            List<Strategy> strategies = strategyService.getBySql(newSql)
                    .stream()
                    .peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" "))))
                    .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed())
                    .toList();
            verifyPredictRes(strategies);
        }
    }


    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        commonDataService.init();
        String sql = "rise4_middle < rise5_middle and rise3_middle < rise4_middle and rise5_middle > 0.01 and is_active= true";
        String newSql = sql + " and field_enum_code = '" + fieldEnum.getCode() + "'";
        List<Strategy> strategies = strategyService.getBySql(newSql)
                .stream()
                .peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" "))))
                .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed())
                .toList();
        predict("20260403", strategies, false, 1.2);
    }


    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        commonDataService.init();
        buildImg(201281);
    }


    public void verifyPredictRes(List<Strategy> strategies, Map<String, Detail> detailMap) {
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
                for (Strategy strategy : strategies) {
                    List<Function<Detail, Boolean>> filterFuncs = strategy.getStrategyCodeSet().stream()
                            .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
                    boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                    if (res) {
                        Pair<List<Detail>, Map<Integer, Double>> pair =
                                dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> Pair.of(new ArrayList<>(), new ConcurrentHashMap<>()));
                        //详情id-权重map
                        Map<Integer, Double> detailIdToWeightMap = pair.getRight();
                        if (!detailIdToWeightMap.containsKey(detail.getDetailId())) {
                            pair.getLeft().add(detail);
                        }
                        detailIdToWeightMap.merge(detail.getDetailId(), 1d, (a, b) -> a + 1);
                    }
                }
            }
        });
        double rise1MaxDateAvgSum = 0;
        double rise1DateAvgSum = 0;
        double rise3MaxDateAvgSum = 0;
        double rise3DateAvgSum = 0;
        double rise5MaxDateAvgSum = 0;
        double rise5DateAvgSum = 0;
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
            double rise1MaxSum = 0;
            double rise1Sum = 0;
            double rise3MaxSum = 0;
            double rise3Sum = 0;
            double rise5MaxSum = 0;
            double rise5Sum = 0;
            for (Detail detail : details) {
                rise1MaxSum += detail.getRise1Max() * detailIdToWeightMap.get(detail.getDetailId());
                rise1Sum += detail.getRise1() * detailIdToWeightMap.get(detail.getDetailId());
                rise3MaxSum += detail.getRise3Max() * detailIdToWeightMap.get(detail.getDetailId());
                rise3Sum += detail.getRise3() * detailIdToWeightMap.get(detail.getDetailId());
                rise5MaxSum += detail.getRise5Max() * detailIdToWeightMap.get(detail.getDetailId());
                rise5Sum += detail.getRise5() * detailIdToWeightMap.get(detail.getDetailId());
            }
            Double weightSum = sum(detailIdToWeightMap.values().stream().toList());
            double rise1MaxDateAvg = divide(rise1MaxSum, weightSum);
            double rise1DateAvg = divide(rise1Sum, weightSum);
            double rise3MaxDateAvg = divide(rise3MaxSum, weightSum);
            double rise3DateAvg = divide(rise3Sum, weightSum);
            double rise5MaxDateAvg = divide(rise5MaxSum, weightSum);
            double rise5DateAvg = divide(rise5Sum, weightSum);
            log.info("\n 日期：{}\n   " +
                            "1日平均涨幅：{}%   1日最高平均涨幅：{}%\n" +
                            "3日平均涨幅：{}%   3日最高平均涨幅：{}%\n" +
                            "5日平均涨幅：{}%   5日最高平均涨幅：{}%\n",
                    date,
                    String.format("%.3f", rise1DateAvg * 100),
                    String.format("%.3f", rise1MaxDateAvg * 100),
                    String.format("%.3f", rise3DateAvg * 100),
                    String.format("%.3f", rise3MaxDateAvg * 100),
                    String.format("%.3f", rise5DateAvg * 100),
                    String.format("%.3f", rise5MaxDateAvg * 100));
            rise1MaxDateAvgSum += rise1MaxDateAvg;
            rise1DateAvgSum += rise1DateAvg;
            rise3MaxDateAvgSum += rise3MaxDateAvg;
            rise3DateAvgSum += rise3DateAvg;
            rise5MaxDateAvgSum += rise5MaxDateAvg;
            rise5DateAvgSum += rise5DateAvg;
        }
        if (isEquals(0d, rise1DateAvgSum)) {
            return;
        }
        log.info("平均1日最高涨幅 {}%", String.format("%.3f", rise1MaxDateAvgSum / predictDateList.size() * 100));
        log.info("平均1日涨幅 {}%", String.format("%.3f", rise1DateAvgSum / predictDateList.size() * 100));
        log.info("平均3日最高涨幅 {}%", String.format("%.3f", rise3MaxDateAvgSum / predictDateList.size() * 100));
        log.info("平均3日涨幅 {}%", String.format("%.3f", rise3DateAvgSum / predictDateList.size() * 100));
        log.info("平均5日最高涨幅 {}%", String.format("%.3f", rise5MaxDateAvgSum / predictDateList.size() * 100));
        log.info("平均5日涨幅 {}%", String.format("%.3f", rise5DateAvgSum / predictDateList.size() * 100));
    }

    private void buildImg(Integer strategyId) {
        Strategy strategy = strategyService.getById(strategyId);
        List<Detail> details = new ArrayList<>(1000);

        for (Object detailId : strategy.getDetailIdArray()) {
            details.add(detailArr[(Integer) detailId]);
        }
        log.info("开始绘制");
        for (Detail detail : details) {
            try {
                StockGuiUtils.genDetailImage(detail, strategy.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("绘制完成");
    }


    /**
     * 预测明日股票
     */
    public void predict(String curDate, List<Strategy> strategys, boolean isOnTime, double quantityMult) throws InterruptedException, ExecutionException {
        Map<Strategy, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<String, Detail> codeToDetailMap = detailService.getCodeToCurDetailMap(curDate);
        for (Strategy strategy : strategys) {
            List<Function<Detail, Boolean>> filterFuncs = Arrays.stream(strategy.getStrategyCode().split(" "))
                    .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        Detail detail = codeToDetailMap.get(stockCode);
                        if (Objects.isNull(detail) || Objects.isNull(detail.getT5())
                                || Objects.isNull(detail.getT5().getSixtyDayLine())
                                || moreThan(detail.getRise0(), 0.097)) {
                            continue;
                        }
                        if (isOnTime) {
                            detail.setDealQuantity(multiply(detail.getDealQuantity(), quantityMult));
                        }
                        boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                        if (res) {
                            double pert = detail.getRise0() != null ? detail.getRise0() : 0;
                            strategyToStockMap.computeIfAbsent(strategy, k -> Collections.synchronizedList(new ArrayList<>()))
                                    .add(stockCode + " " + pert);
                        }
                    }
                }, cpuThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        log.info("计算结束");
        String filePath = "src/main/resources/file/预测的股票.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Map<String, Long> stockToCntMap = strategyToStockMap.values().stream().flatMap(List::stream)
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<Strategy> resStrategies = strategyToStockMap.keySet().stream()
                    .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed()).toList();
            for (Strategy strategy : resStrategies) {
                List<String> resStockList = strategyToStockMap.get(strategy);
                String str = String.format("\n\n历史总数：%d  策略：%s \n5日最高中位数涨幅：%4f \n",
                        strategy.getDateCnt(), strategy.getName(), strategy.getRise5MaxMiddle());
                fos.write(str.getBytes());
                for (String s : resStockList) {
                    fos.write((s + "\n").getBytes());
                }
            }
            fos.write(("\n\n").getBytes());
            stockToCntMap.forEach((k, v) -> {
                try {
                    fos.write((k + "     :   " + v + "\n").getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ignored) {
        }

    }
}
