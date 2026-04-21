package com.mmwwtt.stock.entity.strategy;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "query_t", autoResultMap = true)
public class Query {

    /**
     * 查询di
     */
    private Integer queryId;

    /**
     * 查询的sql条件
     */
    private String sqlStr;
    /**
     * 能预测的天数统计
     */
    private Integer dateCnt;

    /**
     * 预测的3日平均涨幅
     */
    private Double rise3Avg;

    /**
     * 预测的3日最高平均涨幅
     */
    private Double rise3MaxAvg;
    /**
     * 预测的5日平均涨幅
     */
    private Double rise5Avg;
    /**
     * 预测的5日最高平均涨幅
     */
    private Double rise5MaxAvg;

    /**
     * 统计的每日信息
     */
    private String otherData;


    /**
     * 预测的3日平均涨幅（加权后）
     */
    private Double rise3WeightAvg;

    /**
     * 预测的3日最高平均涨幅(加权后)
     */
    private Double rise3MaxWeightAvg;
    /**
     * 预测的5日平均涨幅(加权后)
     */
    private Double rise5WeightAvg;
    /**
     * 预测的5日最高平均涨幅(加权后)
     */
    private Double rise5MaxWeightAvg;

    /**
     * 统计的每日信息(加权后)
     */
    private String otherWeightData;

    /**
     * 过滤策略枚举编码
     */
    private String fieldEnumCode;
}
