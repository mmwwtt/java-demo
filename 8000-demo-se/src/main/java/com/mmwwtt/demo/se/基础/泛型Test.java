package com.mmwwtt.demo.se.基础;


import com.mmwwtt.demo.se.common.Level_0;
import com.mmwwtt.demo.se.common.Level_0_1;
import com.mmwwtt.demo.se.common.Level_0_1_2;
import com.mmwwtt.demo.se.common.Message;
import com.mmwwtt.demo.se.基础.枚举.CodeDescEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class 泛型Test {

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
        List<? extends Level_0_1> level1List1 = List.of(new Level_0_1());
        Level_0 level_0 = level1List1.get(0);

        //放入T及T的子类
        List<? super Level_0_1> level1List2= new ArrayList<>();
        level1List2.add(new Level_0_1_2());
    }


    public <T> List<T> getData() {
        List<T> dataList = new ArrayList<>();
        dataList.add((T) "String");
        return dataList;
    }

    @Test
    @DisplayName("泛型使用")
    public  void test4() {
        List<String> stringData = getData();
        List<Integer> integerData = getData();  //int里插入了一个string
        log.info("");
    }


    /**
     * 泛型T都 继承/实现CodeDescEnum
     */
    static  <T extends CodeDescEnum> T getEnumByCode(Class<T> enumClass, String code) {
        for (T constant : enumClass.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant;
            }
        }
        return null;
    }


    /**
     * list中元素都 继承/实现CodeDescEnum
     */
    public void printCodes(List<? extends CodeDescEnum> list) {
        for (CodeDescEnum item : list) {
            System.out.println(item.getCode());
        }
    }
}
