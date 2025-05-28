package com.mmwwtt.demo.se.设计模式23种;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 用于算法的自由切换，比如线程池的清理策略
 */
@Slf4j
public class 结构_策略模式Test {

    @Test
    @DisplayName("测试策略模式")
    public void test() {
        List<Integer> list = List.of(1,-1,18);

        VO vo = new VO();
        vo.setList(list);

        //采用升序排序策略
        vo.setStrategy(new StrategyASC());
        vo.sort();
        log.info("{}", vo.getList());

        //采用降序排序策略
        vo.setStrategy(new StrategyDESC());
        vo.sort();
        log.info("{}", vo.getList());
    }


    @ToString
    @Data
    class VO {
        Strategy strategy;
        List<Integer> list;
        public void sort() {
            strategy.sort(list);
        }
    }

    /**
     * 抽象策略
     */
    interface Strategy {
        void sort(List<Integer> list);
    }

    /**
     * 具体策略：升序排序
     */
    class StrategyASC implements Strategy{
        public void sort(List<Integer> list) {
            Collections.sort(list);
        }
    }

    /**
     * 具体策略：降序排序
     */
    class StrategyDESC implements Strategy{
        public void sort(List<Integer> list) {
            Collections.sort(list);
            Collections.reverse(list);
        }
    }

}