/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.HitsDTO;
import com.cafa.pdf.core.commom.dto.ReadingDTO;
import com.cafa.pdf.core.service.TreatiseService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.statistics.HitsSearchReq;
import com.cafa.pdf.core.web.request.statistics.ReadingSearchReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class StatisticsController extends ABaseController{

    private final TreatiseService treatiseService;

    @Autowired
    public StatisticsController(TreatiseService treatiseService) {
        this.treatiseService = treatiseService;
    }

    /**
     * 返回点击量的页面
     */
    @GetMapping("/hits/list")
    @RequiresPermissions("statistics:show")
    public ModelAndView hitsList() {
        ModelAndView mv = new ModelAndView("admin/statistics/hits");
        return mv;
    }
    /**
     * 返回阅读量的页面
     */
    @GetMapping("/reading/list")
    @RequiresPermissions("statistics:show")
    public ModelAndView readingList() {
        ModelAndView mv = new ModelAndView("admin/statistics/reading");
        return mv;
    }
    /**
     * 点击量分页查询
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/hits/page")
    @RequiresPermissions("statistics:show")
    @ResponseBody
    public Response hitsPage(BasicSearchReq basicSearchReq, HitsSearchReq hitsSearchReq) {
        log.info("【点击量分页查询】 {}", hitsSearchReq);
        Page<HitsDTO> page = treatiseService.findHitsPage(hitsSearchReq, basicSearchReq);
        return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
    }
    /**
     * 阅读量分页查询
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/reading/page")
    @RequiresPermissions("statistics:show")
    @ResponseBody
    public Response readingPage(BasicSearchReq basicSearchReq, ReadingSearchReq readingSearchReq) {
        log.info("【阅读量分页查询】 {}", readingSearchReq);
        Page<ReadingDTO> page = treatiseService.findReadingPage(readingSearchReq, basicSearchReq);
        return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
    }


}
