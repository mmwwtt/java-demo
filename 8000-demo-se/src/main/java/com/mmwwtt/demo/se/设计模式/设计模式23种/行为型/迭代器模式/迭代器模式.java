package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.迭代器模式;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class 迭代器模式 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("world");
        list.add("mmwwtt");
        list.add("dog");

        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
            log.info(iterator.next());
        }
    }
}


