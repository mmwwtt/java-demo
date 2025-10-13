package com.wwmmtt.demo.springmvc.设计模式.结构_观察者模式.demo1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ServiceTest1 {
    @Autowired
    private List<UpdateDataService> updateDataServiceList;

    @Test
    public void test1() {
        List<Integer> list = Arrays.asList(1,3,5,7,2);

        //观察到某事件执行后，通知对应的服务执行相关操作
        updateDataServiceList.forEach(service-> service.updateDataById(list));
    }
}
