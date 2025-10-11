package com.mmwwtt.demo.ee.guava;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class Sets使用 {

    /**
     * Set工具类集合相关操作
     */
    @Test
    @DisplayName("com.mmwwtt.demo.ee.guava.Sets使用")
    public void test1() {
        Set<Integer> set1 = Set.of(1,2,3);
        Set<Integer> set2 = Set.of(3,4,5);
        // 交集
        Set<Integer> inter = Sets.intersection(set1, set2).immutableCopy();

        // 并集
        Set<Integer> union = Sets.union(set1, set2);

        // 差集（a - b）
        Set<Integer> diff  = Sets.difference(set1, set2);

        System.out.println();
    }
}
