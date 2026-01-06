package com.mmwwtt.demo.ee.guava;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Lists使用 {

    @Test
    @DisplayName("Lists工具类的使用")
    public void test1() {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7");

        //将list 每隔3个分一组
        List<List<String>> partition = Lists.partition(list, 3);

    }
}
