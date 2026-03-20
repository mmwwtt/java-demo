package com.mmwwtt.stock.common;

import java.util.Objects;

public interface BaseEnum {
    String name();

    String getCode();

    String getDesc();

    static <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, String code) {
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

    static <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, Object code) {
        return getEnumByCode(enumClass, String.valueOf(code));
    }

    static <T extends BaseEnum> T getEnumByName(Class<T> enumClass, String name) {
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

    static <T extends BaseEnum> String getNameByCode(Class<T> enumClass, Object code) {
        return getNameByCode(enumClass, String.valueOf(code));
    }

    static boolean equalsEnumCode(BaseEnum enumm, String code) {
        return Objects.equals(enumm.getCode(), code);
    }

    static boolean equalsEnumCode(BaseEnum enumm, Object code) {
        return equalsEnumCode(enumm, String.valueOf(code));
    }
}
