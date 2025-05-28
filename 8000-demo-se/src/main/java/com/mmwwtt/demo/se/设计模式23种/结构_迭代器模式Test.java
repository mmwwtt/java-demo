package com.mmwwtt.demo.se.设计模式23种;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class 结构_迭代器模式Test {

    @Test
    @DisplayName("测试迭代器模式")
    public void test() {
        List<String> list = List.of("Hello", "world", "mmwwtt", "dog");

        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
            log.info(iterator.next());
        }
    }
}


