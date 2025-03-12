package com.mmwwtt.demo.ee.apachecommons;


import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CommonsLang3Demo {
    @Test
    public void PairDemo() {
      Pair<String, String> pair = ImmutablePair.of("hello", "world");
        String str1 = pair.getKey();
        String str11 = pair.getLeft();
        String str2 = pair.getValue();
        String str22 = pair.getRight();
        CommonUtils.println(str1,str11, str2,str22);
    }

    @Test
    public void TripleDemo() {
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
    public void TripBiConsumerDemo() {
        BaseInfoVO baseInfoVO = new BaseInfoVO();
        List<Triple<String, Integer, BiConsumer<Integer, String>>> tripleList = new ArrayList<>();
        tripleList.add(ImmutableTriple.of("hello", 18, baseInfoVO::valid));
        tripleList.forEach(pair -> {
            String name = pair.getLeft();
            Integer age = pair.getMiddle();
            BiConsumer<Integer, String> func = pair.getRight();
            func.accept(age, name);
        });
    }
}
