/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.TreatiseShowDTO;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.CheckService;
import com.cafa.pdf.core.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class PortalController {

    @GetMapping("index")
    public String index(){
        return "index";
    }
    @Autowired
    private TreatiseSolrRepository treatiseSolrRepository;
    @GetMapping("{q}/result")
    public ModelAndView result(@PathVariable("q") String query){
        ModelAndView mv = new ModelAndView("result");
        if(query.contains(":")){
            //属性检索

        }else {
            //关键字检索
            HighlightPage<TreatiseSolrDoc> page =  treatiseSolrRepository.findByContentOrderById(query,new SolrPageRequest(0,10));
            List<TreatiseShowDTO> list = new ArrayList<>();
            List<TreatiseSolrDoc> treatiseSolrDocs = page.getContent();
            for(TreatiseSolrDoc d : treatiseSolrDocs) {
                TreatiseShowDTO dto = new TreatiseShowDTO();
                StringBuilder sb = new StringBuilder();
                List<HighlightEntry.Highlight> hs = page.getHighlights(d);
                for(HighlightEntry.Highlight h  : hs) {
                    for(String s  : h.getSnipplets()) {
                        sb.append(s);
                    }
                }
                dto.setHighlighted(sb.toString());
                dto.setAuthor(d.getAuthor());
                dto.setCategoryName(d.getCategoryName());
                dto.setTitle(d.getTitle());
                dto.setPublishDate(d.getPublishDate());
                list.add(dto);
            }

            mv.addObject("total",page.getTotalElements());//总个数
            mv.addObject("totalPage",page.getTotalPages());//总页数
            mv.addObject("current",1);//当前页数，总是为1
            mv.addObject("treatises",list);
        }
        return mv;
    }
}
