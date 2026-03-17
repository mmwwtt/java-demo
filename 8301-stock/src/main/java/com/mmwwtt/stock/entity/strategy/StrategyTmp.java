package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.enums.FilterFildEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;
import static com.mmwwtt.stock.service.impl.CommonService.INIT_DATE_SIZE;

/**
 * 策略胜率
 *
 * @author moweitao
 */
@Data
@TableName(value = "strategy_tmp_t")
@NoArgsConstructor
@Slf4j
public class StrategyTmp {

    private Long strategyId;
    /**
     * 策略编码  组合编码
     */
    private String strategyCode;

    /**
     * 有符合数据的日期天数
     */
    private Integer dateCnt;

    /**
     * 用于过滤数据的 字段阈值
     */
    private Double pert;


    @TableField(exist = false)
    private List<Detail> details = Collections.synchronizedList(new ArrayList<>(50000));

    @TableField(exist = false)
    private Set<String> parentWinStrategyCodeSet;

    @TableField(exist = false)
    private Double parentPert;

    @TableField(exist = false)
    private int[] detailIdArr;


    @TableField(exist = false)
    private Set<String> strategyCodeSet = new HashSet<>();

    @TableField(exist = false)
    private List<Function<Detail, Boolean>> filterFuncs= new ArrayList<>();


    public int getLevel() {
        return strategyCodeSet.size();
    }

    /**
     * 将结果累加到数据中
     */
    public void addToResult(Detail detail) {
        if (Objects.isNull(detail)) {
            log.info("不存在的详情");
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
        boolean isMiddleFunc = filterFildEnum.getCode().endsWith("MIDDLE");
        Map<String, List<Double>> dateToValuesMap = new HashMap<>(500);
        for (Detail detail : details) {
            Double pert = filterFildEnum.getDetailGetter().apply(detail);
            if (pert == null) {
                continue;
            }
            dateToValuesMap.computeIfAbsent(detail.getDealDate(), k -> new ArrayList<>()).add(pert);
        }
        //先计算每日的平均值/中位数
        List<Double> dayValues = new ArrayList<>(INIT_DATE_SIZE);
        for (List<Double> values : dateToValuesMap.values()) {
            dayValues.add(isMiddleFunc ? getMiddle(values) : getAverage(values));
        }
        //再计算总体的平均数/中位数
        pert = isMiddleFunc ? getMiddle(dayValues) : getAverage(dayValues);
        dateCnt = dateToValuesMap.size();
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


    public StrategyTmp(String strategyCode) {
        this(strategyCode, null, null);
    }

    public StrategyTmp(String strategyCode,
                       Double parentPert, int[] detailIdArr) {
        this.strategyCode = strategyCode;
        this.parentPert = parentPert;
        this.detailIdArr = detailIdArr;
        strategyCodeSet.add(strategyCode);
        if (Objects.nonNull(parentWinStrategyCodeSet)) {
            strategyCodeSet.addAll(parentWinStrategyCodeSet);
        }
    }
}