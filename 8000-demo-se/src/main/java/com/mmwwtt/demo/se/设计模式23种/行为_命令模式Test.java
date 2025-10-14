package com.mmwwtt.demo.se.设计模式23种;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 行为_命令模式Test {

    @Test
    @DisplayName("测试命令模式")
    public void test1() {

        Receiver receiver = new Receiver();

        //命令中指定接收者
        Command command = new Command();
        command.setReceiver(receiver);

        //发出者指定命令
        Subject subject = new Subject();
        subject.setCommand(command);

        //调用命令
        subject.invoke();
    }


    /**
     * 命令接收者
     */
    class Receiver {
        public void action() {
            log.info("接收者做出相应动作");
        }
    }


    interface ICommand {
        /**
         * 要执行的命令
         */
        void execute();

    }

    @Data
    class Command implements ICommand {
        private Receiver receiver;

        @Override
        public void execute() {
            receiver.action();
        }
    }

    @Data
    class Subject {
        private Command command;

        //发出命令
        public void invoke() {
            command.execute();
        }
    }
}