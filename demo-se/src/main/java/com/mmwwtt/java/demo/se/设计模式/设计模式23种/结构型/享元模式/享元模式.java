package com.mmwwtt.java.demo.se.设计模式.设计模式23种.结构型.享元模式;

import java.util.HashMap;
import java.util.Map;

/**
 *使用物件(如常量池)来减少内存使用量，减少创建对象的数量
 * java中的Integer,long,Short,String常量池都运用了享元模式
 * Integer的享元对象在JVM启动时就提前创建好了
 * 享元模式主要是为了对象的复用，减少重复对象的创建
 * 单例模式的为了限制对象的个数，而不是复用
 */
public class 享元模式 {

    Map<Integer, String> map = new HashMap<>();

    public static void main(String[] args) {
        Integer a = 1;   //会使用享元模式
        //Integer b = new Integer(1);  //不会使用享元模式，已经过时
        Integer c = Integer.valueOf(1);  //会使用享元模式

    }
}

/**
 * 享元模式
 */
class Factory {
    private Map<Integer, String> map;
    public String getString(String str) {
        Integer address = str.hashCode();
        if (map.containsKey(address)) {
            return map.get(address);
        }
        map.put(address, str);
        return str;
    }
}