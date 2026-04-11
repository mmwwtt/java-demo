package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUtils;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Query;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.impl.DetailServiceImpl;
import com.mmwwtt.stock.service.impl.QueryServiceImpl;
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

    @Resource
    private QueryServiceImpl queryService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    @Test
    @DisplayName("验证策略")
    public void verifyPredict() throws ExecutionException, InterruptedException {
        List<String> baseSqlList = Arrays.asList("1=1", "rise3_middle < rise4_middle  and rise4_middle < rise5_middle", "rise1_middle < rise3_middle and rise3_middle < rise5_middle", "rise1_middle < rise2_middle and rise2_middle < rise3_middle  " + "and rise3_middle < rise4_middle and rise4_middle < rise5_middle", "rise3_middle > rise5_middle", "rise5_middle > rise10_middle");
        List<String> rangeList = Arrays.asList(" and  0.01  <rise5_middle  and  rise5_middle <0.02", " and  0.02  <rise5_middle  and  rise5_middle <0.03", " and  0.03  <rise5_middle  and  rise5_middle <0.04", " and  0.04  <rise5_middle  and  rise5_middle <0.05", " and  0.05  <rise5_middle  and  rise5_middle <0.06", " and  0.06  <rise5_middle  and  rise5_middle <0.07", " and  0.07  <rise5_middle  and  rise5_middle <0.08", " and  0.08  <rise5_middle  and  rise5_middle <0.09", " and  0.09  <rise5_middle  and  rise5_middle <0.10", " and  0.10  <rise5_middle", " and  0.01  <rise3_middle  and  rise3_middle <0.02", " and  0.02  <rise3_middle  and  rise3_middle <0.03", " and  0.03  <rise3_middle  and  rise3_middle <0.04", " and  0.04  <rise3_middle  and  rise3_middle <0.05", " and  0.05  <rise3_middle  and  rise3_middle <0.06", " and  0.06  <rise3_middle  and  rise3_middle <0.07", " and  0.07  <rise3_middle  and  rise3_middle <0.08", " and  0.08  <rise3_middle  and  rise3_middle <0.09", " and  0.09  <rise3_middle  and  rise3_middle <0.10", " and  0.10  <rise3_middle");

        commonDataService.init();
        queryService.remove(new QueryWrapper<>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        baseSqlList.forEach(baseSql -> rangeList.forEach(rangeSql -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String newSql = baseSql + rangeSql + " and field_enum_code = '" + fieldEnum.getCode() + "'";
                verifyPredictRes(newSql);
            }, cpuThreadPool);
            futures.add(future);
        }));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    }

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        commonDataService.init();
        String sql = " rise3_avg > 0.04";
        List<Query> queryList = queryService.getBySql(sql);
        List<Strategy> strategies = new ArrayList<>();
        Set<Integer> strategyIdSet = new HashSet<>();
        for (Query query : queryList) {
            List<Strategy> tmpLit = strategyService.getBySql(query.getSqlStr());
            for (Strategy strategy : tmpLit) {
                if (strategyIdSet.contains(strategy.getStrategyId())) {
                    continue;
                }
                strategy.getStrategyCodeSet().addAll(List.of(strategy.getStrategyCode().split(" ")));
                strategyIdSet.add(strategy.getStrategyId());
                strategies.add(strategy);
            }
        }
        predict("20260410", strategies, false, 1.2);
    }


    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        commonDataService.init();
        buildImg(201281);
    }


    public void verifyPredictRes(String sql) {
        List<Strategy> strategies = strategyService.getBySql(sql).stream().peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" ")))).sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed()).toList();
        //日期    详情列表   详情-权重
        Map<String, Pair<List<Detail>, Map<Integer, Double>>> dataToDetailsMap = new ConcurrentHashMap<>();

        codeToDetailMap.forEach((key, value) -> {
            for (Detail detail : value) {
                if (detail.getDealDate().compareTo(calcEndDate) <= 0) {
                    break;
                }
                if (Objects.isNull(detail.getRise5Max()) || Objects.isNull(detail.getT10()) || Objects.isNull(detail.getT10().getSixtyDayLine()) || moreThan(detail.getRise0(), 0.097)) {
                    continue;
                }
                for (Strategy strategy : strategies) {
                    List<Function<Detail, Boolean>> filterFuncs = strategy.getStrategyCodeSet().stream().map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
                    boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                    if (res) {
                        Pair<List<Detail>, Map<Integer, Double>> pair = dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> Pair.of(new ArrayList<>(), new ConcurrentHashMap<>()));
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


        //统计策略的预测结果 分不加权和加权
        double rise3DateAvgSum = 0;
        double rise3MaxDateAvgSum = 0;
        double rise5DateAvgSum = 0;
        double rise5MaxDateAvgSum = 0;
        double rise3DateWeightAvgSum = 0;
        double rise3MaxDateWeightAvgSum = 0;
        double rise5DateWeightAvgSum = 0;
        double rise5MaxDateWeightAvgSum = 0;
        int dateCnt = 0;
        StringBuilder logStr = new StringBuilder("\n\n").append(String.format("条件： %s", sql));
        StringBuilder weightLogStr = new StringBuilder("加权后\n\n").append(String.format("条件： %s", sql));
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
            dateCnt++;
            double rise3Sum = 0;
            double rise3MaxSum = 0;
            double rise5Sum = 0;
            double rise5MaxSum = 0;
            double rise3WeightSum = 0;
            double rise3MaxWeightSum = 0;
            double rise5WeightSum = 0;
            double rise5MaxWeightSum = 0;
            for (Detail detail : details) {
                rise3WeightSum += detail.getRise3() * detailIdToWeightMap.get(detail.getDetailId());
                rise3MaxWeightSum += detail.getRise3Max() * detailIdToWeightMap.get(detail.getDetailId());
                rise5WeightSum += detail.getRise5() * detailIdToWeightMap.get(detail.getDetailId());
                rise5MaxWeightSum += detail.getRise5Max() * detailIdToWeightMap.get(detail.getDetailId());


                rise3Sum += detail.getRise3();
                rise3MaxSum += detail.getRise3Max();
                rise5Sum += detail.getRise5();
                rise5MaxSum += detail.getRise5Max();
            }

            //不加权的计算
            int size = detailIdToWeightMap.size();
            double rise3DateAvg = divide(rise3Sum, size);
            double rise3MaxDateAvg = divide(rise3MaxSum, size);
            double rise5DateAvg = divide(rise5Sum, size);
            double rise5MaxDateAvg = divide(rise5MaxSum, size);
            rise3DateAvgSum += rise3DateAvg;
            rise3MaxDateAvgSum += rise3MaxDateAvg;
            rise5DateAvgSum += rise5DateAvg;
            rise5MaxDateAvgSum += rise5MaxDateAvg;
            logStr.append(String.format("\n日期：%s\n" + "3日平均涨幅：%.3f%%    3日最高平均涨幅：%.3f%% \n" + "5日平均涨幅：%.3f%%    5日最高平均涨幅：%.3f%% \n", date, rise3DateAvg * 100, rise3MaxDateAvg * 100, rise5DateAvg * 100, rise5MaxDateAvg * 100));

            //加权后的计算
            Double weightSum = sum(detailIdToWeightMap.values().stream().toList());
            double rise3DateWeightAvg = divide(rise3WeightSum, weightSum);
            double rise3MaxDateWeightAvg = divide(rise3MaxWeightSum, weightSum);
            double rise5DateWeightAvg = divide(rise5WeightSum, weightSum);
            double rise5MaxDateWeightAvg = divide(rise5MaxWeightSum, weightSum);
            rise3DateWeightAvgSum += rise3DateWeightAvg;
            rise3MaxDateWeightAvgSum += rise3MaxDateWeightAvg;
            rise5DateWeightAvgSum += rise5DateWeightAvg;
            rise5MaxDateWeightAvgSum += rise5MaxDateWeightAvg;
            weightLogStr.append(String.format("\n 日期：%s\n   " + "3日平均涨幅：%.3f%%    3日最高平均涨幅：%.3f%% \n" + "5日平均涨幅：%.3f%%    5日最高平均涨幅：%.3f%% \n", date, rise3DateWeightAvg * 100, rise3MaxDateWeightAvg * 100, rise5DateWeightAvg * 100, rise5MaxDateWeightAvg * 100));
        }
        if (isEquals(0d, rise3DateAvgSum)) {
            return;
        }
        Query query = new Query();
        query.setSqlStr(sql);
        query.setDateCnt(dateCnt);
        query.setRise3Avg(rise3DateAvgSum / dateCnt);
        query.setRise3MaxAvg(rise3MaxDateAvgSum / dateCnt);
        query.setRise5Avg(rise5DateAvgSum / dateCnt);
        query.setRise5MaxAvg(rise5MaxDateAvgSum / dateCnt);
        query.setOtherData(logStr.toString());

        query.setRise3WeightAvg(rise3DateWeightAvgSum / dateCnt);
        query.setRise3MaxWeightAvg(rise3MaxDateWeightAvgSum / dateCnt);
        query.setRise5WeightAvg(rise5DateWeightAvgSum / dateCnt);
        query.setRise5MaxWeightAvg(rise5MaxDateWeightAvgSum / dateCnt);
        query.setOtherWeightData(weightLogStr.toString());
        queryService.save(query);
        dataToDetailsMap.clear();
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
            List<Function<Detail, Boolean>> filterFuncs = strategy.getStrategyCodeSet().stream()
                    .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        Detail detail = codeToDetailMap.get(stockCode);
                        if (Objects.isNull(detail) || Objects.isNull(detail.getT5())
                                || Objects.isNull(detail.getT5().getSixtyDayLine()) || moreThan(detail.getRise0(), 0.097)) {
                            continue;
                        }
                        if (isOnTime) {
                            detail.setDealQuantity(multiply(detail.getDealQuantity(), quantityMult));
                        }
                        boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                        if (res) {
                            double pert = detail.getRise0() != null ? detail.getRise0() : 0;
                            strategyToStockMap.computeIfAbsent(strategy, k -> Collections.synchronizedList(new ArrayList<>()))
                                    .add("               " +stockCode + "                    " + pert);
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
        Map<String, Long> stockToCntMap = strategyToStockMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<Strategy> resStrategies = strategyToStockMap.keySet().stream().sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed()).toList();
            for (Strategy strategy : resStrategies) {
                List<String> resStockList = strategyToStockMap.get(strategy);
                String str = String.format("\n\n历史总数：%d  策略：%s \n5日最高中位数涨幅：%4f \n", strategy.getDateCnt(), strategy.getName(), strategy.getRise5MaxMiddle());
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
