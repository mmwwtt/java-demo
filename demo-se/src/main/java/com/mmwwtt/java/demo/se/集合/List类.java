package com.mmwwtt.java.demo.se.集合;


import org.junit.jupiter.api.Test;

import java.util.*;

public class List类 {

    @Test
    public void list种类() {
        //数组结构
        List<Integer> list1 = new ArrayList<>();

        //链表结构
        List<Integer> list2 = new LinkedList<>();
    }

    @Test
    public void list常用方法() {
        //初始化方法1
        List<Integer> list1 = new ArrayList<>();

        //初始化方法2: 通过asList获得定长的list,长度不能改变
        List<Integer> list2 = new ArrayList<>(Arrays.asList(1,2,3));

        //初始化方法3: 将一个数组赋给另一个数组（深拷贝）
        List<Integer> list3 = new ArrayList<>(list1);

        //初始化方法4：返回的list长度不能修改
        List<Integer> list5 = Arrays.asList(1);

        //返回list大小
        list1.size();

        //添加元素
        list1.add(1);
        //指定下标添加元素
        list1.add(0, 18);

        //list添加另一个list
        list1.addAll(list2);

        //将list转为数组 0表示全部拷贝
        Integer[] arr = list2.toArray(new Integer[0]);
        Integer[] arr1 = list2.toArray(new Integer[list2.size()]);
        //jdk11后使用该形式， 不需要创建临时数组，且不用考虑长度
        Integer[] arr2 = list2.toArray(Integer[] ::new);
        //访问指定下标的元素
        list1.get(0);

        //移除指定下标的元素
        list1.remove(0);

        //清空元素
        list1.clear();

        List<Integer> list4 = list2.subList(0,1);


    }

    /**
     * 初始化集合尽量给出初始化大小，
     * 每次添加元素都要判断集合容量是否到达零界点，如果到达则重新生成一段更大的内存段，将原数据拷贝到新内存段中
     * 判断和移动都存在性能消耗
     */
    @Test
    public void 规范() {
        List<Integer> list = new ArrayList<>(15);
    }

}
