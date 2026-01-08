package com.mmwwtt.stock.enums;

import com.mmwwtt.demo.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 分时级别枚举
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum TimeLevelEnum implements BaseEnum {
    FIVE("5", "5分钟"),
    FIFTEEN("15", "15分钟"),
    THIRTY("30", "30分钟"),
    SIXTY("60", "60分钟"),
    DAY("d", "日线"),
    WEEK("w", "周线"),
    MONTH("m", "月线"),
    YEAR("f", "年线");
    private final String code;
    private final String desc;
}
