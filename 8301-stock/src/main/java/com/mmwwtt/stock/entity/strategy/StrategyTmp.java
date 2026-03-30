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

import static com.mmwwtt.stock.common.CommonUtils.getMiddle;

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
    private Double rise5MaxMiddle;

    /**
     * 最大回撤中位数
     */
    private Double rise5MinMiddle;


    @TableField(exist = false)
    private List<Detail> details = Collections.synchronizedList(new ArrayList<>(1000));

    @TableField(exist = false)
    private Set<String> parentWinStrategyCodeSet;

    @TableField(exist = false)
    private Double parentRise5MaxMiddle;

    @TableField(exist = false)
    private Double parentRise5MinMiddle;

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
        Map<String, Sum> map = new HashMap<>(200);
        for (Detail detail : details) {
            Double pert = detail.getRise5Max();
            if (pert == null) {
                continue;
            }
            Double rise5Min = detail.getRise5Min();
            if (rise5Min == null) {
                continue;
            }
            String dealDate = detail.getDealDate();
            Sum sum = map.get(dealDate);
            if (sum == null) {
                sum = new Sum();
                map.put(dealDate, sum);
            }
            sum.add(pert, rise5Min);
        }
        double[] rise5MaxArr = new double[map.size()];
        double[] rise5MinArr = new double[map.size()];
        int i = 0;
        for (Sum value : map.values()) {
            rise5MaxArr[i] = value.getRise5MaxAvg();
            rise5MinArr[i] = value.getRise5MinAvg();
            i++;
        }

        rise5MaxMiddle = getMiddle(rise5MaxArr);
        rise5MinMiddle = getMiddle(rise5MinArr);
        dateCnt = map.size();
        detailCnt = detailIdArr.length;
    }

    @Data
    private static class Sum {
        double rise5MaxSum = 0;
        double rise5MinSum = 0;
        int cnt = 0;

        public void add(double pert, double rise5Min) {
            this.rise5MaxSum += pert;
            this.rise5MinSum += rise5Min;
            cnt++;
        }

        public double getRise5MaxAvg() {
            return rise5MaxSum / cnt;
        }

        public double getRise5MinAvg() {
            return rise5MinSum / cnt;
        }
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
        this.parentRise5MaxMiddle = parentStrategyTmp.getRise5MaxMiddle();
        this.parentRise5MinMiddle = parentStrategyTmp.getRise5MinMiddle();
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