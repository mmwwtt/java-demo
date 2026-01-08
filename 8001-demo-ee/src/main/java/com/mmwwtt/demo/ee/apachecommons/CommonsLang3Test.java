package com.mmwwtt.demo.ee.apachecommons;


import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.*;

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
     * Consumer<T> :只能接收一个参数
     * BiConsumer<T, U> : 可以接收两个参数
     * Supplier<R>  : 无参数， 有返回值
     * Function<T, R>  : 入参类型， 返回值类型
     * BiFunction<T,U, R>  : 入参类型， 返回值类型
     */
    @Test
    @DisplayName("测试Consumer使用")
    public void test3() {
        BaseInfoVO baseInfoVO = BaseInfoVO.getInstance();
        String str = "hello";

        //适合set方法引用
        Consumer<String> consumer1 = StringUtils::isBlank;
        consumer1.accept(str);
        Consumer<String> consumer2 = str::indexOf;
        consumer2.accept("h");


        BiConsumer<String, Character> biConsumer1 = StringUtils::indexOf;
        biConsumer1.accept(str, 'e');
        BiConsumer<Integer, Integer> biConsumer2 = str::substring;
        biConsumer2.accept(1,3);


        //适合get方法引用
        Supplier<byte[]> supplier1 = str::getBytes;
        byte[] supplierRes1 =  supplier1.get();

        Function<String, Boolean> function1 = StringUtils::isBlank;
        Boolean funRes1= function1.apply(str);
        Function<String, Integer> function2 = str::indexOf;
        Integer funRes2 = function2.apply("h");


        BiFunction<String,Character, Integer> biFunction1 = StringUtils::indexOf;
        Integer biFunRes1= biFunction1.apply(str, 'h');
        BiFunction<Integer, Integer, String> biFunction2 = str::substring;
        String biFunRes2 = biFunction2.apply(1,2);


        //此处类名::方法名 等价于 item -> item.方法名()
        List<String> list = BaseInfo.getPresetList().stream().map(BaseInfo::getName).toList();
    }
}
