package com.example.demo.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.example.demo.service.IStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 张海鹏
 * @since 2020-03-08
 */
@Controller
@RequestMapping("/students")
public class StudentsController {

    @Autowired
    private IStudentsService iStudentsService;
    @RequestMapping("/List")
    private String getList(Model m){
        Wrapper wrapper = new EntityWrapper();
        List list = iStudentsService.selectList(wrapper);
        m.addAttribute("list",list);
        System.out.println(list);
        return "/list";
    }
}
