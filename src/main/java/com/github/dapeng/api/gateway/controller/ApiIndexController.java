package com.github.dapeng.api.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
/**
 * @author with struy.
 * Create by 2018/4/17 14:21
 * email :yq1724555319@gmail.com
 */

@Controller
@RequestMapping("/")
public class ApiIndexController{
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
