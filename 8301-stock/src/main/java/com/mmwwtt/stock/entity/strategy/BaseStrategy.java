package com.mmwwtt.stock.entity.strategy;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.mmwwtt.stock.entity.Detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.CommonDataService.*;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseStrategy {

    /**
     * 结果id
     */
    @EqualsAndHashCode.Include
    @TableId(type = IdType.AUTO)
    protected Integer strategyId;

    /**
     * 策略编码
     */
    protected String strategyCode;

    /**
     * 策略描述
     */
    protected String name;


    /**
     * 预测对的列表
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    protected JSONArray detailIds;

    /**
     * 1日平均涨幅
     */
    protected Double rise1Avg;

    /**
     * 2日平均涨幅
     */
    protected Double rise2Avg;

    /**
     * 3日平均涨幅
     */
    protected Double rise3Avg;

    /**
     * 4日平均涨幅
     */
    protected Double rise4Avg;

    /**
     * 5日平均涨幅
     */
    protected Double rise5Avg;

    /**
     * 10日平均涨幅
     */
    protected Double rise10Avg;

    /**
     * 5日最大平均涨幅
     */
    protected Double rise5MaxAvg;

    /**
     * 10日最大平均涨幅
     */
    protected Double rise10MaxAvg;


    /**
     * 1日中位数涨幅
     */
    protected Double rise1Middle;

    /**
     * 2日中位数涨幅
     */
    protected Double rise2Middle;

    /**
     * 3日中位数涨幅
     */
    protected Double rise3Middle;

    /**
     * 4日中位数涨幅
     */
    protected Double rise4Middle;

    /**
     * 5日中位数涨幅
     */
    protected Double rise5Middle;

    /**
     * 10日中位数涨幅
     */
    protected Double rise10Middle;

    /**
     * 5日最大中位数涨幅
     */
    protected Double rise5MaxMiddle;

    /**
     * 10日最大中位数涨幅
     */
    protected Double rise10MaxMiddle;


    /**
     * 临时属性
     */
    @TableField(exist = false)
    protected List<Detail> details = Collections.synchronizedList(new ArrayList<>(50000));
    @TableField(exist = false)
    protected List<Double> rise1Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise2Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise3Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise4Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10Avgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5MaxAvgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10MaxAvgs = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise1Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise2Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise3Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise4Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10Middles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);


    /**
     * 符合数据的id Array   需要从小到大排序，便于两个策略取交集的算法使用
     */
    @TableField(exist = false)
    protected int[] detailIdArr;

    /**
     * 策略编码
     */
    @TableField(exist = false)
    protected Set<String> strategyCodeSet = new HashSet<>();

    public void fillCodeSet() {
        strategyCodeSet = Arrays.stream(strategyCode.split(" ")).collect(Collectors.toSet());
    }

    /**
     * DFS完成后进行全部的数据填充
     */
    public void fillOtherData() {
        name = Arrays.stream(strategyCode.split(" "))
                .map(strategyCode -> codeToL1Map.containsKey(strategyCode)?
                        codeToL1Map.get(strategyCode).getName(): name)
                .collect(Collectors.joining(" "));

        List<Integer> detailIdList = details.stream().map(Detail::getDetailId)
                .sorted(Comparator.comparing(Integer::intValue)).toList();
        if (Objects.isNull(detailIds)) {
            detailIds = new JSONArray(detailIdList);
        }
        if (Objects.isNull(detailIdArr)) {
            detailIdArr = detailIdList.stream().mapToInt(Integer::intValue).toArray();
        }

        //填充其他相关数据
        Map<String, List<Detail>> dateToDetailListMap = details.stream().collect(Collectors.groupingBy(Detail::getDealDate));

        dateToDetailListMap.forEach((date, details) -> {
            //统计每日中符合策略的stock的涨幅
            List<Double> curRise1s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise2s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise3s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise4s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5Maxs = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10Maxs = new ArrayList<>(INIT_DATE_SIZE);
            for (Detail detail : details) {
                if (Objects.nonNull(detail.getNext1())) {
                    curRise1s.add(detail.getRise1());
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curRise2s.add(detail.getRise2());
                }
                if (Objects.nonNull(detail.getNext3())) {
                    curRise3s.add(detail.getRise3());
                }
                if (Objects.nonNull(detail.getNext4())) {
                    curRise4s.add(detail.getRise4());
                }
                if (Objects.nonNull(detail.getNext5())) {
                    curRise5s.add(detail.getRise5());
                    curRise5Maxs.add(detail.getRise5Max());
                }
                if (Objects.nonNull(detail.getNext10())) {
                    curRise10s.add(detail.getRise10());
                    curRise10Maxs.add(detail.getRise10Max());
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
            if (CollectionUtils.isNotEmpty(curRise5Maxs)) {
                rise5MaxAvgs.add(getAverage(curRise5Maxs));
                rise5MaxMiddles.add(getMiddle(curRise5Maxs));
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
        rise1Middle = getMiddle(rise1Middles);
        rise2Avg = getAverage(rise2Avgs);
        rise2Middle = getMiddle(rise2Middles);
        rise3Avg = getAverage(rise3Avgs);
        rise3Middle = getMiddle(rise3Middles);
        rise4Avg = getAverage(rise4Avgs);
        rise4Middle = getMiddle(rise4Middles);
        rise5Avg = getAverage(rise5Avgs);
        rise5Middle = getMiddle(rise5Middles);
        rise5MaxAvg = getAverage(rise5MaxAvgs);
        rise5MaxMiddle = getMiddle(rise5MaxMiddles);
        rise10Avg = getAverage(rise10Avgs);
        rise10Middle = getMiddle(rise10Middles);
        rise10MaxAvg = getAverage(rise10MaxAvgs);
        rise10MaxMiddle = getMiddle(rise10MaxMiddles);
    }
}
