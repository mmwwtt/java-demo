package com.mmwwtt.demo.se.基础.枚举;

import com.mmwwtt.demo.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MyEnum1 implements BaseEnum {
    MOLE("1", "摩尔"),
    MASS("2", "质量"),
    STDVOL("3", "标准体积"),
    VOLUME("4", "体积");
    private final String code;
    private final String desc;
}
