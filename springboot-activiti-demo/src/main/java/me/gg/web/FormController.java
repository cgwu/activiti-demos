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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form")
public class FormController {
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


    @GetMapping //不指定参数,即为默认路径""
    public ModelAndView index() {
        log.info("FormController.index() called.");

        Map<String,Object> map = new HashMap<>();
        map.put("foo","bar信息");

        FooEntity fe= new FooEntity();
        fe.setId(123L);
        fe.setMsg("信息msg");

        map.put("entity",fe);

        return new ModelAndView("form/index",map);
    }

//    @RequestMapping("/form/start")
    @GetMapping("/print")
    @ResponseBody
    public String print() {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("AskForLeaveFormProcess");
        long cntProc = query.count();
        log.info("流程数:{}", cntProc);

        ProcessDefinition procDef = query.singleResult();
        log.info(procDef.toString());

        StartFormData formData = formService.getStartFormData(procDef.getId());  //拿取流程启动前的表单字段。
        // 默认支持的字段类型: string,long,enum,date,boolean (org.activiti.engine.impl.form.*FormType)
        List<FormProperty> formProperties = formData.getFormProperties();   //获取表单字段值

//        Map<String,String> fromValues = new HashMap<String,String>();
        for(FormProperty formProperty : formProperties){
            log.info("id:{},name:{},type:{}", formProperty.getId(), formProperty.getName(), formProperty.getType());

//            String value = request.getParameter(formProperty.getId());//拿取具体参数值
//            formValues.put(formProperty.getId,value);    //将ID和value存入map中
        }

//        formService.submitStartFormData(processDefinitionId,formValues);//启动流程，提交表单
        return "print done";
    }

    /* 启动流程 */
//    @RequestMapping("/form/start2")
    @GetMapping("/start")
    public ModelAndView start(Model model) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("AskForLeaveFormProcess").latestVersion().singleResult();

        //使用动态表单时生成内容
//        Object rendered = formService.getRenderedStartForm(procDef.getId());
//        log.info("开始表单render: {}",rendered);

        model.addAttribute("foo","bar信息");
        return new ModelAndView( "form/start","m",model);
    }

    @PostMapping("/start")
    @ResponseBody
    public String start(@RequestParam Map<String, Object> mapArgs) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("AskForLeaveFormProcess").latestVersion();
        ProcessDefinition procDef = query.singleResult();
        StartFormData formData = formService.getStartFormData(procDef.getId());  //拿取流程启动前的表单字段。
        // 默认支持的字段类型: string,long,enum,date,boolean (org.activiti.engine.impl.form.*FormType)
        List<FormProperty> formProperties = formData.getFormProperties();   //获取表单字段值

        Map<String,String> formValues = new HashMap<String,String>();
        for(FormProperty formProperty : formProperties){
            log.info("id:{},name:{},type:{}", formProperty.getId(), formProperty.getName(), formProperty.getType());
            String value = mapArgs.getOrDefault(formProperty.getId(),"").toString();  //拿取具体参数值
            formValues.put(formProperty.getId(),value);    //将ID和value存入map中
        }
        identityService.setAuthenticatedUserId("发起人"+mapArgs.get("applyUser").toString());
        ProcessInstance processInstance = formService.submitStartFormData(procDef.getId(),formValues);//启动流程，提交表单
        return "Start process OK: " +processInstance.getId();
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

//            log.info("未签收之前: Task assignee:{}",task.getAssignee());
//            taskService.claim(taskId,"gg");
//            log.info("未签收之后: Task assignee:{}",task.getAssignee()); // ERROR:改变之后需要重新读取task
//            taskService.unclaim(taskId);
//            log.info("取消签收之后: Task assignee:{}",task.getAssignee());

            // 获取已有的表单变量
            Map<String,Object> mapVariables = runtimeService.getVariables(task.getExecutionId());
//            log.info("#mapVariables count: {}", mapVariables.size());
//            for(Map.Entry<String, Object> entry: mapVariables.entrySet()){
//                log.info("key: {}, value:{}", entry.getKey(), entry.getValue().toString());
//            }

//            log.info("task.getProcessDefinitionId(): {}",task.getProcessDefinitionId());
//            Object obj = formService.getRenderedStartForm(task.getProcessDefinitionId());
//            log.info("###obj: {}",obj);


            StartFormData formData = formService.getStartFormData(task.getProcessDefinitionId());
//            TaskFormData formData = formService.getTaskFormData(taskId);
            for(FormProperty formProperty : formData.getFormProperties()){
                log.info("任务表单项: id:{},name:{},type:{},value:{}", formProperty.getId(), formProperty.getName(),
                        formProperty.getType(), /* formProperty.getValue() */ mapVariables.get(formProperty.getId()));
            }

//            Map<String, Object> variables = task.getProcessVariables();
            model.addAttribute("taskId",taskId);
            model.addAttribute("variables", mapVariables);
            log.info("任务ID:{}, procInstId:{}",taskId, procInstId);
        }
        return new ModelAndView( "form/approve","m",model);
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
