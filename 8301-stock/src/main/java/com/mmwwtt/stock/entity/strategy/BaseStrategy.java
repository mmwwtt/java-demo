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
    protected JSONArray detailIdArray;



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
     * 20日中位数涨幅
     */
    protected Double rise20Middle;

    /**
     * 3日最大中位数涨幅
     */
    protected Double rise3MaxMiddle;

    /**
     * 5日最大中位数涨幅
     */
    protected Double rise5MaxMiddle;

    /**
     * 10日最大中位数涨幅
     */
    protected Double rise10MaxMiddle;
    /**
     * 20日最大中位数涨幅
     */
    protected Double rise20MaxMiddle;

    /**
     * 3日最大中位数跌幅
     */
    protected Double rise3MinMiddle;


    /**
     * 5日最大中位数跌幅
     */
    protected Double rise5MinMiddle;

    /**
     * 10日最大中位数跌幅
     */
    protected Double rise10MinMiddle;
    /**
     * 20日最大中位数跌幅
     */
    protected Double rise20MinMiddle;


    /**
     * 临时属性
     */
    @TableField(exist = false)
    protected List<Detail> details = Collections.synchronizedList(new ArrayList<>(50000));
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
    protected List<Double> rise20Middles = new ArrayList<>(INIT_DATE_SIZE);

    @TableField(exist = false)
    protected List<Double> rise3MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise20MaxMiddles = new ArrayList<>(INIT_DATE_SIZE);

    @TableField(exist = false)
    protected List<Double> rise3MinMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise5MinMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise10MinMiddles = new ArrayList<>(INIT_DATE_SIZE);
    @TableField(exist = false)
    protected List<Double> rise20MinMiddles = new ArrayList<>(INIT_DATE_SIZE);


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
                .collect(Collectors.joining("\n"));

        List<Integer> detailIdList = details.stream().map(Detail::getDetailId)
                .sorted(Comparator.comparing(Integer::intValue)).toList();
        if (Objects.isNull(detailIdArray)) {
            detailIdArray = new JSONArray(detailIdList);
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
            List<Double> curRise3Maxs = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise3Mins = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise4s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5Maxs = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise5Mins = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10Maxs = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise10Mins = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise20s = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise20Maxs = new ArrayList<>(INIT_DATE_SIZE);
            List<Double> curRise20Mins = new ArrayList<>(INIT_DATE_SIZE);
            for (Detail detail : details) {
                if (Objects.nonNull(detail.getNext1())) {
                    curRise1s.add(detail.getRise1());
                }
                if (Objects.nonNull(detail.getNext2())) {
                    curRise2s.add(detail.getRise2());
                }
                if (Objects.nonNull(detail.getNext3())) {
                    curRise3s.add(detail.getRise3());
                    curRise3Maxs.add(detail.getRise3Max());
                    curRise3Mins.add(detail.getRise3Min());
                }
                if (Objects.nonNull(detail.getNext4())) {
                    curRise4s.add(detail.getRise4());
                }
                if (Objects.nonNull(detail.getNext5())) {
                    curRise5s.add(detail.getRise5());
                    curRise5Maxs.add(detail.getRise5Max());
                    curRise5Mins.add(detail.getRise5Min());
                }
                if (Objects.nonNull(detail.getNext10())) {
                    curRise10s.add(detail.getRise10());
                    curRise10Maxs.add(detail.getRise10Max());
                    curRise10Mins.add(detail.getRise10Min());
                }
                if (Objects.nonNull(detail.getNext20())) {
                    curRise20s.add(detail.getRise20());
                    curRise20Maxs.add(detail.getRise20Max());
                    curRise20Mins.add(detail.getRise20Min());
                }
            }

            //算出每日中符合策略的stock的   平均  和中位数
            if (CollectionUtils.isNotEmpty(curRise1s)) {
                rise1Middles.add(getMiddle(curRise1s));
            }
            if (CollectionUtils.isNotEmpty(curRise2s)) {
                rise2Middles.add(getMiddle(curRise2s));
            }
            if (CollectionUtils.isNotEmpty(curRise3s)) {
                rise3Middles.add(getMiddle(curRise3s));
            }
            if (CollectionUtils.isNotEmpty(curRise3Maxs)) {
                rise3MaxMiddles.add(getMiddle(curRise3Maxs));
            }
            if (CollectionUtils.isNotEmpty(curRise3Mins)) {
                rise3MinMiddles.add(getMiddle(curRise3Mins));
            }
            if (CollectionUtils.isNotEmpty(curRise4s)) {
                rise4Middles.add(getMiddle(curRise4s));
            }
            if (CollectionUtils.isNotEmpty(curRise5s)) {
                rise5Middles.add(getMiddle(curRise5s));
            }
            if (CollectionUtils.isNotEmpty(curRise5Maxs)) {
                rise5MaxMiddles.add(getMiddle(curRise5Maxs));
            }
            if (CollectionUtils.isNotEmpty(curRise5Mins)) {
                rise5MinMiddles.add(getMiddle(curRise5Mins));
            }
            if (CollectionUtils.isNotEmpty(curRise10s)) {
                rise10Middles.add(getMiddle(curRise10s));
            }
            if (CollectionUtils.isNotEmpty(curRise10Maxs)) {
                rise10MaxMiddles.add(getMiddle(curRise10Maxs));
            }
            if (CollectionUtils.isNotEmpty(curRise10Mins)) {
                rise10MinMiddles.add(getMiddle(curRise10Mins));
            }
            if (CollectionUtils.isNotEmpty(curRise20s)) {
                rise20Middles.add(getMiddle(curRise20s));
            }
            if (CollectionUtils.isNotEmpty(curRise20Maxs)) {
                rise20MaxMiddles.add(getMiddle(curRise20Maxs));
            }
            if (CollectionUtils.isNotEmpty(curRise20Mins)) {
                rise20MinMiddles.add(getMiddle(curRise20Mins));
            }

        });
        //算出策略的存在符合数据的日期的   平均  和中位数
        rise1Middle = getMiddle(rise1Middles);
        rise2Middle = getMiddle(rise2Middles);
        rise3Middle = getMiddle(rise3Middles);
        rise3MaxMiddle = getMiddle(rise3MaxMiddles);
        rise3MinMiddle = getMiddle(rise3MinMiddles);
        rise4Middle = getMiddle(rise4Middles);
        rise5Middle = getMiddle(rise5Middles);
        rise5MaxMiddle = getMiddle(rise5MaxMiddles);
        rise5MinMiddle = getMiddle(rise5MinMiddles);
        rise10Middle = getMiddle(rise10Middles);
        rise10MaxMiddle = getMiddle(rise10MaxMiddles);
        rise10MinMiddle = getMiddle(rise10MinMiddles);
        rise20Middle = getMiddle(rise20Middles);
        rise20MaxMiddle = getMiddle(rise20MaxMiddles);
        rise20MinMiddle = getMiddle(rise20MinMiddles);
    }
}
