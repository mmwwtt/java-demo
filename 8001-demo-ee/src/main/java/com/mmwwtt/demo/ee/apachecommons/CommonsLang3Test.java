package com.mmwwtt.demo.ee.apachecommons;


import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class CommonsLang3Test {

    @Test
    @DisplayName("测试Pari容器类")
    public void test1() {
      Pair<String, String> pair = ImmutablePair.of("hello", "world");
        String str1 = pair.getKey();
        String str11 = pair.getLeft();
        String str2 = pair.getValue();
        String str22 = pair.getRight();
        CommonUtils.println(str1,str11, str2,str22);
    }

    @Test
    @DisplayName("测试Triple容器类")
    public void test2() {
        Triple<String, String, String> Triple = ImmutableTriple.of("hello", "world", "!");
        String str1 = Triple.getLeft();
        String str2 = Triple.getMiddle();
        String str3 = Triple.getRight();
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
        List<Triple<Double, String, BiConsumer<Double, String>>> tripleList = new ArrayList<>();
        tripleList.add(ImmutableTriple.of(175.00, "0", baseInfoVO::valid));
        tripleList.forEach(pair -> {
            Double height = pair.getLeft();
            String sexCode = pair.getMiddle();
            BiConsumer<Double, String> func = pair.getRight();
            func.accept(height, sexCode);
        });
        log.info("");
    }
}
