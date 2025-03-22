package com.mmwwtt.demo.se.泛型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class 泛型擦除 {

    /**
     * 不管是什么类型的list, 类型一致都是list
     */
    @Test
    public void test() {
        List<Integer> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        assertTrue(list1.getClass() == list2.getClass());
        log.info("{}",list1.getClass());
    }
}
