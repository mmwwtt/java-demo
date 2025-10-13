package com.wwmmtt.demo.springmvc.设计模式.结构_观察者模式.demo1;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BService implements UpdateDataService {

    @Override
    public void updateDataById(List<Integer> list) {
        System.out.println("执行B的操作");
    }
}
