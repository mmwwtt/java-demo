package com.mmwwtt.enums;

import com.mmwwtt.demo.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 除权方式枚举
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum ExcludeRightEnum implements BaseEnum {

    NONE("n", "不复权"),
    FRONT("f", "前复权"),
    BACKWARD("b", "后复权"),
    FRONT_RATIO("fr", "等比前复权"),
    BACKWARD_RATIO("br", "等比后复权");
    private final String code;
    private final String desc;
}
