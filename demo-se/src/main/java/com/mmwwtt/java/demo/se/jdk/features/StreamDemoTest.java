package com.mmwwtt.java.demo.se.jdk.features;

import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemoTest {

    /**
     * 创建数组
     */
    @Test
    public void createListDemo() {
        List<BaseInfoVO> list = Stream.of(
                        new BaseInfoVO(),
                        new BaseInfoVO())
                .collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * map():映射元素
     */
    @Test
    public void mapDemo() {
        List<String> nameList1 = BaseInfoVO.getPresetList().stream().map(BaseInfoVO::getName)
                .collect(Collectors.toList());

        List<String> nameList2 = BaseInfoVO.getPresetList().stream()
                .map(item -> String.format("hello: %s", item.getName()))
                .collect(Collectors.toList());
        System.out.println(nameList1);
        System.out.println(nameList2);
    }

    /**
     * mapToXXX():映射成基本数据类型
     * sum():累加
     */
    @Test
    public void mapToIntDemo() {
        int sum = BaseInfoVO.getPresetList().stream()
                .mapToInt(BaseInfoVO::getAge).sum();
        System.out.println(sum);
    }

    /**
     * flatMap各个数组并不是分别映射一个流，而是映射成流的内容，
     * 所有使用flatMap(Arrays::stream)时生成的单个流被合并起来，
     * 扁平化成了一个流
     */
    @Test
    public void flatMapDemo() {
        List<BaseInfoVO> list1 = BaseInfoVO.getPresetList();
        List<BaseInfoVO> list2 =BaseInfoVO.getPresetList();
        List<BaseInfoVO> list3 = Stream.of(list1, list2)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
        System.out.println(list3);
    }

    /**
     * filter():过滤元素
     * true表示保留
     */
    @Test
    public void filterDemo() {
        List<BaseInfoVO> list1 = BaseInfoVO.getPresetList().stream()
                .filter(item -> item.getAge() > 10)
                .toList();
        CommonUtils.println(list1);
    }

    /**
     * limit(n): 获取前n个元素
     */
    @Test
    public void limitDemo() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList().stream()
                .limit(2)
                .toList();
        CommonUtils.println(list);
    }

    /**
     * skip(n): 跳过前n个元素（和limit相反）
     */
    @Test
    public void skipDemo() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList().stream()
                .skip(1)
                .toList();
        CommonUtils.println(list);
    }

    /**
     * distinct: 去除重复元素(==判断为true的元素)
     */
    @Test
    public void distinctDemo() {
        List<String> list = Stream.of("hello", "world", "hello")
                .distinct()
                .toList();
        CommonUtils.println(list);
    }

    /**
     * Collectors.groupingBy 根据条件对集合进行分组
     */
    @Test
    public void groupingByDemo() {
        Map<Integer, List<BaseInfoVO>> ageToBaseInfoMap = BaseInfoVO.getPresetList().stream()
                .collect(Collectors.groupingBy(BaseInfoVO::getAge));
        CommonUtils.println(ageToBaseInfoMap);
    }

    /**
     * 多字段排序(默认降序)，
     * reversed()将降序反转为升序
     * sorted(Comparator.comparing(Point::getX)) 根据x进行排序
     * thenComparing(Point::y) 再根据y进行排序
     */
    @Test
    public void streamSortDemo() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList().stream()
                .sorted(Comparator.comparing(BaseInfoVO::getName).reversed()
                        .thenComparing(BaseInfoVO::getAge).reversed())
                .toList();
        CommonUtils.println(list);
    }

    /**
     * count计数
     */
    @Test
    public void CountDemo() {
        Long count = BaseInfoVO.getPresetList()
                .stream().filter(item -> item.getAge() > 10)
                .count();
        CommonUtils.println(count);
    }

    /**
     * Collectors.joining() 字符串拼接
     */
    @Test
    public void joinDemo() {
        String nameJoinStr = BaseInfoVO.getPresetList()
                .stream().map(item -> String.valueOf(item.getName()))
                .collect(Collectors.joining());
        CommonUtils.println(nameJoinStr);
    }

    /**
     * anyMatch:有一个为true 就返回true
     * allMatch:全部为true 才返回true
     * noneMath: 全部为flase 才返回true
     */
    @Test
    public void matchDemo() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList();
        boolean flag1 = list.stream().anyMatch(item -> item.getAge() >= 10);
        boolean flag2 = list.stream().allMatch(item -> item.getAge() >= 11);
        boolean flag3 = list.stream().allMatch(item -> item.getAge() >= 12);
        CommonUtils.println(flag1,flag2,flag3);
    }

    /**
     * int需要先通过boxed转为包装类型
     */
    @Test
    public void arrToListDemo() {
        int[] array = new int[]{1,2,3};
        List<Integer> list = Arrays.stream(array)
                .boxed()
                .toList();
        CommonUtils.println(list);
    }

    /**
     * list转map
     * Student::getName 建
     * Function.identity() 返回一个输出和输入一样的lambda表达式对象，等价于 item -> item
     * (key1, key2) -> key2 表示键相同的，后来的会覆盖之前的   不写如果出现该情况会报错
     */
    @Test
    public void mapToListDemo() {


        Map<String, BaseInfoVO> nameToBaseInfoMap = BaseInfoVO.getPresetList().stream()
                .collect(Collectors.toMap(BaseInfoVO::getName, Function.identity(),
                        (key1, key2) -> key2));


        // map转list
        List<BaseInfoVO> list = nameToBaseInfoMap.values()
                .stream().toList();
        CommonUtils.println(nameToBaseInfoMap, list);
    }


}
