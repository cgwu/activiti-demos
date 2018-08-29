package me.gg.web;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import me.gg.model.FooEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form")
public class FormController {

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
    public String formStart(Model model) {
        model.addAttribute("foo","bar信息");
        return "form/start";
    }

//    @RequestMapping("/form/start2")
    @GetMapping("/start")
    public ModelAndView start(Model model) {
        model.addAttribute("foo","bar信息");
        return new ModelAndView( "form/start","m",model);
    }

}
