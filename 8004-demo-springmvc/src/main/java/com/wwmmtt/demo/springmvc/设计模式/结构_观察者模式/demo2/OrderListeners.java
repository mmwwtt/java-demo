package com.wwmmtt.demo.springmvc.设计模式.结构_观察者模式.demo2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderListeners {

    /**
     * 执行对应的事件
     * @param event
     */
    @EventListener
    public void sendEmail(OrderCreatedEvent event) {
        log.info("【邮件】给订单 {} 发确认信", event.getIds());
    }

    @EventListener
    @Async   // ← 想异步就加
    public void refreshStock(OrderCreatedEvent event) {
        log.info("【库存】扣减订单 {} 的商品库存", event.getIds());
    }

    @EventListener(condition = "#event.ids.size() >= 5000")   // 条件监听
    @Async
    public void vipService(OrderCreatedEvent event) {
        log.info("【VIP】大额订单 {} 专属客服跟进", event.getIds());
    }
}