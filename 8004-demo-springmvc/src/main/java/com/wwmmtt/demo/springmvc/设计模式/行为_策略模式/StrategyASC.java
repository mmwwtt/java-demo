package com.wwmmtt.demo.springmvc.设计模式.行为_策略模式;


import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
class StrategyASC implements InitStrategy {

    /**
     * 具体策略：升序排序
     */
    @Override
    public void sort(List<Integer> list) {
        Collections.sort(list);
    }
}