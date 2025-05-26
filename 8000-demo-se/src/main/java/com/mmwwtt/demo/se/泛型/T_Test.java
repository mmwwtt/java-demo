package com.mmwwtt.demo.se.泛型;


import com.mmwwtt.demo.se.common.Level_0;
import com.mmwwtt.demo.se.common.Level_1;
import com.mmwwtt.demo.se.common.Level_2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class T_Test {

    @Test
    @DisplayName("泛型使用")
    public void test1() {
        Message<String> message1 = new Message<>();
        message1.setData("");

        Message<Integer> message2 = new Message<>();
        message2.setData(22);
    }

    /**
     * 不管是什么类型的list, 类型都是list
     */
    @Test
    @DisplayName("泛型擦除")
    public void test2() {
        List<Integer> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        assertTrue(list1.getClass() == list2.getClass());
        log.info("{}",list1.getClass());
    }

    /**
     * 泛型限定符
     * <? extends T>: 生产者，不能使用add方法，只能从集合中get元素(用T及T的父类接收)
     * <? super T>: 消费者，不能使用get方法，只能往集合中add元素(T及T的子类),读取时要强转
     */
    @Test
    @DisplayName("通过限定符限制可用的泛型类型")
    public void test3() {
        //用T及T的父类接收
        List<? extends Level_1> level1List1 = List.of(new Level_1());
        Level_0 level_0 = level1List1.get(0);

        //放入T及T的子类
        List<? super Level_1> level1List2= new ArrayList<>();
        level1List2.add(new Level_2());
    }
}
