package me.gg.web;

import lombok.extern.slf4j.Slf4j;
import me.gg.model.FooEntity;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/formkey")
public class FormKeyController {
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


    @GetMapping("/start")
    public ModelAndView start(Model model) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave_formkey").latestVersion().singleResult();

        //使用外置表单时生成内容
        Object rendered = formService.getRenderedStartForm(procDef.getId());
        log.info("开始表单render: {}",rendered);

        model.addAttribute("startForm",rendered.toString());
        return new ModelAndView( "formkey/start","m",model);
    }

    @PostMapping("/start")
    @ResponseBody
    public String start(HttpServletRequest request) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave_formkey").latestVersion();
        ProcessDefinition procDef = query.singleResult();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> formValues = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            formValues.put(entry.getKey(), entry.getValue()[0]);
        }
        /*
        StartFormData formData = formService.getStartFormData(procDef.getId());  //拿取流程启动前的表单字段。
        // 默认支持的字段类型: string,long,enum,date,boolean (org.activiti.engine.impl.form.*FormType)
        List<FormProperty> formProperties = formData.getFormProperties();   //获取表单字段值


        for(FormProperty formProperty : formProperties){
            log.info("id:{},name:{},type:{}", formProperty.getId(), formProperty.getName(), formProperty.getType());
            String value = mapArgs.getOrDefault(formProperty.getId(),"").toString();  //拿取具体参数值
            formValues.put(formProperty.getId(),value);    //将ID和value存入map中
        }
        */

        String applyUser = request.getParameter("applyUser").toString();
        identityService.setAuthenticatedUserId("发起人" + applyUser);
        ProcessInstance processInstance = formService.submitStartFormData(procDef.getId(),
                "bizKey:" + applyUser, formValues);     //启动流程，提交表单
        return "Start process OK: " + processInstance.getId();
    }

    @GetMapping("/claim/{id}")
    @ResponseBody
    public String claim(@PathVariable("id") String taskId) {
        taskService.claim(taskId,"gg");
        return "Claim done:"+taskId.toString();
    }

    @GetMapping("/unclaim/{id}")
    @ResponseBody
    public String unclaim(@PathVariable("id") String taskId) {
        taskService.unclaim(taskId);
        return "UnClaim done:"+taskId.toString();
    }


    //领导批准
    @GetMapping("/approve")
    public ModelAndView approve(Model model) {
        // Candidate是等签收,Assigned是已签收的.
        //Task task = taskService.createTaskQuery().taskCandidateUser("gg").singleResult();
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned("gg").singleResult();
        if(task == null){
            log.info("无待办任务");
        } else {
            String taskId = task.getId();
            String procInstId = task.getProcessInstanceId();

//            runtimeService.createExecutionQuery().
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(procInstId).singleResult();
            log.info("业务键ID:{} ",processInstance.getBusinessKey());

            // 获取已有的表单变量
            Map<String,Object> mapVariables = runtimeService.getVariables(task.getExecutionId());
//            log.info("#mapVariables count: {}", mapVariables.size());
//            for(Map.Entry<String, Object> entry: mapVariables.entrySet()){
//                log.info("key: {}, value:{}", entry.getKey(), entry.getValue().toString());
//            }

            log.info("task.getProcessDefinitionId(): {}",task.getProcessDefinitionId());
            Object obj = formService.getRenderedStartForm(task.getProcessDefinitionId());
            log.info("#RenderedStartForm 起渲染开始表单内容#: {}",obj);


            /*
            // 外置表单无FormData
            StartFormData formData = formService.getStartFormData(task.getProcessDefinitionId());
            log.info("formData map size:{} ", formData.getFormProperties().size());
//            TaskFormData formData = formService.getTaskFormData(taskId);
            for(FormProperty formProperty : formData.getFormProperties()){
                log.info("任务表单项: id:{},name:{},type:{},value:{}", formProperty.getId(), formProperty.getName(),
                        formProperty.getType(), mapVariables.get(formProperty.getId()));
            }
            */

//            Map<String, Object> variables = task.getProcessVariables();
            model.addAttribute("taskId",taskId);
            model.addAttribute("variables", mapVariables);
            model.addAttribute("assignee",task.getAssignee());
            log.info("任务ID:{}, procInstId:{}",taskId, procInstId);
        }
        return new ModelAndView( "formkey/approve","m",model);
    }

    //领导批准
    @PostMapping("/approve")
    @ResponseBody
    public String approve(@RequestParam Map<String, Object> mapArgs) {
        boolean isApproved = "true".equals(mapArgs.get("approval"));
        log.info("approval:{}", isApproved);

        Map<String, String> formValues = new HashMap<>();
        formValues.put("approval", String.valueOf(isApproved));

        String taskId = mapArgs.get("taskId").toString();
        formService.submitTaskFormData(taskId, formValues);
        return "approve:" + taskId;
    }

}
