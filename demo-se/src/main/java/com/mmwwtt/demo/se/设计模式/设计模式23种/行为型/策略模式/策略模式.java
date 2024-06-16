package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.策略模式;


import lombok.Data;
import lombok.ToString;

import java.util.*;

/**
 * 用于算法的自由切换，比如线程池的清理策略
 */
public class 策略模式 {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(-1);
        list.add(18);

        Strategy strategyASC = new StrategyASC();
        Strategy strategyDESC = new StrategyDESC();
        ObjectClass objectClass = new ObjectClass();
        objectClass.setList(list);

        //采用升序排序策略
        objectClass.setStrategy(strategyASC);
        objectClass.sort();
        System.out.println(objectClass.getList());

        //采用降序排序策略
        objectClass.setStrategy(strategyDESC);
        objectClass.sort();
        System.out.println(objectClass.getList());
    }
}

@ToString
@Data
class ObjectClass {
    Strategy strategy;
    List<Integer> list;
    public void sort() {
        strategy.sort(list);
    }

}

/**
 * 抽象策略
 */
abstract class Strategy {
    public abstract void sort(List<Integer> list);
}

/**
 * 具体策略：升序排序
 */
class StrategyASC extends Strategy{
    public void sort(List<Integer> list) {
        Collections.sort(list);
    }
}

/**
 * 具体策略：降序排序
 */
class StrategyDESC extends Strategy{

    @Override
    public void sort(List<Integer> list) {
        Collections.sort(list);
        Collections.reverse(list);
    }
}
