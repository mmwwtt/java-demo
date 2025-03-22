package com.mmwwtt.demo.se.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@Slf4j
public class OptionalDemo {

    /**
     * Optional类：避免大量的是否为null判断，代码更加优雅
     * isPresent() 非null返回ture
     * Optional.empty():创建空的Optional对象
     * Optional.of(): 创建非空的Optional对象
     * Optional.ofNullable: 创建可能为空的Optional对象
     * orElse: 如果是null返回默认值
     * orElseGet：如果是null返回方法结果值
     * orElseThrow: 如果是null则抛出异常
     */
    @Test
    public void test() {
        Optional<String> empty = Optional.empty();
        log.info("{}",empty.isPresent());
        empty.get();

        Optional<String> notNull = Optional.of("hello");
        log.info("{}",notNull.isPresent());

        Optional<String> ableNull = Optional.ofNullable(null);
        log.info("{}",ableNull.isPresent());

        String str = ableNull.get();
        String str1 = ableNull.orElse("hell0");
        String str2 = ableNull.orElseGet(() -> "hello");
        String str3 = ableNull.orElseThrow();
    }

}
