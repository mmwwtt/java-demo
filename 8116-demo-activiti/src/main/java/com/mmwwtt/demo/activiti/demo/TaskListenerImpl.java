package com.mmwwtt.demo.activiti.demo;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerImpl implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        String assignee = "小王"; // 设置任务办理人
        delegateTask.setAssignee(assignee);
    }
}