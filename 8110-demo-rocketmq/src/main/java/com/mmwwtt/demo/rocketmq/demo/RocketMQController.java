package com.mmwwtt.demo.rocketmq.demo;

import com.mmwwtt.demo.common.entity.BaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rocket-mq")
@Slf4j
public class RocketMQController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private final String topic = "demo-topic";
    private final String tag = "demoTag";

    @GetMapping("/product/send-message")
    public String productSendMessage() {
        String messageContext = "hello world";
        String topicTag = topic + ":" + tag;
        rocketMQTemplate.convertAndSend(topic, messageContext);
        rocketMQTemplate.convertAndSend(topicTag, messageContext);


        Message<String> message = MessageBuilder.withPayload(messageContext).setHeader(RocketMQHeaders.TAGS, tag) //
                // 设置 Tag
                .build();
        rocketMQTemplate.convertAndSend(topic, message);
        return "消息发送成功";
    }

    // 同步发送消息
    @GetMapping("/product/sync/send-message")
    public String productSendSyncMessage() {
        String tag = "sync";
        rocketMQTemplate.syncSend(topic + ":" + tag, "hello sync");
        return "发送同步消息成功";
    }


    // 发送异步消息
    @GetMapping("/product/async/send-message")
    public String productSendAsyncMessage() {
        String tag = "async";
        rocketMQTemplate.asyncSend(topic + ":" + tag, "hello async", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送成功: {}\n", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("异步发送失败: {}\n", throwable.getMessage());
            }
        });
        return "异步消息发送成功";
    }

    // 单向发送消息(不会返回成功失败)
    @GetMapping("/product/one-way/send-message")
    public String productSendOneWayMessage() {
        String tag = "oneWay";
        rocketMQTemplate.sendOneWay(topic + ":" + tag, "hello one way");
        return "单向消息发送成功";
    }


    //顺序发送消息，同一个hashKey的消息 都发往同一个消息队列中
    @GetMapping("/product/order/send-message")
    public String productSendOrderMessage() {
        String tag = "order";
        for (int i = 0; i < 10; i++) {
            rocketMQTemplate.syncSendOrderly(topic + ":" + tag, "hello-" + i, "demoKey");
        }
        return "顺序消息示例发送成功";
    }


    //发送给延迟消息
    @GetMapping("/product/delay/send-message")
    public String productSendDelayMessage() {
        String tag = "delay";
        rocketMQTemplate.syncSend(topic + ":" + tag, MessageBuilder.withPayload("Hello " + "Delay").build(), 3000, 3);
        return "延迟消息示例发送成功";
    }

    //批量发送消息
    @GetMapping("/product/batch/send-message")
    public String productSendBatchMessage() {
        String tag = "batch";
        List<Message> messageList = new ArrayList<>();
        List<String> messages = List.of("hello-1", "hello-2", "hello-3");
        for (String msg : messages) {
            messageList.add(MessageBuilder.withPayload(msg).setHeader(RocketMQHeaders.TAGS, topic).build());
        }
        SendResult sendResult = rocketMQTemplate.syncSend(topic + ":" + tag, messageList);
        log.info("Batch messages sent: {}\n", sendResult);
        return "批量消息示例发送成功";
    }

    //发送事务消息
    @GetMapping("/product/transaction/send-message")
    public String productSendTransactionMessage() {
        String tag = "transaction";
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(topic + ":" + tag,
                MessageBuilder.withPayload("Transaction Message").build(), null);
        return "事务消息示例发送成功";
    }

    //发送对象消息
    @GetMapping("/product/json-body/send-message")
    public String productSendJsonMessage() {
        BaseInfo baseInfo = BaseInfo.getPresetSingle1();
        String tag = "jsonBody";
        rocketMQTemplate.convertAndSend(topic + ":" + tag, baseInfo);
        return "对象消息发送成功";
    }

}
