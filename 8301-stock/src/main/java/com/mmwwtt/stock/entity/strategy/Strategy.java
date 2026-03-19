package com.mmwwtt.stock.entity.strategy;

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
    private Boolean isActive = true;

    /**
     * 有符合数据的详情数
     */
    private Integer detailCnt;
    public void fillOtherData() {
        super.fillOtherData();
        detailCnt = detailIds.size();
    }
}
