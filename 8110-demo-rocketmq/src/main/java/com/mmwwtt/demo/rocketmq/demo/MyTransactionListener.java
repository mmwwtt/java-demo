package com.mmwwtt.demo.rocketmq.demo;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RocketMQTransactionListener
public class MyTransactionListener implements RocketMQLocalTransactionListener {
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        RocketMQLocalTransactionState result = null;
        try {
            result= RocketMQLocalTransactionState.COMMIT;

        } catch (Exception ex) {
            result = RocketMQLocalTransactionState.ROLLBACK;
        }
        return result;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        RocketMQLocalTransactionState result = null;

        try {
            result= RocketMQLocalTransactionState.COMMIT;
        } catch (Exception ex) {
            result = RocketMQLocalTransactionState.ROLLBACK;

        }
        return result;
    }
}