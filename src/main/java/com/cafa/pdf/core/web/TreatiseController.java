/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.service.TreatiseService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    private final TreatiseService treatiseService;
    private final TreatiseCategoryService treatiseCategoryService;

    @Autowired
    public TreatiseController(TreatiseService treatiseService,
                              TreatiseCategoryService treatiseCategoryService) {
        this.treatiseService = treatiseService;
        this.treatiseCategoryService = treatiseCategoryService;
    }

    /**
     * 全局返回著作类别
     * @return List<TreatiseCategory>
     */
    @ModelAttribute("categories")
    public List<TreatiseCategory> categories(){
        return treatiseCategoryService.findAll();
    }

    @GetMapping
    @RequiresPermissions("treatise:show")
    public ModelAndView show(){
        ModelAndView mv = new ModelAndView("admin/treatise/list");
        return mv;
    }

    @GetMapping("pdf")
    public ResponseEntity<byte[]> showPDF() throws IOException {
        File file = new File("D:/apache-solr-ref-guide-5.5.pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "D:a.pdf");
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(
                FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }

    /**
     * 分页查询
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
            log.error("获取列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 返回新增著作的页面
     */
    @GetMapping("/add")
    @RequiresPermissions("treatise:add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/treatise/add");
        return mv;
    }

    /**
     * 返回修改著作信息的页面
     */
    @GetMapping("/{treatiseId}/update")
    @RequiresPermissions("treatise:update")
    public ModelAndView updateForm(@PathVariable("treatiseId") Long treatiseId){
        ModelAndView mv = new ModelAndView("admin/treatise/edit");
        Treatise treatise = treatiseService.findById(treatiseId);
        mv.addObject("treatise", treatise);
        return mv;
    }


    //TODO 先隐藏功能
    /*
    *//**
     * 删除
     * @param treatiseId ID
     * @return JSON
     *//*
    @DeleteMapping("/{treatiseId}/delete")
    @ResponseBody
    @RequiresPermissions("treatise:delete")
    public Response delete(@PathVariable("treatiseId") Long treatiseId){
        try {
            treatiseService.delete(treatiseId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    *//**
     * 更改信息
     * @param treatiseUpdateReq 更新信息
     * @return ModelAndView
     *//*
    @PostMapping("/update")
    @RequiresPermissions("treatise:update")
    public ModelAndView update(@Validated TreatiseUpdateReq treatiseUpdateReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/treatise/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if(treatiseUpdateReq == null || treatiseUpdateReq.getId() == null){
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatise", treatiseUpdateReq);
        }else {
            try {
                treatiseService.update(treatiseUpdateReq);
                mv.addObject("treatise", treatiseService.findById(treatiseUpdateReq.getId()));
                errorMap.put("msg", "修改成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("修改错误:{}", e.getMessage());
            }
        }
        return mv;
    }

    *//**
     * 保存
     * @param treatiseSaveReq 保存的信息
     * @return ModelAndView
     *//*
    @PostMapping("/save")
    @RequiresPermissions("treatise:add")
    public ModelAndView save(@Validated TreatiseSaveReq treatiseSaveReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/treatise/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatise", treatiseSaveReq);
        }else {
            try {
                treatiseService.save(treatiseSaveReq);
                errorMap.put("msg", "添加成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("添加失败:{}", e.getMessage());
            }
        }
        return mv;
    }*/
    
    
}
