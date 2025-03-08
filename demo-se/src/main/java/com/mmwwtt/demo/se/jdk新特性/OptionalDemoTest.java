package com.mmwwtt.demo.se.jdk新特性;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class OptionalDemoTest {

    /**
     * Optional类：避免大量的是否为null判断，代码更加优雅
     * Optional.empty():创建空的Optional对象
     * Optional.of(): 创建非空的Optional对象
     * Optional.ofNullable: 创建可能为空的Optional对象
     */
    @Test
    public void test() {
        Optional<String> empty = Optional.empty();
        log.info("{}",empty);
        assertEquals(false, empty.isPresent());


        Optional<String> notNull = Optional.of("hello");
        log.info("{}",notNull);
        assertEquals(true, notNull.isPresent());

        Optional<String> ableNull = Optional.ofNullable(null);
        log.info("{}",ableNull);
        assertEquals(false, ableNull.isPresent());


    }

}
