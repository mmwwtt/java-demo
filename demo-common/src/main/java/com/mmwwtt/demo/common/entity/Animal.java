package com.mmwwtt.demo.common.entity;

import lombok.Data;

@Data
public class Animal {
    private String sort  = "动物";

    public String name = "动物";
    public void say() {
        System.out.println("hello");
    }
    public String getSort() {
        return sort;
    }
}
