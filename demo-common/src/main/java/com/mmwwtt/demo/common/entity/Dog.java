package com.mmwwtt.demo.common.entity;

import lombok.Data;

@Data
public class Dog extends Animal {
    public void say() {
        System.out.println("汪汪");
    }

}
