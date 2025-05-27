package com.mmwwtt.demo.se.设计模式23种.创建型;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 对原有的对象实例进行拷贝，得到一个新的对象
 * 就是复制原对象
 */
@Slf4j
public class 创建_原型模式Test {
    @Test
    @DisplayName("测试原型模式")
    public void test1() throws CloneNotSupportedException {
        Demo demo = new Demo("小明");
        Demo demoCopy = demo.clone();

        //用的是浅拷贝，拷贝对象改变后原对象也会改变
        demo.setName("小华");

        log.info("{},{}", demo,demoCopy);
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
}
