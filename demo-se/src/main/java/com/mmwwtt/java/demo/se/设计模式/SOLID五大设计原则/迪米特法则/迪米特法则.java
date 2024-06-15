package com.mmwwtt.java.demo.se.设计模式.SOLID五大设计原则.迪米特法则;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * 最少知道原则
 * 不该有直接依赖关系的类之间，不要有依赖，有依赖关系的类之间，尽量只依赖必要的接口
 * 如果两个实体之间无需直接通信，就不应该发生互相调用，可以通过第三方让两个实体交互通信
 */
public class 迪米特法则 {

    @Test
    public void test() {
        Star star = new Star("小明");
        Fans fans = new Fans("小红");
        Agent agent = new Agent(star, fans);
        agent.meeting();
    }
}

@Data
@AllArgsConstructor
class Star {
    private String name;
}

@Data
@AllArgsConstructor
class Fans {
    private String name;
}

@AllArgsConstructor
@Data
class Agent {
    private Star star;
    private Fans fans;

    public void meeting() {
        System.out.println(String.format("粉丝-%s 和 明星-%s 见面了", fans.getName(), star.getName()));
    }
}