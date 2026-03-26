package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.enums.FilterFildEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.getMiddle;
import static com.mmwwtt.stock.service.CommonDataService.INIT_DATE_SIZE;

/**
 * 策略胜率
 *
 * @author moweitao
 */
@Data
@TableName(value = "strategy_tmp_t")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class StrategyTmp {

    private Integer strategyId;

    /**
     * 策略编码  组合编码
     */
    private String strategyCode;

    /**
     * 有符合数据的日期天数
     */
    private Integer dateCnt;

    /**
     * 有符合数据的日期天数
     */
    private Integer detailCnt;

    /**
     * 用于过滤数据的 字段阈值
     */
    private Double pert;


    @TableField(exist = false)
    private List<Detail> details = Collections.synchronizedList(new ArrayList<>(1000));

    @TableField(exist = false)
    private Set<String> parentWinStrategyCodeSet;

    @TableField(exist = false)
    private Double parentPert;

    /**
     * 符合数据的id Array   需要从小到大排序，便于两个策略取交集的算法使用
     */
    @TableField(exist = false)
    private int[] detailIdArr;


    @TableField(exist = false)
    private Set<String> strategyCodeSet = new HashSet<>();


    /**
     * 同一种类型的type只能有一个，null除外
     */
    @TableField(exist = false)
    private Set<String> strategyTypeSet = new HashSet<>();


    @TableField(exist = false)
    private List<Function<Detail, Boolean>> filterFuncs = new ArrayList<>();

    private Integer level;

    /**
     * 将结果累加到数据中
     */
    public void addToResult(Detail detail) {
        if (Objects.isNull(detail)) {
            return;
        }
        details.add(detail);
    }

    /**
     * 填充数据
     * 只计算 字段的中位涨幅 和平均涨幅
     * 先统计单日的中位数和平均数，  再根据日的中位数和平均数计算总体的中位数和平均数
     */
    public void fillFilterField(FilterFildEnum filterFildEnum) {
        Map<String, Integer> idxMap = new HashMap<>(INIT_DATE_SIZE);
        Map<String, double[]> valuesMap = new HashMap<>(INIT_DATE_SIZE);
        Map<String, Long> cntMap = details.stream().collect(Collectors.groupingBy(Detail::getDealDate, Collectors.counting()));
        cntMap.forEach((date, cnt) -> {
            if (cnt != 0) {
                valuesMap.put(date, new double[Math.toIntExact(cnt)]);
                idxMap.put(date, 0);
            }
        });

        for (Detail detail : details) {
            double[] values = valuesMap.get(detail.getDealDate());
            values[idxMap.get(detail.getDealDate())] = filterFildEnum.getDetailGetter().apply(detail);
            idxMap.merge(detail.getDealDate(), 1, Integer::sum);
        }
        //先计算每日的平均值/中位数
        List<Double> dayValues = new ArrayList<>(INIT_DATE_SIZE);
        for (double[] values : valuesMap.values()) {
            dayValues.add(getMiddle(values));
        }
        //再计算总体的平均数/中位数
        pert = getMiddle(dayValues);
        dateCnt = valuesMap.size();
        detailCnt = detailIdArr.length;
    }

    /**
     * 填充数据
     */
    public void fillCode() {
        //填充策略名称和层级
        StringBuilder unionCode = new StringBuilder(32 * strategyCodeSet.size());
        for (String code : strategyCodeSet) {
            if (!unionCode.isEmpty()) {
                unionCode.append(' ');
            }
            unionCode.append(code);
        }
        this.strategyCode = unionCode.toString();
    }

    public StrategyTmp(StrategyL1 strategyL1,
                       StrategyTmp parentStrategyTmp, int[] detailIdArr) {
        this.strategyCode = strategyL1.getStrategyCode();
        this.parentWinStrategyCodeSet = parentStrategyTmp.getStrategyCodeSet();
        this.parentPert = parentStrategyTmp.getPert();
        this.detailIdArr = Arrays.stream(detailIdArr).sorted().toArray();

        strategyCodeSet.add(strategyL1.getStrategyCode());
        if (Objects.nonNull(parentStrategyTmp.getStrategyCodeSet())) {
            strategyCodeSet.addAll(parentStrategyTmp.getStrategyCodeSet());
        }
        level = strategyCodeSet.size();

        if (Objects.nonNull(strategyL1.getType())) {
            strategyTypeSet.add(strategyL1.getType());
        }
        if (Objects.nonNull(parentStrategyTmp.getStrategyTypeSet())) {
            strategyTypeSet.addAll(parentStrategyTmp.getStrategyTypeSet());
        }
    }
}