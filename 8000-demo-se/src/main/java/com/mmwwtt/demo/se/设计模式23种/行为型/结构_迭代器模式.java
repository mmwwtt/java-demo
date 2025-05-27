package com.mmwwtt.demo.se.设计模式23种.行为型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class 结构_迭代器模式 {

    @Test
    @DisplayName("测试迭代器模式")
    public void test() {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("world");
        list.add("mmwwtt");
        list.add("dog");

        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
            log.info(iterator.next());
        }
    }
}


