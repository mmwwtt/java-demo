package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "strategy_t", autoResultMap = true)
public class Strategy extends BaseStrategy {

    /**
     * 策略层级
     */
    private Integer level;

    /**
     * 有符合数据的日期天数
     */
    private Integer dateCnt;

    /**
     * 是否有效， 当两个策略重复度高达95%时， 字段阈值高的有效， 低的则改成失效状态
     */
    @TableField(exist = false)
    private Boolean isActive = true;

    /**
     * 过滤策略枚举编码
     */
    private String fieldEnumCode;

    /**
     * 能预测的天数统计
     */
    private Integer predictDateCnt;

    /**
     * 预测的3日平均涨幅
     */
    private Double predictRise3Avg;

    /**
     * 预测的3日最高平均涨幅
     */
    private Double predictRise3MaxAvg;
    /**
     * 预测的5日平均涨幅
     */
    private Double predictRise5Avg;
    /**
     * 预测的5日最高平均涨幅
     */
    private Double predictRise5MaxAvg;

    /**
     * 有符合数据的详情数
     */
    private Integer detailCnt;
    public void fillOtherData() {
        super.fillOtherData();
        detailCnt = detailIdArray.size();
    }
}
