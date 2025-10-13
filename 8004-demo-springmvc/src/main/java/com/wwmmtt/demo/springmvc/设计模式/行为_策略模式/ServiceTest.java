package com.wwmmtt.demo.springmvc.设计模式.行为_策略模式;

import com.wwmmtt.demo.springmvc.DemoSpringMVCStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = DemoSpringMVCStarter.class)
public class ServiceTest {
    @Autowired
    private Map<String, InitStrategy> initStrategyMap;

    @Test
    public void test1() {
        List<Integer> list = Arrays.asList(1,3,5,7,2);
        InitStrategy strategyASC = initStrategyMap.get("strategyASC");
        strategyASC.sort(list);
        System.out.println(list);
    }
}
