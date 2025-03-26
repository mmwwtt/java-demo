package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.原型模式;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 通过一个对象实例确定创建对象的种类，通过拷贝原型来创建新的实例
 * 就是复制原对象
 */
@Slf4j
public class 原型模式 {
    public static void main(String[] args) throws CloneNotSupportedException {
        Demo demo = new Demo("小明");
        Demo demoCopy = demo.clone();

        //修改对象中的引用类型的对象中的值，被拷贝的对象也会改变
        demo.setName("小华");

        log.info("{},{}", demo,demoCopy);
    }
}

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
class Demo implements Cloneable {
    private String name;
    @Override
    protected Demo clone() throws CloneNotSupportedException {
        return (Demo) super.clone();
    }
}
