package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import com.mmwwtt.java.demo.se.common.User;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Stream流 {

    @Test
    public void 创建动态数组() {
        List<User> userList = Stream.of(
                        new User(Long.valueOf(1)),
                        new User(Long.valueOf(1)))
                .collect(Collectors.toList());
        assertEquals(userList.size(), 2);
    }

    /**
     * map():映射元素
     */
    @Test
    public void map映射() {
        List<String> userNameList1 = getUserList().stream().map(User::getUserName)
                .collect(Collectors.toList());
        assertEquals(userNameList1.get(0),"小明" );

        List<String> userNameList2 = getUserList().stream()
                .map(item -> {
                    return String.format("hello: %s", item.getUserName());
                }).collect(Collectors.toList());
        assertEquals(userNameList2.get(0),"hello: 小明" );
    }

    /**
     * mapToXXX():映射成基本数据类型
     * sum():累加
     */
    @Test
    public void mapToInt映射() {
        int sum = getUserList().stream()
                .mapToInt(User::getAge).sum();
        assertEquals(8,sum);
    }

    /**
     * flatMap各个数组并不是分别映射一个流，而是映射成流的内容，所有使用flatMap(Arrays::stream)时生成的单个流被合并起来，扁平化成了一个流
     */
    @Test
    public void 二位数组合并成一维数组flatMap() {
        List<User> userList1 = getUserList();
        List<User> userList2 = getUserList();
        List<User> userList3 = Stream.of(userList1, userList2).flatMap(Collection::parallelStream).collect(Collectors.toList());
        assertEquals(userList3.size(), 8);
    }

    /**
     * filter():过滤元素
     */
    @Test
    public void filter过滤() {
        List<User> list1 = getUserList().stream()
                .filter(item -> item.getAge() > 1)
                .collect(Collectors.toList());
        assertEquals(3, list1.size());

        List<User> list2 = getUserList().stream()
                .filter(item -> {
                    if (item.getAge() > 1) {
                        return true;
                    }
                    return false;
        }).collect(Collectors.toList());
        assertEquals(3, list2.size());
    }

    /**
     * limit(n): 获取前n个元素
     */
    @Test
    public void limit截取元素() {
        List<User> userList = getUserList().stream()
                .limit(2)
                .collect(Collectors.toList());
        assertEquals(2, userList.size());
    }

    /**
     * skip(n): 跳过前n个元素（和limit相反）
     */
    @Test
    public void skip跳过元素() {
        List<User> userList = getUserList().stream()
                .skip(1)
                .collect(Collectors.toList());
        assertEquals(3, userList.size());
    }

    /**
     * distinct: 去除重复元素(==判断为true的元素)
     */
    @Test
    public void distinct去重() {
        List<String> list = Arrays.asList("hello", "world", "hello").stream()
                .distinct()
                .collect(Collectors.toList());
        assertEquals(2, list.size());
    }

    /**
     * Collectors.groupingBy 根据条件对集合进行分组
     */
    @Test
    public void groupingBy分组() {
        Map<Integer, List<User>> userMap = getUserList().stream()
                .collect(Collectors.groupingBy(User::getAge));
        assertEquals(userMap.size(),3);
    }

    /**
     * 多字段排序(默认降序)，
     * reversed()将降序反转为升序
     * sorted(Comparator.comparing(Point::getX)) 根据x进行排序
     * thenComparing(Point::y) 再根据y进行排序
     */
    @Test
    public void stream多字段排序() {
        List<User> userList = getUserList().stream()
                .sorted(Comparator.comparing(User::getUserId).reversed()
                        .thenComparing(User::getUserName).reversed())
                .collect(Collectors.toList());
        assertEquals(userList.size(), 4);
    }

    /**
     * count计数
     */
    @Test
    public void 计数() {
        Long count = getUserList().stream().count();
        assertEquals(count, 4);
    }

    /**
     * Collectors.joining() 字符串拼接
     */
    @Test
    public void 拼接() {
        String nameJoinStr = getUserList().stream().map(item -> String.valueOf(item.getUserName())).collect(Collectors.joining());
        assertNotNull(nameJoinStr);
    }

    /**
     * anyMatch:有一个为true 就返回true
     * allMatch:全部为true 才返回true
     * noneMath: 全部为flase 才返回true
     */
    @Test
    public void 判断() {
        boolean flag1 = getUserList().stream().anyMatch(item -> item.getAge() >= 2);
        boolean flag2 = getUserList().stream().allMatch(item -> item.getAge() >= 2);
        boolean flag3 = getUserList().stream().allMatch(item -> item.getAge() >= 2);
        assertEquals(flag1, true);
        assertEquals(flag2, false);
        assertEquals(flag2, false);
    }

    @Test
    public void arr转list() {
        int[] array = new int[]{1,2,3};
        List<Integer> list = Arrays.stream(array).boxed().collect(Collectors.toList());
        assertEquals(list.size(), 3);
    }

    @Test
    public void map和list相互转换() {

        /**
         * list转map
         * Student::getName 建
         * Function.identity() 返回一个输出和输入一样的lambda表达式对象，等价于 item -> item
         * (key1, key2) -> key2 表示键相同的，后来的会覆盖之前的
         */
        Map<String, User> userMap = getUserList().stream()
                .collect(Collectors.toMap(User::getUserName, Function.identity(),(key1, key2) -> key2));
        assertEquals(4,userMap.size());

        /**
         * map转list
         */
        List<User> userList = userMap.entrySet().stream().map(item -> item.getValue()).collect(Collectors.toList());
        assertEquals(4,userList.size());
    }

    public List<User> getUserList() {
        List<User> list = new ArrayList();
        list.add(new User(Long.valueOf(1), "小明", 1, 1));
        list.add(new User(Long.valueOf(2), "张华", 1, 2));
        list.add(new User(Long.valueOf(3), "王二", 2, 3));
        list.add(new User(Long.valueOf(4), "赵九", 2, 2));
        return list;
    }
}
