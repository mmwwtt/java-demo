package com.mmwwtt.demo.se.设计模式.设计模式23种.行为型.命令模式;

import lombok.extern.slf4j.Slf4j;

public class 命令模式 {

    public static void main(String[] args) {

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
}

/**
 * 命令接收者
 */
@Slf4j
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

class Command implements ICommand {
    private Receiver receiver;
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.action();
    }
}

class Subject {
    private Command command;
    public void setCommand(Command command) {
        this.command = command;
    }
    //发出命令
    public void invoke() {
        command.execute();
    }
}