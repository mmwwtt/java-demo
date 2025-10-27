package com.mmwwtt.demo.common.entity.多态;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Dog extends Animal{

    /**
     * 尾巴大小
     */
    private String tailSize;
}
