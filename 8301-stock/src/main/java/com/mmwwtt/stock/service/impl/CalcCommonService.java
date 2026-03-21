package com.mmwwtt.stock.service.impl;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.strategy.Strategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
import static com.mmwwtt.stock.service.impl.CommonDataService.*;

@Service
@Slf4j
public class CalcCommonService {

    @Resource
    private DetailServiceImpl detailService;

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();


    /**
     * 预测明日股票
     */
    public void predict(String curDate, List<Strategy> strategys, boolean isOnTime, double quantityMult) throws InterruptedException, ExecutionException {
        Map<Strategy, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        Map<String, Detail> codeToDetailMap = detailService.getCodeToCurDetailMap(curDate);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
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
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<Strategy> resStrategies = strategyToStockMap.keySet().stream()
                    .sorted(Comparator.comparing(Strategy::getRise5Avg).reversed()).toList();
            for (Strategy strategy : resStrategies) {
                List<String> resStockList = strategyToStockMap.get(strategy);
                String str = String.format("\n\n历史总数：%d  策略：%s \n5日最高平均涨幅：%4f \n5日最高中位数涨幅：%4f \n",
                        strategy.getDateCnt(),strategy.getName(),
                        strategy.getRise5MaxAvg(),strategy.getRise5MaxMiddle());
                fos.write(str.getBytes());
                for (String s : resStockList) {
                    fos.write((s + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }

    }


    /**
     * 验证预测的股票结果
     */
    public List<Detail> verifyPredictRes(String curDate, List<Strategy> strategys) throws InterruptedException, ExecutionException {
        Map<String, Detail> codeToDetailMap = CommonDataService.idToDetailMap.values().stream()
                .filter(item -> Objects.equals(item.getDealDate(), curDate))
                .collect(Collectors.toMap(Detail::getStockCode, item -> item));
        List<Detail> resList = Collections.synchronizedList(new ArrayList<>());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Set<String> stockCodeSet = ConcurrentHashMap.newKeySet();

        for (Strategy strategy : strategys) {
            List<Function<Detail, Boolean>> functionList = Arrays.stream(strategy.getStrategyCode().split(" "))
                    .map(item -> codeToL1Map.get(item).getFilterFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        Detail detail = codeToDetailMap.get(stockCode);
                        if (stockCodeSet.contains(stockCode) || Objects.isNull(detail)
                                || Objects.isNull(detail.getRise5Max())
                                || Objects.isNull(detail.getT5())
                                || Objects.isNull(detail.getT5().getSixtyDayLine())
                                || moreThan(detail.getRise0(), 0.097)
                                || (!detail.getFiveIsUp() && !detail.getTenIsUp())) {
                            continue;
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(detail));
                        if (res) {
                            stockCodeSet.add(stockCode);
                            resList.add(detail);
                        }
                    }
                }, cpuThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();;
        return resList;
    }


}
