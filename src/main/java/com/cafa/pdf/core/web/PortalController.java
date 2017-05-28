/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.TreatiseShowDTO;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.dal.entity.TreatiseReading;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.CheckService;
import com.cafa.pdf.core.service.SysConfigService;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.service.TreatiseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FancyKong
 * @file PortalController.java
 * @date 2017/5/23 21:32
 * @since 0.0.1
 */
@Controller
@RequestMapping("/")
@Slf4j
public class PortalController {

    @GetMapping("index")
    public String index(){
        return "index";
    }
    @Autowired
    private TreatiseSolrRepository treatiseSolrRepository;
    @Autowired
    private TreatiseCategoryService treatiseCategoryService;
    @Autowired
    private TreatiseService treatiseService;

    @GetMapping("{id}/detail")
    public ModelAndView detail(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("detail");
        return modelAndView;
    }

    /**
     * 全局返回著作类别
     * @return List<TreatiseCategory>
     */
    @ModelAttribute("categories")
    public List<TreatiseCategory> categories() {
        return treatiseCategoryService.findParent();
    }

    /**
     * 全局返回阅读热度排行榜
     * @return List<Treatise>
     */
    @ModelAttribute("treatisesTop")
    public List<TreatiseReading> treatisesTop(){
        return treatiseService.treatisesHot();
    }
    /**
     * 首页的检索
     * @param type 检索方式
     * @param query 检索字符串
     * @param size 每页的个数
     * @param page 第几页
     * @return
     */
    @GetMapping("/result")
    public ModelAndView result(
            @RequestParam(value = "t",required = false,defaultValue = "") String type,
            @RequestParam(value = "q") String query,
            @RequestParam(value = "s",required = false,defaultValue = "10") Integer size,
            @RequestParam(value = "p",required = false,defaultValue = "1") Integer page){
        ModelAndView mv = new ModelAndView("result");
        if("".equals(type)){
            //关键字检索
            HighlightPage<TreatiseSolrDoc> docs =  treatiseSolrRepository.findByContentOrderById(query,new SolrPageRequest(page-1,size));
            List<TreatiseShowDTO> list = new ArrayList<>();
            List<TreatiseSolrDoc> treatiseSolrDocs = docs.getContent();
            for(TreatiseSolrDoc d : treatiseSolrDocs) {
                TreatiseShowDTO dto = new TreatiseShowDTO();
                StringBuilder sb = new StringBuilder("...");
                List<HighlightEntry.Highlight> hs = docs.getHighlights(d);
                for(HighlightEntry.Highlight h  : hs) {
                    for(String s  : h.getSnipplets()) {
                        sb.append(s).append("...");
                    }
                }
                sb.append(d.getDescription());
                int end = sb.length()>500?500:sb.length();
                dto.setHighlighted(sb.toString().substring(0,end)+"...");
                dto.setAuthor(d.getAuthor());
                dto.setCategoryName(d.getCategoryName());
                dto.setTitle(d.getTitle());
                dto.setPublishDate(d.getPublishDate());
                dto.setId(Long.parseLong(d.getId()));
                list.add(dto);
            }
            mv.addObject("typeName",query);
            mv.addObject("total",docs.getTotalElements());//总个数
            mv.addObject("totalPage",docs.getTotalPages());//总页数
            mv.addObject("treatises",list);
        }else if("keywords".equals(type)){
            //查询关键词
            mv.addObject("typeName","关键词:"+query);
            mv.addObject("total",100);
        }else if("author".equals(type)){
            //查询作者
            //Long categoryId = Long.parseLong(query);
            mv.addObject("typeName","作者:"+query);
            mv.addObject("total",10);
        }else if("title".equals(type)){
            //查询书名
            mv.addObject("typeName","著作名:"+query);
            mv.addObject("total",10);
        }
        mv.addObject("current",page);//当前页数，总是为1
        mv.addObject("size",size);
        mv.addObject("query",query);
        mv.addObject("type",type);
        return mv;
    }

}
