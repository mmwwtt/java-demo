package com.mmwwtt.demo.se.多线程;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class 线程安全的类 {

    @Test
    public void 线程安全的类示例() {
        Stack<Integer> stack = new Stack<>();
        Vector<Integer> vector = new Vector<>();
        Map<String, String> hashtable = new Hashtable<>();
        Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        StringBuffer stringBuffer = new StringBuffer();

        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();

        //将线程不安全集合通过API 转为 线程安全的集合
        map = Collections.synchronizedMap(map);
        set = Collections.synchronizedSet(set);
        list = Collections.synchronizedList(list);
    }
}
