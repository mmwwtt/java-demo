package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;

/**
 * 策略胜率
 *
 * @author moweitao
 */
@Data
@TableName(value = "stock_strategy_win_t")
@NoArgsConstructor
@Slf4j
public class StrategyWin {

    private Long strategyWinId;
    /**
     * 策略编码  组合编码
     */
    private String strategyCode;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 有符合数据的日期天数
     */
    private Integer dateCnt;

    /**
     * 1日平均涨幅
     */
    private Double rise1Avg;

    /**
     * 2日平均涨幅
     */
    private Double rise2Avg;

    /**
     * 3日平均涨幅
     */
    private Double rise3Avg;

    /**
     * 4日平均涨幅
     */
    private Double rise4Avg;

    /**
     * 5日平均涨幅
     */
    private Double rise5Avg;

    /**
     * 10日平均涨幅
     */
    private Double rise10Avg;

    /**
     * 5日最大平均涨幅
     */
    private Double rise5MaxAvg;

    /**
     * 10日最大平均涨幅
     */
    private Double rise10MaxAvg;


    /**
     * 1日中位数涨幅
     */
    private Double rise1Middle;

    /**
     * 2日中位数涨幅
     */
    private Double rise2Middle;

    /**
     * 3日中位数涨幅
     */
    private Double rise3Middle;

    /**
     * 4日中位数涨幅
     */
    private Double rise4Middle;

    /**
     * 5日中位数涨幅
     */
    private Double rise5Middle;

    /**
     * 10日中位数涨幅
     */
    private Double rise10Middle;

    /**
     * 5日最大中位数涨幅
     */
    private Double rise5MaxMiddle;

    /**
     * 10日最大中位数涨幅
     */
    private Double rise10MaxMiddle;


    /**
     * 策略层数
     */
    private Integer level;


    private static int INIT_DATE_SIZE = 500;
    /**
     * 临时属性
     */
    @TableField(exist = false)
    private List<StockDetail> list = Collections.synchronizedList(new ArrayList<>());
    @TableField(exist = false)
    private List<Double> rise1Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise2Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise3Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise4Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise5Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise10Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise5MaxAvgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise10MaxAvgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise1Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise2Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise3Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise4Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise5Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise10Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise5MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    private List<Double> rise10MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);


    @TableField(exist = false)
    private Set<String> strategyCodeSet = new HashSet<>();

    @TableField(exist = false)
    private Set<String> parentWinStrategyCodeSet;

    @TableField(exist = false)
    private Double parentLowLimit;

    @TableField(exist = false)
    private int[] details;

    /**
     * 将结果累加到数据中
     */
    public void addToResult(StockDetail stockDetail) {
        if (Objects.isNull(stockDetail)) {
            log.info("不存在的详情");
            return;
        }
        list.add(stockDetail);
    }

    /**
     * 填充数据
     * 只计算 五日最高中位数涨幅 和五日最高平均涨幅
     */
    public void fillData1() {
        Map<String, List<Double>> dateToFiveMaxPertMap = new HashMap<>(500);
        for (StockDetail stockDetail : list) {
            Double pert = stockDetail.getNext5MaxPricePert();
            if (pert == null) {
                continue;
            }
            dateToFiveMaxPertMap.computeIfAbsent(stockDetail.getDealDate(), k -> new ArrayList<>()).add(pert);
        }

        for (List<Double> fiveMaxPerts : dateToFiveMaxPertMap.values()) {
            if (!fiveMaxPerts.isEmpty()) {
                rise5MaxAvgs.add(getAverage(fiveMaxPerts));
                rise5MaxMiddles.add(getMiddle(fiveMaxPerts));
            }
        }
        rise5MaxAvg = getAverage(rise5MaxAvgs);
        rise5MaxMiddle = getMiddle(rise5MaxMiddles);
        dateCnt = rise5MaxAvgs.size();
    }

    /**
     * 填充数据
     */
    public void fillData2() {

        //填充策略名称和层级
        this.level = strategyCodeSet.size();
        StringBuilder unionName = new StringBuilder(32 * strategyCodeSet.size());
        StringBuilder unionCode = new StringBuilder(32 * strategyCodeSet.size());
        for (String code : strategyCodeSet) {
            if(!unionName.isEmpty()) {
                unionName.append(' ');
            }
            unionName.append(StrategyEnum.codeToEnumMap.get(code).getName());

            if(!unionCode.isEmpty()) {
                unionCode.append(' ');
            }
            unionCode.append(code);
        }
        this.strategyName = unionName.toString();
        this.strategyCode = unionCode.toString();


        //填充其他相关数据
        Map<String, List<StockDetail>> dateToDetailListMap = list.stream().collect(Collectors.groupingBy(StockDetail::getDealDate));

        dateToDetailListMap.forEach((date, details) -> {
            //统计每日中符合策略的stock的涨幅
            List<Double> curRise1s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise2s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise3s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise4s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10Maxs = new ArrayList<>(INIT_DATE_SIZE);
            for (StockDetail detail : details) {
                Double endPrice = detail.getEndPrice();
                if (Objects.nonNull(detail.getNext1())) {
                    curRise1s.add(getRise(detail.getNext1().getEndPrice(), endPrice));
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curRise2s.add(getRise(detail.getNext2().getEndPrice(), endPrice));
                }
                if (Objects.nonNull(detail.getNext3())) {
                    curRise3s.add(getRise(detail.getNext3().getEndPrice(), endPrice));
                }
                if (Objects.nonNull(detail.getNext4())) {
                    curRise4s.add(getRise(detail.getNext4().getEndPrice(), endPrice));
                }
                if (Objects.nonNull(detail.getNext5())) {
                    curRise5s.add(getRise(detail.getNext5().getEndPrice(), endPrice));
                }
                if (Objects.nonNull(detail.getNext10())) {
                    curRise10s.add(getRise(detail.getNext10().getEndPrice(), endPrice));
                    curRise10Maxs.add(detail.getNext10MaxPricePert());
                }
            }

            //算出每日中符合策略的stock的   平均  和中位数
            if (CollectionUtils.isNotEmpty(curRise1s)) {
                rise1Avgs.add(getAverage(curRise1s));
                rise1Middles.add(getMiddle(curRise1s));
            }
            if (CollectionUtils.isNotEmpty(curRise2s)) {
                rise2Avgs.add(getAverage(curRise2s));
                rise2Middles.add(getMiddle(curRise2s));
            }
            if (CollectionUtils.isNotEmpty(curRise3s)) {
                rise3Avgs.add(getAverage(curRise3s));
                rise3Middles.add(getMiddle(curRise3s));
            }
            if (CollectionUtils.isNotEmpty(curRise4s)) {
                rise4Avgs.add(getAverage(curRise4s));
                rise4Middles.add(getMiddle(curRise4s));
            }
            if (CollectionUtils.isNotEmpty(curRise5s)) {
                rise5Avgs.add(getAverage(curRise5s));
                rise5Middles.add(getMiddle(curRise5s));
            }
            if (CollectionUtils.isNotEmpty(curRise10s)) {
                rise10Avgs.add(getAverage(curRise10s));
                rise10Middles.add(getMiddle(curRise10s));
            }
            if (CollectionUtils.isNotEmpty(curRise10Maxs)) {
                rise10MaxAvgs.add(getAverage(curRise10Maxs));
                rise10MaxMiddles.add(getMiddle(curRise10Maxs));
            }

        });
        //算出策略的存在符合数据的日期的   平均  和中位数
        rise1Avg = getAverage(rise1Avgs);
        rise1Middle = getAverage(rise1Middles);
        rise2Avg = getAverage(rise2Avgs);
        rise2Middle = getAverage(rise2Middles);
        rise3Avg = getAverage(rise3Avgs);
        rise3Middle = getAverage(rise3Middles);
        rise4Avg = getAverage(rise4Avgs);
        rise4Middle = getAverage(rise4Middles);
        rise5Avg = getAverage(rise5Avgs);
        rise5Middle = getAverage(rise5Middles);
        rise10Avg = getAverage(rise10Avgs);
        rise10Middle = getAverage(rise10Middles);
        rise10MaxAvg = getAverage(rise10MaxAvgs);
        rise10MaxMiddle = getAverage(rise10MaxMiddles);
    }

    public StrategyWin(String strategyCode) {
        this(strategyCode, null, null, null);

    }

    public StrategyWin(String strategyCode, Set<String> parentWinStrategyCodeSet,
                       Double parentLowLimit, int[] details) {
        this.strategyCode = strategyCode;
        this.parentWinStrategyCodeSet = parentWinStrategyCodeSet;
        this.parentLowLimit = parentLowLimit;
        this.details = details;
        strategyCodeSet.add(strategyCode);
        if (Objects.nonNull(parentWinStrategyCodeSet)) {
            strategyCodeSet.addAll(parentWinStrategyCodeSet);
        }
    }
}