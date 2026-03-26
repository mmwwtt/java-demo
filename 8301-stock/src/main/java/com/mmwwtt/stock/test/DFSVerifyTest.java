package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.StockGuiUtils;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.service.impl.DetailServiceImpl;
import com.mmwwtt.stock.service.impl.StrategyServiceImpl;
import jakarta.annotation.PostConstruct;
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

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.CommonDataService.*;

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
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private static List<Strategy> strategies;

    // rise5  rise5_max_middle > 0.16
    @PostConstruct
    public void init() {
        String sql = "rise5_middle > rise3_middle " +
                "and rise10_middle > rise5_middle ";
        strategies = strategyService.getBySql(sql)
                .stream()
                .peek(item -> item.getStrategyCodeSet().addAll(List.of(item.getStrategyCode().split(" "))))
                .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed())
                .toList();
    }

    @Test
    @DisplayName("验证策略")
    public void verifyPredictResByFiveMax() {
        log.info("\n\n");
        verifyPredictRes(Detail::getRise5, Detail::getRise5Max);
    }


    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        predict("20260324", strategies, false, 1.2);
    }


    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() {
        buildImg(201281);
    }


    public void verifyPredictRes(Function<Detail, Double> riseGetter,
                                 Function<Detail, Double> riseMaxGetter) {
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
                    List<Function<Detail, Boolean>> filterFuncs = strategyWin.getStrategyCodeSet().stream()
                            .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
                    boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                    if (res) {
                        Pair<List<Detail>, Map<Integer, Double>> pair =
                                dataToDetailsMap.computeIfAbsent(detail.getDealDate(), k -> Pair.of(new ArrayList<>(), new HashMap<>()));
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
            List<Double> riseMaxs = details.stream()
                    .map(item -> riseMaxGetter.apply(item) * detailIdToWeightMap.get(item.getDetailId()))
                    .toList();
            List<Double> rises = details.stream()
                    .map(item -> riseGetter.apply(item) * detailIdToWeightMap.get(item.getDetailId()))
                    .toList();
            Double weightSum = sum(detailIdToWeightMap.values().stream().toList());
            double riseMaxsDateAvg = divide(sum(riseMaxs), weightSum);
            double risesDateAvg = divide(sum(rises), weightSum);
            log.info("日期：{}   5日平均涨幅：{}%   5日最高平均涨幅：{}%  \n",
                    date, String.format("%.3f", risesDateAvg * 100),
                    String.format("%.3f", riseMaxsDateAvg * 100));

            fiveMaxDateAvgList.add(riseMaxsDateAvg);
            fiveDateAvgList.add(risesDateAvg);
        }
        log.info("平均5日最高涨幅 {}%", String.format("%.3f", getAverage(fiveMaxDateAvgList) * 100));
        log.info("平均5日涨幅 {}%", String.format("%.3f", getAverage(fiveDateAvgList) * 100));
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
        Set<String> stockCodeSet = ConcurrentHashMap.newKeySet();
        for (Strategy strategy : strategys) {
            List<Function<Detail, Boolean>> filterFuncs = Arrays.stream(strategy.getStrategyCode().split(" "))
                    .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        Detail detail = codeToDetailMap.get(stockCode);
                        if (stockCodeSet.contains(stockCode)
                                || Objects.isNull(detail) || Objects.isNull(detail.getT5())
                                || Objects.isNull(detail.getT5().getSixtyDayLine())
                                || moreThan(detail.getRise0(), 0.097)) {
                            continue;
                        }
                        if (isOnTime) {
                            detail.setDealQuantity(multiply(detail.getDealQuantity(), quantityMult));
                        }
                        boolean res = filterFuncs.stream().allMatch(item -> item.apply(detail));
                        if (res) {
                            stockCodeSet.add(stockCode);
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

        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<Strategy> resStrategies = strategyToStockMap.keySet().stream()
                    .sorted(Comparator.comparing(Strategy::getRise5MaxMiddle).reversed()).toList();
            for (Strategy strategy : resStrategies) {
                List<String> resStockList = strategyToStockMap.get(strategy);
                String str = String.format("\n\n历史总数：%d  策略：%s \n5日最高中位数涨幅：%4f \n",
                        strategy.getDateCnt(), strategy.getName(),
                        strategy.getRise5MaxMiddle());
                fos.write(str.getBytes());
                for (String s : resStockList) {
                    fos.write((s + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }

    }
}
