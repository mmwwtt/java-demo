package com.wwmmtt.demo.springmvc.设计模式.结构_观察者模式.demo2;

import com.wwmmtt.demo.springmvc.DemoSpringMVCStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 使用springboot中的事件发布订阅，来触发消息监听
 */
@Service
@SpringBootTest(classes = DemoSpringMVCStarter.class)
public class OrderServiceTest {
    @Autowired
    private  ApplicationEventPublisher publisher;

    @Test
    public void test1() {
        List<Integer> list = Arrays.asList(1,3,5,7,2);
        //发出事件通知
        publisher.publishEvent(new OrderCreatedEvent(this, list));
    }
}
