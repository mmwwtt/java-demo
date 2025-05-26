package com.mmwwtt.demo.activiti.demo;

import jakarta.annotation.Resource;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class ActivitiProcess {
    @Resource
    private RepositoryService repositoryService;

    /**
     * 部署流程定义
     */
    public void deployProcess() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/MyProcess.bpmn20.xml")
                .name("MyProcess")
                .deploy();
        System.out.println("流程部署成功，部署ID：" + deployment.getId());
    }


    @Resource
    private RuntimeService runtimeService;

    /**
     * 启动流程实例
     */
    public void startProcessInstance() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("processKey");
        System.out.println("流程实例启动成功，实例ID：" + processInstance.getId());
    }


    @Resource
    private TaskService taskService;

    /**
     * 查询代办任务并完成
     * @param taskId
     */
    public void completeTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.complete(taskId);
            System.out.println("任务完成：" + task.getName());
        }
    }
}
