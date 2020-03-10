package com.realtime.controller;

import com.realtime.bean.ResBean;
import com.realtime.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private DemoService demoService;

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping(value = "/firstDemo")
    public ModelAndView firstDemo() {
        return new ModelAndView("firstDemo");
    }

    @PostMapping("/demo/getData")
    public List<ResBean> getDatas() {
        return demoService.getResBeans();
    }

    @GetMapping(value = "/seconddemo")
    public ModelAndView secondDemo() {
        return new ModelAndView("seconddemo");
    }

}
