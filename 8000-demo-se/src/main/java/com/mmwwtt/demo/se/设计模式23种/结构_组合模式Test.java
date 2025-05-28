package com.mmwwtt.demo.se.设计模式23种;

import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 将对象组合成树形结构
 * Component: 组合中对象的抽象类
 * leaf:叶子节点 程序员类
 * Composite: 非叶子节点 项目经理类
 */
public class 结构_组合模式Test {
    @Test
    @DisplayName("测试组合模式")
    public void test() {
        Employer pm1 = new Manager("项目经理1");
        Employer pm2 = new Manager("项目经理2");
        Employer programmer1 = new Programser("程序员1");
        Employer programmer2 = new Programser("程序员2");
        pm1.add(pm2);
        pm1.add(programmer1);
        pm1.add(programmer2);
    }

    /**
     * 节点抽象类
     */
    @Data
    abstract class Employer {
        private String name;

        private List<Employer> employerList;

        public void add(Employer employer) {
            employerList.add(employer);
        }

        public void remove(Employer employer) {
            employerList.remove(employer);
        }
    }

    /**
     * 叶子节点类
     * 程序员下面无员工
     */
    class Programser extends Employer {
        public Programser(String name) {
            setName(name);
            setEmployerList(null);
        }
    }

    /**
     * 正常节点
     * 经理下面可以有员工
     */
    class Manager extends Employer {
        public Manager(String name) {
            setName(name);
            setEmployerList(new ArrayList<>());
        }
    }
}
