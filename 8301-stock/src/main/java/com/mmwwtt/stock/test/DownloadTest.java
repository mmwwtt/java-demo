package com.mmwwtt.stock.test;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.common.LoggingInterceptor;
import com.mmwwtt.stock.convert.StockConverter;
import com.mmwwtt.stock.convert.VoConvert;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.enums.ExcludeRightEnum;
import com.mmwwtt.stock.enums.TimeLevelEnum;
import com.mmwwtt.stock.service.impl.*;
import com.mmwwtt.stock.vo.DetailOnTimeVO;
import com.mmwwtt.stock.vo.DetailVO;
import com.mmwwtt.stock.vo.StockVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.common.Constants.*;
import static com.mmwwtt.stock.service.impl.CommonDataService.*;

/**
 * 必盈url   <a href="https://www.biyingapi.com/">...</a>
 * 限流 每分钟200次接口调用
 */
@SpringBootTest
@Slf4j
public class DownloadTest {

    @Resource
    private StockServiceImpl stockService;

    @Resource
    private DetailServiceImpl detailService;

    @Resource
    private StrategyServiceImpl strategyService;

    @Resource
    private StrategyTmpServiceImpl strategyTmpService;

    @Resource
    private StrategyL1ServiceImpl strategyL1Service;


    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    private final ThreadPoolExecutor cpuThreadPool = GlobalThreadPool.getCpuThreadPool();

    private final RestTemplate restTemplate = createRestTemplate();
    @Autowired
    private CommonDataService commonDataService;

    /**
     * 创建带超时配置的 RestTemplate
     * 连接超时 30 秒，读取超时 60 秒（可根据需要调整）
     */
    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);  // 连接超时 60 秒
        factory.setReadTimeout(180000);   // 读取超时 180 秒（3 分钟）
        return new RestTemplate(factory);
    }

    /**
     * 今天的日期
     */
    private static final String NOW_DATA;

    private final VoConvert voConvert = VoConvert.INSTANCE;

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        NOW_DATA = LocalDate.now().format(formatter);
    }


    @Test
    @DisplayName("从0开始构建数据")
    public void buildDetail() throws ExecutionException, InterruptedException {
        try {
            downLoadInit();
            downStock();
            downDetail();
//            commonDataService.init();
//            buildStrategyL1();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @DisplayName("重新生成L1曾策略")
    @Test
    public void buildStrategyL1() throws ExecutionException, InterruptedException {
        strategyL1Service.remove(new QueryWrapper<>());

        // 生成最终 strategyL1Enums：baseStrategys + tripleDerivedEnums，并统一打上 T0/T1/T2/T3 前缀。
        List<Integer> tList = List.of(0, 1, 2, 3);
        List<StrategyL1> baseL1s = getBaseL1s();
        List<StrategyL1> allBaseL1 = new ArrayList<>(baseL1s.size() * 10);
        tList.forEach(t -> {
            List<StrategyL1> dateBaseL1 = baseL1s.stream().map(item -> {
                StrategyL1 cur = VoConvert.INSTANCE.convertToStrategyL1(item);
                cur.setCode(t + item.getCode());
                cur.setName(String.format("T%s-%s", t, item.getName()));
                cur.setFilterFunc(item.getFilterFunc());
                if (Objects.nonNull(item.getType())) {
                    cur.setType(String.format("T%s-%s", t, item.getType()));
                }
                return cur;
            }).toList();
            allBaseL1.addAll(dateBaseL1);
        });


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StrategyL1 strategyL1 : allBaseL1) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Detail> curDetails = new ArrayList<>(100000);
                for (String stockCode : stockCodeList) {
                    List<Detail> details = codeToDetailMap.get(stockCode);
                    for (Detail detail : details) {
                        if (Objects.isNull(detail.getNext1())
                                || Objects.isNull(detail.getT10())
                                || Objects.isNull(detail.getT10().getSixtyDayLine())
                                || moreThan(detail.getRise0(), 0.097)
                                || detail.getDealDate().compareTo("202505") < 0
                                || detail.getDealDate().compareTo(calcEndDate) > 0) {
                            continue;
                        }
                        if (strategyL1.getFilterFunc().apply(detail)) {
                            curDetails.add(detail);
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(curDetails) && curDetails.size() > 10) {
                    strategyL1.setDetails(curDetails);
                    strategyL1.fillOtherData();
                    strategyL1Service.save(strategyL1);
                }
            }, cpuThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        log.info("策略层级 1 计算 - 结束");
    }

    @Test
    @DisplayName("验证数据下载接口")
    public void getDataTest() {
        String stockCode = "605162.SH";
        Map<String, String> map1 = new HashMap<>();
        map1.put(LICENCE, BI_YING_LICENCE);
        map1.put(STOCK_CODE, stockCode);
        map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
        map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
        map1.put(START_DATA, "20251201");
        map1.put(END_DATA, NOW_DATA);
        map1.put(MAX_SIZE, "350");
        List<DetailVO> detailVOS = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<DetailVO>>() {
        });

        Map<String, String> map2 = new HashMap<>();
        map2.put(LICENCE, BI_YING_LICENCE);
        map2.put(STOCK_CODE, stockCode.split("\\.")[0]);
        DetailOnTimeVO detailOnTimeVO = getResponse(ON_TIME_DATA_URL, map2, new ParameterizedTypeReference<DetailOnTimeVO>() {
        });
        log.info("获取单个代码的数据结束：{}", JSONObject.toJSONString(detailVOS));
    }

    public void downLoadInit() {
        detailService.remove(new QueryWrapper<>());
        strategyL1Service.remove(new QueryWrapper<>());
        strategyTmpService.remove(new QueryWrapper<>());
        strategyService.remove(new QueryWrapper<>());
        log.info("清空表 end\n");
    }


    @Test
    @DisplayName("调接口获取数据")
    public void downStock() {
        stockService.remove(new QueryWrapper<>());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        Map<String, String> map = new HashMap<>();
        map.put(LICENCE, BI_YING_LICENCE);
        List<StockVO> stockVOList = getResponse(STOCK_LIST_URL, map, new ParameterizedTypeReference<List<StockVO>>() {
        });
        List<Stock> stockList = StockConverter.INSTANCE.convertToStock(stockVOList);
        stockList = stockList.stream().filter(stock -> !stock.getCode().startsWith("30")
                && !stock.getCode().startsWith("68")
                && !stock.getName().contains("ST")).toList();
        stockService.saveBatch(stockList);
        log.info("下载股票列表数据 end");
    }

    @Test
    @DisplayName("调接口获取每日详细数据-全量")
    public void downDetail() throws InterruptedException, ExecutionException {
        List<Stock> stockList = stockService.list();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<Stock>> parts = Lists.partition(stockList, 50);
        for (List<Stock> part : parts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Stock stock : part) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(LICENCE, BI_YING_LICENCE);
                    map1.put(STOCK_CODE, stock.getCode());
                    map1.put(TIME_LEVEL, TimeLevelEnum.DAY.getCode());
                    map1.put(EXCLUDE_RIGHT, ExcludeRightEnum.NONE.getCode());
                    map1.put(START_DATA, "20250101");
                    map1.put(END_DATA, NOW_DATA);
                    map1.put(MAX_SIZE, "350");
                    log.info("获取详情数据-{}", stock.getCode());
                    List<DetailVO> detailVOS = getResponse(HISTORY_DATA_URL, map1, new ParameterizedTypeReference<List<DetailVO>>() {
                    });
                    if (Objects.isNull(detailVOS)) {
                        continue;
                    }
                    detailVOS = detailVOS.stream()
                            .peek(item -> item.setStockCode(stock.getCode()))
                            .filter(item -> item.getSf() == 0)
                            .collect(Collectors.toList());
                    List<Detail> details = voConvert.convertToDetail(detailVOS);
                    details.sort(Comparator.comparing(Detail::getDealDate).reversed());
                    details.forEach(item -> item.calc());
                    Detail.calc(details);
                    detailService.saveBatch(details);
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        log.info("下载股票详细数据  end \n\n\n");
    }


    private <T> T getResponse(String url, Map<String, String> paramMap, ParameterizedTypeReference<T> reference) {
        int cnt = 1;
        while (true) {
            try {
                ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.GET, null, reference, paramMap);
                return res.getBody();
            } catch (Exception e) {
                //打印除限流和连接错误外的错误
                if (!e.getMessage().startsWith("429") && !e.getMessage().startsWith("I/O error on GET")) {
                    log.info("调接口时发生错误{}", e.getMessage());
                    return null;
                }
                try {
                    Thread.sleep(5000);
                    cnt++;
                    if (cnt > 15) {
                        log.info("{}", e.getMessage());
                        break;
                    }
                } catch (Exception e1) {
                    break;
                }
            }
        }
        return null;
    }


    /**
     * 返回最基础的l1策略
     */
    public List<StrategyL1> getBaseL1s() {
        List<StrategyL1> list = new ArrayList<>();


        list.addAll(Arrays.asList(
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


        log.info("根据 triples 生成区间策略枚举(16分/滑动窗口)");
        List<StrategyL1> l1s = new ArrayList<>();
        int fieldIdx = 0;
        for (Triple<String, Function<Detail, Double>, String> triple : triples) {
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

            // 记录 1~3, 2~4, ..., 14~16 => 14 个滑动窗口区间策略
            for (int i = 1; i <= 14; i++) {
                Double left = nodes[i - 1];     // node i
                Double right = nodes[i + 1];   // node i+2
                if (Objects.isNull(left) || Objects.isNull(right) || Objects.equals(left, right)) {
                    continue;
                }

                // base code 独立于 t 前缀，避免不同字段区间重复；后续会在 tList 里前置 T 编码。
                String baseCode = String.format("9%02d%02d", fieldIdx, i);
                String desc = String.format("%s_区间节点%d~%d(%.6f~%.6f)", fieldName, i, i + 2, left, right);
                String finalTypeKey = (typeKey == null || typeKey.isBlank()) ? fieldName : typeKey;
                double l = left;
                double r = right;
                Function<Detail, Boolean> filterFunc = d -> isInRange(getter.apply(d), l, r);

                l1s.add(new StrategyL1(baseCode, desc, finalTypeKey, filterFunc));
            }
            fieldIdx++;
        }
        return list;
    }
}
