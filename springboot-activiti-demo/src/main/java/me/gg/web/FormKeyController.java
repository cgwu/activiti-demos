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

}
