package com.mmwwtt.demo.common.entity.animal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Dog extends Animal1 {
    public void say() {
        log.info("汪汪");
    }

}
