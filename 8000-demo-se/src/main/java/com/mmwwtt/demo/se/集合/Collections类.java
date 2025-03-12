package com.mmwwtt.demo.se.集合;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class Collections类 {

    @Test
    public void 将集合转为不可修改() {
        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();

        map = Collections.unmodifiableMap(map);
        set = Collections.unmodifiableSet(set);
        list = Collections.unmodifiableList(list);

        //jdk10 新增写法
        List<String> list2 = List.of("hello", "world");
    }

    @Test
    public void 将集合转为线程安全的集合() {
        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();

        //将线程不安全集合通过API 转为 线程安全的集合
        map = Collections.synchronizedMap(map);
        set = Collections.synchronizedSet(set);
        list = Collections.synchronizedList(list);

    }

    @Test
    public void Collections类常用方法() {
        //获得定长数组，长度不可变
        List<Integer> list1 = Collections.emptyList();

        List<Integer> list = new ArrayList<>();
        list.add(100);
        list.add(55);
        list.add(400);
        //list倒序
        Collections.reverse(list);

        //交换list中两个元素
        Collections.swap(list, 0, 1);

        //对list进行排序（默认字典序/整形升序）
        Collections.sort(list);
        Collections.sort(list, (Integer a, Integer b) -> {
            /**
             * 返回正数，第一个元素在后面；
             * 返回0,不交换
             * 返回负数，第一个元素在前面；
             */
            return a.intValue() > b.intValue() ? -1 : 1;
        });

//        List<BaseInfoVO> baseInfoList = BaseInfoVO.getPresetList();
//        Collections.sort(baseInfoList, (BaseInfoVO a, BaseInfoVO b) -> {
//            if (a.getAge() != b.getAge()) {
//                return a.getAge() > b.getAge() ? -1 : 1;
//            } else if (!a.getName().equals(b.getName())) {
//                return a.getName().compareTo(b.getName());
//            } else {
//                return 0;
//            }
//        });
        log.info("hello");
    }

}
