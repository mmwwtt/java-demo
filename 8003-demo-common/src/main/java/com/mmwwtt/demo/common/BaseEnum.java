package com.mmwwtt.demo.common;

import java.util.Objects;

public interface BaseEnum {
    String name();
    String getCode();
    String getDesc();


    /**
     * 根据code找枚举值
     */
    static  <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, String code) {
        if (Objects.isNull(enumClass.getEnumConstants())) {
            return null;
        }
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * 根据code找枚举值
     */
    static  <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, Object code) {
        return getEnumByCode(enumClass, String.valueOf(code));
    }

    /**
     * 根据name找枚举值
     */
    static  <T extends BaseEnum> T getEnumByName(Class<T> enumClass, String name) {
        if (Objects.isNull(enumClass.getEnumConstants())) {
            return null;
        }
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.name(), name)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * 根据code找枚举值名称
     */
    static <T extends BaseEnum> String getNameByCode(Class<T> enumClass, String code) {
        if (Objects.isNull(enumClass.getEnumConstants())) {
            return null;
        }
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant.name();
            }
        }
        return "";
    }

    /**
     * 根据code找枚举值名称
     */
    static <T extends BaseEnum> String getNameByCode(Class<T> enumClass, Object code) {
        return getNameByCode(enumClass, String.valueOf(code));
    }


    /**
     * 判断枚举值和key是否相等
     */
    static  boolean equalsEnumCode(BaseEnum enumm, String code) {
        if (Objects.equals(enumm.getCode(),code)) {
            return true;
        }
        return false;
    }

    /**
     * 判断枚举值和key是否相等
     */
    static  boolean equalsEnumCode(BaseEnum enumm, Object code) {
        return equalsEnumCode(enumm, String.valueOf(code));
    }
}
