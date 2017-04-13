/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web.aop;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author FancyKong
 * @file StatisticsController.java
 * @date 2017/4/12 22:07
 * @since 0.0.1
 */
@RequiresAuthentication
@Controller
@RequestMapping("statistics")
public class StatisticsController {

    @GetMapping
    @RequiresPermissions("statistics:show")
    public ModelAndView show(){
        ModelAndView modelAndView = new ModelAndView("admin/statistics/list");
        return modelAndView;
    }
}
