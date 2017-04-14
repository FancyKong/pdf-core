/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.web.response.Response;
import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.service.TreatiseService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author FancyKong
 * @file TreatiseController.java
 * @date 2017/4/12 22:05
 * @since 0.0.1
 */
@Controller
@RequestMapping("treatise")
@RequiresAuthentication
public class TreatiseController extends ABaseController{

    @Autowired
    TreatiseService treatiseService;

    @GetMapping
    @RequiresPermissions("treatise:show")
    public ModelAndView show(){
        ModelAndView mv = new ModelAndView("admin/treatise/list");
        return mv;
    }

    /**
     * 分页查询用户
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/page")
    @ResponseBody
    public Response toPage(BasicSearchReq basicSearchReq, TreatiseSearchReq treatiseSearchReq){

        try {
            Page<TreatiseDTO> page = treatiseService.findAll(treatiseSearchReq, basicSearchReq);

            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取用户列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 返回新增著作的页面
     */
    @GetMapping("/add")
    @RequiresPermissions("user:add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/user/add");
        return mv;
    }

    /**
     * 返回修改著作信息的页面
     */
    @GetMapping("/{userId}/update")
    @RequiresPermissions("user:update")
    public ModelAndView updateForm(@PathVariable("userId") Long userId){
        ModelAndView mv = new ModelAndView("admin/user/edit");
        return mv;
    }
}
