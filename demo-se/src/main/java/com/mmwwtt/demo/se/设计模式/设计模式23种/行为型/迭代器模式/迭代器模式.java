package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.迭代器模式;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class 迭代器模式 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("world");
        list.add("mmwwtt");
        list.add("dog");

        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}


