package com.mmwwtt.demo.se.自定义;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义枚举类
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum MyEnum {
    ONE("ONE", 1, "1号"),
    TWO("TWO", 2, "二号"),
    THREE("THREE", 3, "3号"),
    FOUR("FOUR", 4, "4号"),
    FOUR1("FOUR", null, "4号"),
    ;

    /**
     * 名称
     */
    private final String name;

    /**
     * 值
     */
    private final Integer value;

    /**
     * 描述
     */
    private final String desc;

    private static Map<Integer, MyEnum> keyToEnumMap;

    static {
        MyEnum.keyToEnumMap = Arrays.stream(MyEnum.values())
                .collect(Collectors.toMap(MyEnum::getValue, Function.identity()));
    }

    public static void main(String[] args) {
        log.info("{}", MyEnum.ONE);
    }


}
