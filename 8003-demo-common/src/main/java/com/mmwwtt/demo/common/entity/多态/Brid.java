package com.mmwwtt.demo.common.entity.多态;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Brid extends Animal{
    /**
     * 翅膀大小
     */
    private Double wingSize;
}
