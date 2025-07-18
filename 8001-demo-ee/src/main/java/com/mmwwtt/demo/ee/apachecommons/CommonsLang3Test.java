package com.mmwwtt.demo.ee.apachecommons;


import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class CommonsLang3Test {

    @Test
    @DisplayName("测试pair容器类")
    public void test1() {
        Pair<String, String> pair = Pair.of("hello", "world");
        String str1 = pair.getKey();
        String str11 = pair.getLeft();

        String str2 = pair.getValue();
        String str22 = pair.getRight();

        //可需改的pair容器
        MutablePair<String, String> mutablePair = MutablePair.of("hello", "world");
        mutablePair.setLeft("tt");

        CommonUtils.println(str1, str11, str2, str22);
    }

    @Test
    @DisplayName("测试Triple容器类")
    public void test2() {
        Triple<String, String, String> triple = Triple.of("hello", "world", "!");
        String str1 = triple.getLeft();
        String str2 = triple.getMiddle();
        String str3 = triple.getRight();
        CommonUtils.println(str1, str2, str3);
    }

    /**
     * Consumer :只能接收一个参数
     * BiConsumer : 可以接收两个参数
     */
    @Test
    @DisplayName("测试Consumer使用")
    public void test3() {
        BaseInfoVO baseInfoVO = BaseInfoVO.getInstance();
        String str = "hello";
        Pair<String, Consumer<String>> pair = Pair.of("",StringUtils::isBlank);
        Consumer<String> func1 = pair.getRight();
        String str1 = pair.getLeft();
        func1.accept(str1);


        List<Triple<Double, String, BiConsumer<Double, String>>> tripleList = new ArrayList<>();
        tripleList.add(Triple.of(175.00, "0", baseInfoVO::valid));
        tripleList.forEach(triple -> {
            Double height = triple.getLeft();
            String sexCode = triple.getMiddle();
            BiConsumer<Double, String> func = triple.getRight();
            func.accept(height, sexCode);
        });
        log.info("");
    }
}
