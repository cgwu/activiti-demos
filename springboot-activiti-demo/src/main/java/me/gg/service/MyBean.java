package me.gg.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by sam on 18-8-28.
 */
@Component("myBean")
public class MyBean implements Serializable {
    private static Logger log = LoggerFactory.getLogger(MyBean.class);

    public void print() {
        log.info("MyBean.print() called.");
    }

    public String print(String name) {
        log.info("MyBean.print(String name) called: " + name);
        return name + ", added by print(String name)";
    }

    //接收名为execution的引擎内置变量
    public String printBkey(DelegateExecution execution) {
        String processBusinessKey = execution.getProcessBusinessKey();
        log.info("Process instance id: {}, business key:{}", execution.getProcessInstanceId(), processBusinessKey);
        return processBusinessKey;
    }

    //接收名为task的引擎内置变量
    public void invokeTask(DelegateTask task){
        task.setVariable("setByTask","I'm setted by DelegateTask,"+task.getVariable("name"));
    }

    //接收名为execution的引擎内置变量
    public void endProcess(DelegateExecution execution) {
        String processBusinessKey = execution.getProcessBusinessKey();
        log.info("### 流程结束 ### Process instance id: {}, business key:{}", execution.getProcessInstanceId(), processBusinessKey);
    }
}
