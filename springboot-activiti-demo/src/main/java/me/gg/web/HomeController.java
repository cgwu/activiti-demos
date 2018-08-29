package me.gg.web;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 18-8-27.
 */
@Slf4j
@RestController
public class HomeController {
//    private static Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    IdentityService identityService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @Autowired
    FormService formService;
    @Autowired
    HistoryService historyService;
    @Autowired
    ManagementService managementService;


    @RequestMapping("/")
    public String index() {
        log.info("Home::index() called.");
        return "Hello中国";
    }

    @RequestMapping("/startOneTask")
    public String startOneTask() {
        System.out.println("Number of process definitions : "
                + repositoryService.createProcessDefinitionQuery().count());
        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
        runtimeService.startProcessInstanceByKey("oneTaskProcess", "bizKey业务键");
        String result = ("Number of tasks after process start: " + taskService.createTaskQuery().count());
        System.out.println(result);
        return result;
    }

    @RequestMapping("/startResume")
    public String startResume() {
        Map<String,Object> variables = new HashMap<>();
        variables.put("applyUser","employee1张三");
        variables.put("days",3);
        variables.put("fee",314.56);

        identityService.setAuthenticatedUserId("白秉德");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("MyServiceTaskProcess",
                "MyServiceTaskProcess bizKey业务键",variables);
        return "OK, ProcessId: " + processInstance.getId();
    }


    @RequestMapping("/startExpressionDemo")
    public String startExpressionDemo() {
        String name="小三";
        Map<String,Object> variables = new HashMap<>();
        variables.put("name",name);

        identityService.setAuthenticatedUserId("启动人小李");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ExpressDemoProcess",
                "ExpressDemoProcess业务键001",variables);

        String authenticatedUserIdForTest = runtimeService.getVariable(processInstance.getId(),"authenticatedUserIdForTest").toString();
        log.info("当前操作人 authenticatedUserIdForTest:{}",authenticatedUserIdForTest);

        String returnValue = runtimeService.getVariable(processInstance.getId(),"returnValue").toString();
        log.info("函数返回值 returnValue:{}",returnValue);

        String processedBusinessKey = runtimeService.getVariable(processInstance.getId(),"processedBusinessKey").toString();
        log.info("业务键 processedBusinessKey:{}",processedBusinessKey);

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        String setByTask = taskService.getVariable(task.getId(),"setByTask").toString();
        log.info("SetByTask:{}", setByTask);

//        log.info("isEnded: {}", processInstance.isEnded());
//        taskService.claim(task.getId(),"foo user");
//        taskService.complete(task.getId());
        log.info("isEnded: {}", processInstance.isEnded());

        return "startExpressionDemo OK, ProcessId: " + processInstance.getId();
    }

}
