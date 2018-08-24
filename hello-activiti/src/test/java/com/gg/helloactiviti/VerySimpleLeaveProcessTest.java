package com.gg.helloactiviti;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;

import static org.junit.Assert.*;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 18-8-24.
 */
public class VerySimpleLeaveProcessTest {
    @Test
    public void testStartProcess(){
        //创建流程引擎，使用内存数据库
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
        //部署流程定义文件
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("diagrams/MyProcess.bpmn").deploy();

        //验证已部署流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        assertEquals("myProcess", processDefinition.getKey());

        //启动流程并返回流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess");
        assertNotNull(processInstance);
        System.out.println("pid="+processInstance.getId() + ", pdid=" +processInstance.getProcessDefinitionId());
    }


    @Test
    public void testStartSayHelloToLeaveProcess(){
        //创建流程引擎，使用内存数据库
        ProcessEngine processEngine = ProcessEngineConfiguration
                //.createStandaloneInMemProcessEngineConfiguration()
                .createProcessEngineConfigurationFromResourceDefault()
                .buildProcessEngine();
        //部署流程定义文件
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("diagrams/SayHelloToLeave.bpmn").deploy();

        //验证已部署流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        assertEquals("SayHelloToLeave", processDefinition.getKey());

        //启动流程并返回流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String,Object> variables = new HashMap<>();
        variables.put("applyUser","employee1张三");
        variables.put("days",3);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "SayHelloToLeave",variables);
        assertNotNull(processInstance);
        System.out.println("pid="+processInstance.getId() + ", pdid=" +processInstance.getProcessDefinitionId());

        //领导审批
        TaskService taskService = processEngine.getTaskService();
        Task taskOfDeptLeader = taskService.createTaskQuery()
                .taskCandidateGroup("deptLeader").singleResult();
        assertNotNull(taskOfDeptLeader);
        assertEquals("领导审批", taskOfDeptLeader.getName());

        taskService.claim(taskOfDeptLeader.getId(), "leaderUser");  //签收此任务
        variables = new HashMap<>();
        variables.put("approved",true);
        taskService.complete(taskOfDeptLeader.getId(), variables);  //完成任务

        taskOfDeptLeader = taskService.createTaskQuery()
                .taskCandidateGroup("deptLeader").singleResult();
        assertNull(taskOfDeptLeader);

        HistoryService historyService = processEngine.getHistoryService();
        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
        assertEquals(1, count);
    }
}
