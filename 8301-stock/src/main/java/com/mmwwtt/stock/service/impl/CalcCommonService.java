package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.*;
import com.mmwwtt.stock.vo.StockDetailQueryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.predictDateList;
import static com.mmwwtt.stock.service.impl.CommonService.stockCodePartList;

@Service
@Slf4j
public class CalcCommonService {

    @Autowired
    private StockDetailServiceImpl stockDetailService;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();
    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();


    /**
     * 预测明日股票
     */
    public void predict(String curDate, List<StrategyWin> strategyWinList, boolean isOnTime, double quantityMult) throws InterruptedException, ExecutionException {
        Map<String, StrategyEnum> codeToEnumMap = StrategyEnum.codeToEnumMap;
        Map<StrategyWin, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        Map<String, StockDetail> codeToDetailMap = stockDetailService.getCodeToCurDetailMap(curDate);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        Set<String> stockCodeSet = new HashSet<>();
        for (StrategyWin strategyWin : strategyWinList) {
            List<Function<StockDetail, Boolean>> functionList = Arrays.stream(strategyWin.getStrategyCode().split(" "))
                    .map(item -> codeToEnumMap.get(item).getRunFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        StockDetail stockDetail = codeToDetailMap.get(stockCode);
                        if (stockCodeSet.contains(stockCode)
                                || Objects.isNull(stockDetail) || Objects.isNull(stockDetail.getT5())
                                || Objects.isNull(stockDetail.getT5().getSixtyDayLine())
                                || moreThan(stockDetail.getPricePert(), "0.097")) {
                            continue;
                        }
                        if (isOnTime) {
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), quantityMult));
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(stockDetail));
                        if (res) {
                            stockCodeSet.add(stockCode);
                            try {
                                strategyToStockMap.computeIfAbsent(strategyWin, k -> new ArrayList<>())
                                        .add(stockCode + "_" + stockDetail.getPricePert().doubleValue());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, cpuThreadPool);
                futures.add(future);
            }
        }

        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        log.info("计算结束");
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(String.format("\n\n\n\n\n\n%s\n", getDateStr()).getBytes());
            List<StrategyWin> list = strategyToStockMap.keySet().stream().sorted(Comparator.comparing(StrategyWin::getFiveMaxPercRate).reversed()).toList();
            for (StrategyWin strategyWin : list) {
                List<String> resStockList = strategyToStockMap.get(strategyWin);
                String str = String.format("\n\n策略id:%d \n胜率:%4f \n历史总数：%d\n明天平均涨幅:%4f  \n5日最高平均涨幅：%4f \n策略：%s \n",
                        strategyWin.getStrategyWinId(), strategyWin.getWinRate(), strategyWin.getCnt(), strategyWin.getOnePercRate(),
                        strategyWin.getFiveMaxPercRate(), strategyWin.getStrategyName());
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
    public Map<StrategyWin, List<StockDetail>> verifyPredictRes(String curDate, List<StrategyWin> strategyWinList) throws InterruptedException, ExecutionException {
        Map<String, StrategyEnum> codeToEnumMap = StrategyEnum.codeToEnumMap;
        Map<StrategyWin, List<StockDetail>> strategyToStockMap = new ConcurrentHashMap<>();
        Map<String, StockDetail> codeToDetailMap = CommonService.idToDetailMap.values().stream()
                .filter(item -> Objects.equals(item.getDealDate(), curDate))
                .collect(Collectors.toMap(StockDetail::getStockCode, item -> item));


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Set<String> stockCodeSet = new HashSet<>();
        //策略
        log.info("开始计算");

        for (StrategyWin strategyWin : strategyWinList) {
            List<Function<StockDetail, Boolean>> functionList = Arrays.stream(strategyWin.getStrategyCode().split(" "))
                    .map(item -> codeToEnumMap.get(item).getRunFunc()).toList();
            for (List<String> part : stockCodePartList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (String stockCode : part) {
                        StockDetail stockDetail = codeToDetailMap.get(stockCode);
                        if (stockCodeSet.contains(stockCode) || Objects.isNull(stockDetail)
                                || Objects.isNull(stockDetail.getT1()) || Objects.isNull(stockDetail.getNext1())
                                || Objects.isNull(stockDetail.getT2()) || Objects.isNull(stockDetail.getT3())
                                || Objects.isNull(stockDetail.getT4()) || Objects.isNull(stockDetail.getT5())
                                || Objects.isNull(stockDetail.getT5().getSixtyDayLine())
                                || moreThan(stockDetail.getPricePert(), 0.097)) {
                            continue;
                        }
                        boolean res = functionList.stream().allMatch(item -> item.apply(stockDetail));
                        if (res) {
                            strategyToStockMap.computeIfAbsent(strategyWin, k -> new ArrayList<>()).add(stockDetail);
                        }
                    }
                }, ioThreadPool);
                futures.add(future);
            }
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();
        return strategyToStockMap;
    }


}
