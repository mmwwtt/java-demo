package com.mmwwtt.demo.ee.apachecommons;

import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SetUtils使用 {

    /**
     * Set工具类集合相关操作
     */
    @Test
    @DisplayName("SetUtils使用")
    public void test1() {
        Set<Integer> set1 = Set.of(1,2,3);
        Set<Integer> set2 = Set.of(3,4,5);

        // 差集（a - b）
        Set<Integer> diff = SetUtils.difference(set1, set2);

        // 并集
        Set<Integer> union = SetUtils.union(set1, set2);

        //交集
        Set<Integer> inter = SetUtils.intersection(set1, set2);
    }
}
