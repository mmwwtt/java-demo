package com.mmwwtt.stock.service.impl;

import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.BaseStrategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

@Service
@Slf4j
public class CommonDataService {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private StrategyL1ServiceImpl strategyL1Service;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    public static Map<Integer, Detail> idToDetailMap = new ConcurrentHashMap<>(1048576);
    public static Map<String, List<Detail>> codeToDetailMap = new ConcurrentHashMap<>(4096);
    public static List<StrategyL1> strategyL1s = Collections.synchronizedList(new ArrayList<>(1000));
    public static Map<String, StrategyL1> codeToL1Map = new ConcurrentHashMap<>(1000);
    public static List<List<String>> stockCodePartList = new ArrayList<>();
    public static List<String> stockCodeList = new ArrayList<>();
    public static String calcEndDate;
    public static List<String> predictDateList = new ArrayList<>();

    public static int INIT_DATE_SIZE = 500;

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("初始化开始");

        idToDetailMap = new ConcurrentHashMap<>(1048576);
        codeToDetailMap = new ConcurrentHashMap<>(4096);
        strategyL1s = Collections.synchronizedList(new ArrayList<>(1000));
        codeToL1Map = new ConcurrentHashMap<>(1000);
        stockCodePartList = new ArrayList<>();
        stockCodeList = new ArrayList<>();
        predictDateList = new ArrayList<>();
        log.info("开始加载L1层策略");
        List<CompletableFuture<Void>> futures = new ArrayList<>();


        log.info("开始查询股票列表");
        stockCodeList = stockService.list().stream().map(Stock::getCode).toList();
        stockCodePartList = Lists.partition(stockCodeList, 50);

        futures = new ArrayList<>();
        log.info("开始查询详情列表");
        for (List<String> part : stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> part.forEach(stockCode -> {
                List<Detail> details = detailService.getBySql(String.format("stock_code='%s'", stockCode));
                details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                detailService.genAllDetail(details);
                details.forEach(item -> idToDetailMap.put(item.getDetailId(), item));
                codeToDetailMap.put(stockCode, details);
                details.removeIf(detail -> Objects.isNull(detail) || Objects.isNull(detail.getT5())
                        || Objects.isNull(detail.getT5().getSixtyDayLine())
                        || Objects.isNull(detail.getNext1())
                        || moreThan(detail.getRise0(), 0.097));
            }), ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        log.info("开始设置Dfs截止日期");
        predictDateList = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().limit(15)
                .map(Detail::getDealDate).toList();
        calcEndDate = codeToDetailMap.getOrDefault("000001.SZ", new ArrayList<>())
                .stream().skip(15)
                .map(Detail::getDealDate).findFirst().orElse("20260201");

        if(!idToDetailMap.isEmpty()) {
            Map<String, Function<Detail, Boolean>> codeToFunc = getAllBaseL1s().stream()
                    .collect(Collectors.toMap(BaseStrategy::getStrategyCode, StrategyL1::getFilterFunc));
            List<Integer> l1IdList = strategyL1Service.getIdList();
            for (Integer l1Id : l1IdList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    StrategyL1 strategyL1 = strategyL1Service.getById(l1Id);
                    int[] ids = new int[strategyL1.getDetailIds().size()];
                    for (int i = 0; i < strategyL1.getDetailIds().size(); i++) {
                        ids[i] = strategyL1.getDetailIds().getIntValue(i);
                    }
                    ids = Arrays.stream(ids).sorted().toArray();
                    strategyL1.setDetailIdArr(ids);
                    strategyL1.fillCodeSet();
                    strategyL1.setFilterFunc(codeToFunc.get(strategyL1.getStrategyCode()));
                    strategyL1s.add(strategyL1);
                    codeToL1Map.put(strategyL1.getStrategyCode(), strategyL1);
                }, ioThreadPool);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            log.info("l1层base策略生成结束");
        }

        log.info("初始化结束");
    }


    /**
     * 返回最基础的l1策略
     */
    public static List<StrategyL1> getAllBaseL1s() throws ExecutionException, InterruptedException {
        List<StrategyL1> baseL1s = new ArrayList<>(1000);


        baseL1s.addAll(Arrays.asList(
                new StrategyL1("00004", "二连红",
                        (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed()),
                new StrategyL1("00005", "三连红", (Detail t0) -> t0.getIsRed() && t0.getT1().getIsRed() && t0.getT2().getIsRed()),
                new StrategyL1("00006", "是十字星", Detail::getIsTenStar),
                new StrategyL1("00007", "多头排列_5日线_大于10_大于20", (Detail t0) ->
                        moreThan(t0.getFiveDayLine(), t0.getTenDayLine()) && moreThan(t0.getTenDayLine(), t0.getTwentyDayLine())),

                new StrategyL1("00010", "区间5向上", Detail::getFiveIsUp),
                new StrategyL1("00011", "区间10向上", Detail::getTenIsUp),
                new StrategyL1("00012", "区间20向上", Detail::getTwentyIsUp),
                new StrategyL1("00013", "区间40向上", Detail::getFortyIsUp),
                new StrategyL1("00014", "区间60向上", Detail::getSixtyIsUp),

                new StrategyL1("00015", "区间5向下", (Detail t0) -> !t0.getFiveIsUp()),
                new StrategyL1("00016", "区间10向下", (Detail t0) -> !t0.getTenIsUp()),
                new StrategyL1("00017", "区间20向下", (Detail t0) -> !t0.getTwentyIsUp()),
                new StrategyL1("00018", "区间40向下", (Detail t0) -> !t0.getFortyIsUp()),
                new StrategyL1("00019", "区间60向下", (Detail t0) -> !t0.getSixtyIsUp()),

                // 金叉: 当天 DIF 上穿 DEA，且前3天持续 DIF<DEA，避免今天死叉明天金叉的反复跳动
                new StrategyL1("00020", "DIF线上穿DEA线_金叉", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    if (Objects.isNull(t1) || Objects.isNull(t2) || Objects.isNull(t3)
                            || Objects.isNull(t3.getDif()) || Objects.isNull(t3.getDea())) {
                        return false;
                    }
                    return moreThan(t0.getDif(), t0.getDea())
                            && lessThan(t1.getDif(), t1.getDea())
                            && lessThan(t2.getDif(), t2.getDea())
                            && lessThan(t3.getDif(), t3.getDea());
                }),

                // 上穿过: 前3天持续在均线下方，避免今天下穿明天上穿的反复跳动
                new StrategyL1("00021", "上穿过5日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFiveDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyL1("00022", "上穿过10日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTenDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyL1("00023", "上穿过20日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTwentyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyL1("00024", "上穿过40日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFortyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),
                new StrategyL1("00025", "上穿过60日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getSixtyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && lessThan(t1.getHighPrice(), line) && lessThan(t2.getHighPrice(), line) && lessThan(t3.getHighPrice(), line);
                }),

                // 下穿过: 前3天持续在均线上方，避免今天上穿明天下穿的反复跳动
                new StrategyL1("00026", "下穿过5日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFiveDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyL1("00027", "下穿过10日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTenDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyL1("00028", "下穿过20日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getTwentyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyL1("00029", "下穿过40日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getFortyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),
                new StrategyL1("00030", "下穿过60日线", (Detail t0) -> {
                    Detail t1 = t0.getT1(), t2 = t0.getT2(), t3 = t0.getT3();
                    Double line = t0.getSixtyDayLine();
                    return Objects.nonNull(t1) && Objects.nonNull(t2) && Objects.nonNull(t3) && Objects.nonNull(line)
                            && moreThan(t0.getHighPrice(), line) && lessThan(t0.getLowPrice(), line)
                            && moreThan(t1.getLowPrice(), line) && moreThan(t2.getLowPrice(), line) && moreThan(t3.getLowPrice(), line);
                }),

                // 均线排列强度
                new StrategyL1("00040", "均线多头排列_1", "maAlignBullScore", (Detail t0) -> Objects.equals(1, t0.getMaAlignBullScore())),
                new StrategyL1("00041", "均线多头排列_2", "maAlignBullScore", (Detail t0) -> Objects.equals(2, t0.getMaAlignBullScore())),
                new StrategyL1("00042", "均线多头排列_3", "maAlignBullScore", (Detail t0) -> Objects.equals(3, t0.getMaAlignBullScore())),
                new StrategyL1("00043", "均线多头排列_4", "maAlignBullScore", (Detail t0) -> Objects.equals(4, t0.getMaAlignBullScore())),


                // 量价背离
                new StrategyL1("00050", "量价背离_价涨量缩", (Detail t0) -> Integer.valueOf(1).equals(t0.getVolumePriceDivergence())),
                new StrategyL1("00051", "量价背离_价跌量增", (Detail t0) -> Integer.valueOf(-1).equals(t0.getVolumePriceDivergence())),


                new StrategyL1("10000", "区间60_0_20", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.0, 0.2)),
                new StrategyL1("10001", "区间60_10_30", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.1, 0.3)),
                new StrategyL1("10002", "区间60_20_40", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.2, 0.4)),
                new StrategyL1("10003", "区间60_30_50", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.3, 0.5)),
                new StrategyL1("10004", "区间60_40_60", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.4, 0.6)),
                new StrategyL1("10005", "区间60_50_70", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.5, 0.7)),
                new StrategyL1("10006", "区间60_60_80", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.6, 0.8)),
                new StrategyL1("10007", "区间60_70_90", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.7, 0.9)),
                new StrategyL1("10008", "区间60_80_100", "position60", (Detail t0) -> isInRange(t0.getPosition60(), 0.8, 1.0)),


                new StrategyL1("10100", "区间40_00_20", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.0, 0.2)),
                new StrategyL1("10101", "区间40_10_30", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.1, 0.3)),
                new StrategyL1("10102", "区间40_20_40", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.2, 0.4)),
                new StrategyL1("10103", "区间40_30_50", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.3, 0.5)),
                new StrategyL1("10104", "区间40_40_60", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.4, 0.6)),
                new StrategyL1("10105", "区间40_50_70", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.5, 0.7)),
                new StrategyL1("10106", "区间40_60_80", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.6, 0.8)),
                new StrategyL1("10107", "区间40_70_90", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.7, 0.9)),
                new StrategyL1("10108", "区间40_80_100", "position40", (Detail t0) -> isInRange(t0.getPosition40(), 0.8, 1.0)),


                new StrategyL1("10200", "区间20_00_20", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.0, 0.2)),
                new StrategyL1("10201", "区间20_10_30", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.1, 0.3)),
                new StrategyL1("10202", "区间20_20_40", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.2, 0.4)),
                new StrategyL1("10203", "区间20_30_50", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.3, 0.5)),
                new StrategyL1("10204", "区间20_40_60", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.4, 0.6)),
                new StrategyL1("10205", "区间20_50_70", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.5, 0.7)),
                new StrategyL1("10206", "区间20_60_80", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.6, 0.8)),
                new StrategyL1("10207", "区间20_70_90", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.7, 0.9)),
                new StrategyL1("10208", "区间20_80_100", "position20", (Detail t0) -> isInRange(t0.getPosition20(), 0.8, 1.0)),


                new StrategyL1("10300", "区间10_00_20", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.0, 0.2)),
                new StrategyL1("10301", "区间10_10_30", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.1, 0.3)),
                new StrategyL1("10302", "区间10_20_40", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.2, 0.4)),
                new StrategyL1("10303", "区间10_30_50", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.3, 0.5)),
                new StrategyL1("10304", "区间10_40_60", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.4, 0.6)),
                new StrategyL1("10305", "区间10_50_70", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.5, 0.7)),
                new StrategyL1("10306", "区间10_60_80", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.6, 0.8)),
                new StrategyL1("10307", "区间10_70_90", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.7, 0.9)),
                new StrategyL1("10308", "区间10_80_100", "position10", (Detail t0) -> isInRange(t0.getPosition10(), 0.8, 1.0)),


                new StrategyL1("10400", "上升缺口_00_02", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.00, 0.02);
                }),

                new StrategyL1("10401", "上升缺口_01_03", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.01, 0.03);
                }),
                new StrategyL1("10402", "上升缺口_02_04", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.02, 0.04);
                }),
                new StrategyL1("10403", "上升缺口_03_05", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return isInRange(space, 0.03, 0.05);
                }),
                new StrategyL1("10404", "上升缺口大于_05", "upGap", (Detail t0) -> {
                    double space = divide(subtract(t0.getLowPrice(), t0.getT1().getHighPrice()), t0.getT1().getHighPrice());
                    return moreThan(space, 0.05);
                }),


                new StrategyL1("10501", "比前一天缩量_00_20", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(), 0.0,
                        multiply(t0.getT1().getDealQuantity(), 0.2))),
                new StrategyL1("10502", "比前一天缩量_10_30", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.1), multiply(t0.getT1().getDealQuantity(), 0.3))),
                new StrategyL1("10503", "比前一天缩量_20_40", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.2), multiply(t0.getT1().getDealQuantity(), 0.4))),
                new StrategyL1("10504", "比前一天缩量_30_50", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.3), multiply(t0.getT1().getDealQuantity(), 0.5))),
                new StrategyL1("10505", "比前一天缩量_40_60", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.4), multiply(t0.getT1().getDealQuantity(), 0.6))),
                new StrategyL1("10506", "比前一天缩量_50_70", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.5), multiply(t0.getT1().getDealQuantity(), 0.7))),
                new StrategyL1("10507", "比前一天缩量_60_80", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.6), multiply(t0.getT1().getDealQuantity(), 0.8))),
                new StrategyL1("10508", "比前一天缩量_70_90", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.7), multiply(t0.getT1().getDealQuantity(), 0.9))),
                new StrategyL1("10509", "比前一天缩量_80_100", "lessDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 0.8), multiply(t0.getT1().getDealQuantity(), 1.0))),


                new StrategyL1("10600", "比前一天放量_00_30", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1), multiply(t0.getT1().getDealQuantity(), 1.3))),
                new StrategyL1("10601", "比前一天放量_10_40", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.1), multiply(t0.getT1().getDealQuantity(), 1.4))),
                new StrategyL1("10602", "比前一天放量_20_50", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.2), multiply(t0.getT1().getDealQuantity(), 1.5))),
                new StrategyL1("10603", "比前一天放量_30_60", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.3), multiply(t0.getT1().getDealQuantity(), 1.6))),
                new StrategyL1("10604", "比前一天放量_40_70", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.4), multiply(t0.getT1().getDealQuantity(), 1.7))),
                new StrategyL1("10605", "比前一天放量_50_80", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.5), multiply(t0.getT1().getDealQuantity(), 1.8))),
                new StrategyL1("10606", "比前一天放量_60_90", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.6), multiply(t0.getT1().getDealQuantity(), 1.9))),
                new StrategyL1("10607", "比前一天放量_70_100", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.7), multiply(t0.getT1().getDealQuantity(), 2.0))),
                new StrategyL1("10608", "比前一天放量_80_110", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.8), multiply(t0.getT1().getDealQuantity(), 2.1))),
                new StrategyL1("10609", "比前一天放量_90_120", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 1.9), multiply(t0.getT1().getDealQuantity(), 2.2))),
                new StrategyL1("10610", "比前一天放量_100_130", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.0), multiply(t0.getT1().getDealQuantity(), 2.3))),
                new StrategyL1("10611", "比前一天放量_110_140", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.1), multiply(t0.getT1().getDealQuantity(), 2.4))),
                new StrategyL1("10612", "比前一天放量_120_150", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.2), multiply(t0.getT1().getDealQuantity(), 2.5))),
                new StrategyL1("10613", "比前一天放量_130_160", "moreDeal", (Detail t0) -> isInRange(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.3), multiply(t0.getT1().getDealQuantity(), 2.6))),
                new StrategyL1("10614", "比前一天放量大于160", "moreDeal", (Detail t0) -> moreThan(t0.getDealQuantity(),
                        multiply(t0.getT1().getDealQuantity(), 2.6)))
        ));

        List<Triple<String, Function<Detail, Double>, String>> triples = new ArrayList<>();
        triples.add(Triple.of("dif", Detail::getDif, "dif"));
        triples.add(Triple.of("dea", Detail::getDea, "dea"));
        triples.add(Triple.of("macd", Detail::getMacd, "macd"));
        triples.add(Triple.of("wr", Detail::getWr, "wr"));
        triples.add(Triple.of("上影线占比", Detail::getUpShadowPert, "upShadowPert"));
        triples.add(Triple.of("上影线长度", Detail::getUpShadowLen, "upShadowLen"));
        triples.add(Triple.of("下影线百分比", Detail::getDownShadowPert, "downShadowPert"));
        triples.add(Triple.of("下引线长度", Detail::getDownShadowLen, "downShadowLen"));
        triples.add(Triple.of("总长", Detail::getAllLen, "allLen"));

        triples.add(Triple.of("rsi", Detail::getRsi, "rsi"));
        triples.add(Triple.of("bias5", Detail::getBias5, "bias5"));
        triples.add(Triple.of("bias20", Detail::getBias20, "bias20"));
        triples.add(Triple.of("布林带", Detail::getBollPosition, "bollPosition"));
        triples.add(Triple.of("20均线波动率", Detail::getVolatility20, "volatility20"));
        triples.add(Triple.of("20均线斜率", Detail::getMa20Slope, "ma20Slope"));
        triples.add(Triple.of("ATR14波动率", Detail::getAtr14, "atr14"));

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int startCode = 20000;
        for (Triple<String, Function<Detail, Double>, String> triple : triples) {
            startCode += 100;
            int finalStartCode = startCode;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                int curStartCode = finalStartCode;
                String fieldName = triple.getLeft();
                Function<Detail, Double> getter = triple.getMiddle();
                String typeKey = triple.getRight();

                List<Double> values = idToDetailMap.values().stream()
                        .map(getter)
                        .filter(Objects::nonNull)
                        .sorted()
                        .toList();

                // 取 16 个节点值：node1..node16
                Double[] nodes = new Double[16];
                int n = values.size();
                for (int i = 1; i <= 16; i++) {
                    int idx = (int) Math.floor((double) i * n / 16.0) - 1;
                    idx = Math.max(0, Math.min(n - 1, idx));
                    nodes[i - 1] = values.get(idx);
                }
                for (int i = 1; i <= 14; i++) {
                    Double left = nodes[i - 1];
                    Double right = nodes[i + 1];
                    if (Objects.isNull(left) || Objects.isNull(right) || Objects.equals(left, right)) {
                        continue;
                    }
                    String desc = String.format("%s_%.3f_%.3f", fieldName, left, right);
                    String finalTypeKey = (typeKey == null || typeKey.isBlank()) ? fieldName : typeKey;
                    double l = left;
                    double r = right;
                    Function<Detail, Boolean> filterFunc = d -> isInRange(getter.apply(d), l, r);

                    baseL1s.add(new StrategyL1(String.valueOf(curStartCode), desc, finalTypeKey, filterFunc));
                    curStartCode++;
                }
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        List<Integer> tList = List.of(0, 1, 2, 3,5);
        List<StrategyL1> allBaseL1 = new ArrayList<>(baseL1s.size() * 10);
        tList.forEach(t -> {
            List<StrategyL1> dateBaseL1 = baseL1s.stream().map(item -> {
                StrategyL1 cur = VoConvert.INSTANCE.convertToStrategyL1(item);
                cur.setStrategyCode(t + item.getStrategyCode());
                cur.setName(String.format("T%s-%s", t, item.getName()));
                cur.setFilterFunc((Detail t0) -> switch (t) {
                    case 0 -> item.getFilterFunc().apply(t0);
                    case 1 -> item.getFilterFunc().apply(t0.getT1());
                    case 2 -> item.getFilterFunc().apply(t0.getT2());
                    case 3 -> item.getFilterFunc().apply(t0.getT3());
                    case 4 -> item.getFilterFunc().apply(t0.getT4());
                    case 5 -> item.getFilterFunc().apply(t0.getT5());
                    default -> false;
                });
                if (Objects.nonNull(item.getType())) {
                    cur.setType(String.format("T%s-%s", t, item.getType()));
                }
                return cur;
            }).toList();
            allBaseL1.addAll(dateBaseL1);
        });
        return allBaseL1;
    }
}
