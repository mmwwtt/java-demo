package com.wwmmtt.demo.springmvc;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BaseInfo {
    private String name1;
    private String name2= "name2";
    private String name3 = "name3";
    private static String name4 = "name4";
    private static String name5 ="name5";


    {
        name3 = "name3-update";
    }
    static {
        name5 = "name5-update";
        name6 = "name6-update";
    }
    private static String name6 = "name6";
    private static String name7="name7";

    public static void setName7(String name7) {
        BaseInfo.name7 = name7;
    }

    private List<String> list;
    private Set<String> set;

    //jackson只序列化有getter方法的字段，
    // 通过getter方法暴露static字段给jackSon做序列化(用于controller中返回json)
    public String getName4() {
        return name4;
    }
}
