package com.mmwwtt.demo.common.entity.多态.level1;

import com.mmwwtt.demo.common.entity.多态.Animal;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Brid extends Animal {
    /**
     * 翅膀大小
     */
    public Double wingSize;
}
