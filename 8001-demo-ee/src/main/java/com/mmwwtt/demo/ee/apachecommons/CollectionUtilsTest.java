package com.mmwwtt.demo.ee.apachecommons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class CollectionUtilsTest {
    @Test
    @DisplayName("CollectionUtils测试")
    public void test1() {
        List<String> list = new ArrayList<>();
        Set<String> set = new HashSet<>();

        //判断集合是否为null/空
        CollectionUtils.isEmpty(list);
        CollectionUtils.isEmpty(set);
    }
}
