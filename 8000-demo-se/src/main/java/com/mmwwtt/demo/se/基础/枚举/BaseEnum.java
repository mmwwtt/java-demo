package com.mmwwtt.demo.se.基础.枚举;

import java.util.Objects;

public interface BaseEnum {

    String name();

    String getCode();

    String getDesc();

    default String getOpt1() {
        return null;
    }

    default String getOpt2() {
        return null;
    }

    /**
     * 根据code找枚举值
     */
    static  <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, String code) {
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * 根据code找枚举值名称
     */
    static  <T extends BaseEnum> String getNameByEnumCode(Class<T> enumClass, String code) {
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant.name();
            }
        }
        return null;
    }
}
