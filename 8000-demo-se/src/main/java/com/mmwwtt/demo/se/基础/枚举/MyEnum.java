package com.mmwwtt.demo.se.基础.枚举;

import com.mmwwtt.demo.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义枚举类
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum MyEnum implements BaseEnum {
    MOLE("1", "摩尔"),
    MASS("2", "质量"),
    STDVOL("3", "标准体积"),
    VOLUME("4", "体积");
    private final String code;
    private final String desc;

}
