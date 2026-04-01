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
     * 用于过滤数据的 涨幅阈值
     */
    private Double maxMiddle;

    /**
     * 用于过滤数据的 回撤阈值
     */
    private Double minMiddle;


    @TableField(exist = false)
    private List<Detail> details = Collections.synchronizedList(new ArrayList<>(1000));

    @TableField(exist = false)
    private Set<String> parentWinStrategyCodeSet;

    @TableField(exist = false)
    private Double parentMaxMiddle;

    @TableField(exist = false)
    private Double parentMinMiddle;

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

    @TableField(exist = false)
    private  Map<String, Sum> dateSumMap = new HashMap<>(200);

    public void clearCacheDate() {
        this.dateSumMap.clear();
        this.details.clear();
    }
    /**
     * 将结果累加到数据中
     */
    public void addToResult(Detail detail) {
        if (Objects.isNull(detail)) {
            return;
        }
        details.add(detail);
    }

    public void fillDateSumMap() {
        dateCnt = 0;
        for (Detail detail : details) {
            String dealDate = detail.getDealDate();
            Sum sum = dateSumMap.get(dealDate);
            if (sum == null) {
                sum = new Sum();
                dateSumMap.put(dealDate, sum);
                dateCnt++;
            }
        }
        detailCnt = detailIdArr.length;
    }


    /**
     * 填充数据
     * 只计算 字段的中位涨幅 和平均涨幅
     * 先统计单日的中位数和平均数，  再根据日的中位数和平均数计算总体的中位数和平均数
     */
    public void fillFilterField(FilterFildEnum fildEnum) {
        for (Detail detail : details) {
            Double riseMax = fildEnum.getDetailMaxGetter().apply(detail);
            if (riseMax == null) {
                continue;
            }
            Double riseMin = fildEnum.getDetailMinGetter().apply(detail);
            if (riseMin == null) {
                continue;
            }
            String dealDate = detail.getDealDate();
            dateSumMap.get(dealDate).add(riseMax, riseMin);
        }
        double[] rise5MaxArr = new double[dateSumMap.size()];
        double[] rise5MinArr = new double[dateSumMap.size()];
        int i = 0;
        for (Sum value : dateSumMap.values()) {
            rise5MaxArr[i] = value.getRise5MaxAvg();
            rise5MinArr[i] = value.getRise5MinAvg();
            i++;
        }

        maxMiddle = getMiddle(rise5MaxArr);
        minMiddle = getMiddle(rise5MinArr);
        dateCnt = dateSumMap.size();
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
        this.parentMaxMiddle = parentStrategyTmp.getMaxMiddle();
        this.parentMinMiddle = parentStrategyTmp.getMinMiddle();
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