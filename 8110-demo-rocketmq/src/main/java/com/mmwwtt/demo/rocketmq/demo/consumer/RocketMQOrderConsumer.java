package com.mmwwtt.demo.rocketmq.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(consumerGroup = "consumer-group", topic = "demo-topic", selectorType = SelectorType.TAG,
        selectorExpression = "order", consumeMode = ConsumeMode.ORDERLY)
@Slf4j
public class RocketMQOrderConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("收到消息: {}\n", message);
    }
}
