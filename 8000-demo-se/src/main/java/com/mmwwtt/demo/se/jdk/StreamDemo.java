package com.mmwwtt.demo.se.jdk;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StreamDemo {

    private static List<BaseInfo> list1 = BaseInfo.getPresetList();
    private static List<BaseInfo> list2 = BaseInfo.getPresetList();

    /**
     * 创建数组
     */
    @Test
    public void createListDemo() {
        List<BaseInfo> list = Stream.of(
                        new BaseInfo(),
                        new BaseInfo())
                .collect(Collectors.toList());
        log.info("{}", list);
    }

    /**
     * map():映射元素
     */
    @Test
    public void map() {
        List<String> nameList1 = list1.stream().map(BaseInfo::getName)
                .collect(Collectors.toList());

        List<String> nameList2 = list1.stream()
                .map(item -> "hello: " + item.getName())
                .collect(Collectors.toList());
        log.info("{}\n{}", nameList1, nameList2);
    }

    /**
     * mapToXXX():映射成基本数据类型,包装类不能用sum()
     * sum():累加
     */
    @Test
    public void mapToInt() {
        double sum = list1.stream()
                .mapToDouble(BaseInfo::getHeight).sum();
        log.info("{}", sum);
    }
    /**
     * 基本数据类型转为包装类型
     */
    @Test
    public void boxed() {
        int[] array = {1, 2, 3};
        List<Integer> list = Arrays.stream(array)
                .boxed()
                .toList();
        log.info("{}", list);
    }
    /**
     * flatMap: 将List的流合并成 baseInfo的流
     */
    @Test
    public void flatMap() {
        List<BaseInfo> list = Stream.of(list1, list2)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.info("{}", list);
    }

    /**
     * filter():过滤元素
     * true表示保留
     */
    @Test
    public void filter() {
        List<BaseInfo> list = list1.stream()
                .filter(item -> "1".equals(item.getSexCode()))
                .toList();
        log.info("{}", list);
    }

    /**
     * limit(n): 获取前n个元素
     */
    @Test
    public void limit() {
        List<BaseInfo> list = list1.stream()
                .limit(2)
                .toList();
        log.info("{}", list);
    }

    /**
     * skip(n): 跳过前n个元素（和limit相反）
     */
    @Test
    public void skip() {
        List<BaseInfo> list = list1.stream()
                .skip(1)
                .toList();
        log.info("{}", list);
    }

    /**
     * distinct: 去除重复元素(==判断为true的元素)
     */
    @Test
    public void distinct() {
        List<String> list = Stream.of("hello", "world", "hello")
                .distinct()
                .toList();
        log.info("{}", list);
    }

    /**
     * Collectors.groupingBy 根据条件对集合进行分组
     */
    @Test
    public void groupingBy() {
        Map<String, List<BaseInfo>> map = list1.stream()
                .collect(Collectors.groupingBy(BaseInfo::getSexCode));
        log.info("{}", map);
    }

    /**
     * 多字段排序(默认降序)，
     * reversed()将降序反转为升序
     * sorted(Comparator.comparing(Point::getX)) 根据x进行排序
     * thenComparing(Point::y) 再根据y进行排序
     */
    @Test
    public void sort() {
        List<BaseInfo> list = list1.stream()
                .sorted(Comparator.comparing(BaseInfo::getName).reversed()
                        .thenComparing(BaseInfo::getSexCode).reversed())
                .toList();
        log.info("{}", list);
    }

    /**
     * count计数
     */
    @Test
    public void count() {
        Long count = list1.stream()
                .filter(item -> "0".equals(item.getSexCode()))
                .count();
        log.info("{}", count);
    }

    /**
     * Collectors.joining() 字符串拼接
     */
    @Test
    public void join() {
        String str = list1.stream()
                .map(item -> String.valueOf(item.getName()))
                .collect(Collectors.joining());
        log.info("{}", str);
    }

    /**
     * anyMatch:有一个为true 就返回true
     * allMatch:全部为true 才返回true
     * noneMatch: 全部为flase 才返回true
     */
    @Test
    public void match() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList();
        boolean flag1 = list.stream().anyMatch(item -> item.getHeight() >= 10);
        boolean flag2 = list.stream().allMatch(item -> item.getHeight() >= 11);
        boolean flag3 = list.stream().noneMatch(item -> item.getHeight() >= 12);
        log.info("{},{},{}", flag1, flag2, flag3);
    }

    /**
     * list转map
     * Student::getName 建
     * Function.identity() 返回一个输出和输入一样的lambda表达式对象，等价于 item -> item
     * (key1, key2) -> key2 表示键相同的，后来的会覆盖之前的   不写如果出现该情况会报错
     */
    @Test
    public void mapToList() {
        Map<String, BaseInfo> map1 = list1.stream()
                .collect(Collectors.toMap(BaseInfo::getName, Function.identity(),
                        (key1, key2) -> key2));
        Map<String, Long> map2 = list1.stream()
                .collect(Collectors.toMap(BaseInfo::getName, BaseInfo::getBaseInfoId,
                        (key1, key2) -> key2));
        // map转list
        List<BaseInfo> list = map1.values()
                .stream().toList();
        log.info("{},{}", map1,map2,list);
    }
}
