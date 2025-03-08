package com.mmwwtt.demo.mq.rocketmq.consumer;

import com.mmwwtt.demo.common.entity.BaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "consumer-group", topic = "demo-topic", selectorType = SelectorType.TAG,
        selectorExpression = "jsonBody")
public class RocketMQEntityConsumer implements RocketMQListener<BaseInfo> {
    @Override
    public void onMessage(BaseInfo message) {
        log.info(" 收到对象消息：{}\n", message.toString());
    }
}
