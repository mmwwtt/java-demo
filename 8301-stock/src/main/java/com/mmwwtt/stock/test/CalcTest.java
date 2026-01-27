package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.dao.StockDao;
import com.mmwwtt.stock.dao.StockDetailDao;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockCalcRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockStrategy;
import com.mmwwtt.stock.service.StockCalcService;
import com.mmwwtt.stock.service.StockGuiUitls;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Slf4j
@SpringBootTest
public class CalcTest {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockCalcService stockCalcService;

    @Autowired
    private StockDetailDao stockDetailDao;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private StockCalcResDao stockCalcResDao;

    private final VoConvert voConvert = VoConvert.INSTANCE;


    @Test
    @DisplayName("根据所有策略计算胜率")
    public void startCalc2() throws ExecutionException, InterruptedException {
        stockCalcService.calcByStrategy(StockCalcService.STRATEGY_LIST);
    }

    @Test
    @DisplayName("根据策略绘制蜡烛图")
    public void startCalc3() throws ExecutionException, InterruptedException {
        StockStrategy strategy = new StockStrategy("test_" + getTimeStr(), (StockDetail t0) -> {
            StockDetail t1 = t0.getT1();
            StockDetail t2 = t0.getT2();
            StockDetail t3 = t0.getT3();
            return isInRange(t0.getPosition20(), 0.1, 0.2)
                    && isInRange(t0.getLowShadowLen(), 0.04, 0.10)
                    && t0.getIsRed();
        });
        Map<String, List<StockDetail>> resMap = stockCalcService.calcByStrategy(List.of(strategy));
        resMap.forEach((strategyName, resList) -> {
            resList.stream().filter(item -> item.getNext1().getIsDown()).limit(200).forEach(item -> {
                try {
                    StockGuiUitls.genDetailImage(item, strategyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        log.info("绘制完成");
    }


    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        stockCalcService.calcByStrategy(List.of(

                new StockStrategy("下影线和实体 10%<下影线 20日向上 ", (StockDetail t0) -> {
                    return moreThan(add(t0.getLowShadowLen(), t0.getEntityLen()), 0.1)
                            && t0.getTwentyIsUp();
                })
        ));
    }


    @Test
    @DisplayName("测试单个策略")
    public void startCalc5() throws ExecutionException, InterruptedException {
        StockStrategy strategy = StockCalcService.getStrategy("");
        stockCalcService.calcByStrategy(List.of(strategy));
    }


    @Test
    @DisplayName("测试策略-大类")
    public void startCalc6() throws ExecutionException, InterruptedException {
        List<StockStrategy> strategyList = StockCalcService.getStrategyList("上升缺口");
        stockCalcService.calcByStrategy(strategyList);
    }


    @Test
    @DisplayName("根据策略预测")
    public void getStockByStrategy() throws InterruptedException, ExecutionException {
        stockCalcService.calcByStrategy(StockCalcService.STRATEGY_LIST);

        String curDate = "20260127";
        boolean isOnTime = true;
        Map<String, List<String>> strategyToStockMap = new ConcurrentHashMap<>();
        List<Stock> stockList = stockCalcService.getAllStock();
        Map<String, StockDetail> codeToDetailMap = stockCalcService.getCodeToTodayDetailMap();

        List<List<Stock>> parts = Lists.partition(stockList, 50);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //策略
        log.info("开始计算");
        QueryWrapper<StockCalcRes> detailWapper = new QueryWrapper<>();
        detailWapper.last("where type = 1" +
                " and create_date = (select max(create_date) from stock_calculation_result_t where type = '1')" +
                " and win_rate > 0.7" +
                " order by win_rate desc;");
        Map<String, String> strategyMap =
                stockCalcResDao.selectList(detailWapper).stream()
                        .collect(Collectors.toMap(StockCalcRes::getStrategyDesc, item -> item.getWinRate().toString(), (key1, key2) -> key2));
        for (String strategyName : strategyMap.keySet()) {
            Function<StockDetail, Boolean> runFunc = StockCalcService.getStrategy(strategyName).getRunFunc();
            for (List<Stock> part : parts) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (Stock stock : part) {
                        StockDetail stockDetail = codeToDetailMap.get(stock.getCode());
                        if (Objects.isNull(stockDetail) || Objects.isNull(stockDetail.getT1())
                                || Objects.isNull(stockDetail.getT2()) || Objects.isNull(stockDetail.getT3())
                                || Objects.isNull(stockDetail.getT4()) || Objects.isNull(stockDetail.getT5())) {
                            continue;
                        }
                        if (isOnTime) {
                            stockDetail.setDealQuantity(multiply(stockDetail.getDealQuantity(), "2"));
                        }
                        if (moreThan(stockDetail.getPricePert(), "0.097") || !Objects.equals(stockDetail.getDealDate(), curDate)) {
                            continue;
                        }
                        if (runFunc.apply(stockDetail)) {
                            strategyToStockMap.computeIfAbsent(strategyName, k -> new ArrayList<>())
                                    .add(stock.getCode() + "_" + stock.getName() + "_" + stockDetail.getPricePert().doubleValue());
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
            for (Map.Entry<String, List<String>> entry : strategyToStockMap.entrySet()) {
                fos.write(("\n\n" + entry.getKey() + "   " + strategyMap.get(entry.getKey()) + "\n").getBytes());
                for (String s : entry.getValue()) {
                    fos.write((s + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }

    }


    @Test
    @DisplayName("根据百分比区间预测明日会上涨")
    public void getStockByInterval() {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        List<Stock> stockList = stockDao.selectList(queryWrapper);
        //百分比策略
        for (int i = 0; i < 4; i++) {
            List<Stock> resList = new ArrayList<>();
            QueryWrapper<StockCalcRes> calcWapper = new QueryWrapper<>();
            calcWapper.apply(" create_date = (select max(create_date) from stock_calculation_result_t where type = '0')")
                    .eq("type", StockCalcRes.TypeEnum.INTERVAL.getCode())
                    .ge("all_cnt", 450)
                    .ge("win_rate", 0.6 + i * 0.05)
                    .le("win_rate", 0.65 + i * 0.05);
            List<StockCalcRes> stockCalcResList = stockCalcResDao.selectList(calcWapper);
            stockCalcResList.forEach(item -> item.setStockStrategy(
                    JSON.toJavaObject(JSON.parseObject(item.getStrategyDesc()), StockStrategy.class)));


            for (Stock stock : stockList) {
                //30开头是创业板  68开头是科创版
                if (stock.getCode().startsWith("30") || stock.getCode().startsWith("68")) {
                    return;
                }
                QueryWrapper<StockDetail> detailWapper = new QueryWrapper<>();
                detailWapper.eq("stock_code", stock.getCode())
                        .last("limit 1")
                        .orderByDesc("deal_date");
                List<StockDetail> stockDetails = stockDetailDao.selectList(detailWapper);
                StockDetail stockDetail = stockDetails.stream().findFirst().orElse(null);
                if (Objects.nonNull(stockDetail)) {
                    boolean isOk = stockCalcResList.stream().anyMatch(calc -> {
                        StockStrategy strategy = calc.getStockStrategy();
                        return strategy.getLowShadowLowLimit().compareTo(stockDetail.getLowShadowPert()) <= 0
                                && strategy.getLowShadowUpLimit().compareTo(stockDetail.getLowShadowPert()) >= 0
                                && strategy.getUpShadowLowLimit().compareTo(stockDetail.getUpShadowPert()) <= 0
                                && strategy.getUpShadowUpLimit().compareTo(stockDetail.getUpShadowPert()) >= 0
                                && strategy.getPricePertLowLimit().compareTo(stockDetail.getPricePert()) <= 0
                                && strategy.getPricePertUpLimit().compareTo(stockDetail.getPricePert()) >= 0;
                    });
                    if (isOk) {
                        resList.add(stock);
                    }
                }

            }
            String filePath = "src/main/resources/file/test.txt";

            File file = new File(filePath);

            if (!file.getParentFile().exists()) {
                boolean res = file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write(String.format("\n\n%s    win_rate:%3f  - %3f\n", getDateStr(), 0.6 + i * 0.05, 0.65 + i * 0.05).getBytes());
                for (Stock item : resList) {
                    String str = String.format("%s_%s\n", item.getCode(), item.getName());
                    fos.write(str.getBytes());
                }
            } catch (IOException ignored) {
            }
        }
    }
}
