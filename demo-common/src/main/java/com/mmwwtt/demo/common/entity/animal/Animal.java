package com.mmwwtt.demo.common.entity.animal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Animal {
    private String sort  = "动物";

    public String name = "动物";
    public void say() {
        log.info("hello");
    }
    public String getSort() {
        return sort;
    }
}
