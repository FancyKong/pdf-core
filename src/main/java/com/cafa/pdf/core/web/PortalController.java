/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.ChapterDTO;
import com.cafa.pdf.core.commom.dto.TreatiseShowDTO;
import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.entity.*;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.AuthorService;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.service.TreatiseService;
import com.cafa.pdf.core.util.SessionUtil;
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
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private AuthorService authorService;

    @GetMapping("{id}/detail")
    public ModelAndView detail(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("detail");
        Treatise treatise = treatiseService.findById(id);
        treatise.setDescription(treatise.getDescription().replaceAll("\\r\n","<br/>"));

        // 该著作点击量加一
        treatiseService.addHitsForTreatise(id, 1);

        modelAndView.addObject("treatise",treatise);
        modelAndView.addObject("language", Language.valueOf(treatise.getLanguage()));
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatise.getId());
        modelAndView.addObject("chapters",chapters);
        TreatiseCategory category = treatiseCategoryService.findById(treatise.getCategoryId());
        modelAndView.addObject("category",category);
        Author author = authorService.findById(treatise.getAuthorId());
        modelAndView.addObject("author",author);
        String[] keywords = treatise.getKeywords().split(";");
        modelAndView.addObject("keywords",keywords);
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
            @RequestParam(value = "p",required = false,defaultValue = "1") Integer page,
            @RequestParam(value = "o",required = false,defaultValue = "relativity") String order){
        ModelAndView mv = new ModelAndView("result");
        HighlightPage<TreatiseSolrDoc> docs = null;
        List<TreatiseShowDTO> list = null;
        if("".equals(type)){
            //关键字检索
            if(order.equals("relativity")){
                docs  =  treatiseSolrRepository.findByContent(query,new SolrPageRequest(page-1,size));
            }else if(order.equals("date")){
                docs =  treatiseSolrRepository.findByContentOrderByPublishDateAsc(query,new SolrPageRequest(page-1,size));
            }
        }else if("keywords".equals(type)){
            //查询关键词

        }else if("author".equals(type)){
            //查询作者
            if(order.equals("relativity")){
                docs  =  treatiseSolrRepository.findByAuthor(query,new SolrPageRequest(page-1,size));
            }else if(order.equals("date")){
                docs =  treatiseSolrRepository.findByAuthorOrderByPublishDateAsc(query,new SolrPageRequest(page-1,size));
            }
            query="作者:"+query;
        }else if("title".equals(type)){
            //查询书名
            if(order.equals("relativity")){
                docs = treatiseSolrRepository.findByTitle(query,new SolrPageRequest(page-1,size));
            }else if(order.equals("date")){
                docs = treatiseSolrRepository.findByTitleOrderByPublishDateAsc(query,new SolrPageRequest(page-1,size));
            }
            query="著作名:"+query;
        }else if("category".equals(type)){
            if(order.equals("relativity")){
                docs = treatiseSolrRepository.findByCategoryId(Long.parseLong(query),new SolrPageRequest(page-1,size));
            }else if(order.equals("date")){
                docs = treatiseSolrRepository.findByCategoryIdOrderByPublishDateAsc(Long.parseLong(query),new SolrPageRequest(page-1,size));
            }
            query="";
        }
        else if("pc".equals(type)){
            if(order.equals("relativity")){
                docs = treatiseSolrRepository.findByPCategoryId(Long.parseLong(query),new SolrPageRequest(page-1,size));
            }else if(order.equals("date")){
                docs = treatiseSolrRepository.findByPCategoryIdOrderByPublishDateAsc(Long.parseLong(query),new SolrPageRequest(page-1,size));
            }
            query="";
        }
        if(docs == null){
            throw new ServiceException("请传入查询参数");
        }
        list = new ArrayList<>();
        List<TreatiseSolrDoc> treatiseSolrDocs = docs.getContent();
        for(TreatiseSolrDoc d : treatiseSolrDocs) {
            TreatiseShowDTO dto = new TreatiseShowDTO();
            StringBuilder sb = new StringBuilder("...");
            StringBuilder title = new StringBuilder();
            List<HighlightEntry.Highlight> hs = docs.getHighlights(d);
            for(HighlightEntry.Highlight h  : hs) {
                if("title".equals(h.getField().getName())){
                    for(String s  : h.getSnipplets()) {
                        title.append(s);
                    }
                }else{
                    for(String s  : h.getSnipplets()) {
                        sb.append(s).append("...");
                    }
                }
            }
            sb.append(d.getDescription());
            if(title.length() < 1){
                title.append(d.getTitle());
            }
            int end = sb.length()>500?500:sb.length();
            dto.setHighlighted(sb.toString().substring(0,end)+"...");
            dto.setAuthor(d.getAuthor());
            dto.setCategoryName(d.getCategoryName());
            dto.setTitle(title.toString());
            dto.setPublishDate(d.getPublishDate());
            dto.setId(Long.parseLong(d.getId()));
            list.add(dto);
        }
        mv.addObject("typeName",query);
        mv.addObject("total",docs.getTotalElements());//总个数
        mv.addObject("totalPage",docs.getTotalPages());//总页数
        mv.addObject("treatises",list);

        mv.addObject("current",page);//当前页数，总是为1
        mv.addObject("size",size);
        mv.addObject("query",query);
        mv.addObject("type",type);
        mv.addObject("order",order);
        return mv;
    }

    @RequestMapping("/reading/{treatiseId}")
    public ModelAndView readOnline(@PathVariable("treatiseId") Long treatiseId){
        ModelAndView mv = new ModelAndView("read");
        Treatise treatise = treatiseService.findById(treatiseId);
        List<Chapter> chapters = chapterService.getByTreatiseId(treatiseId);
        Customer customer = SessionUtil.getCustomer();
        if(customer == null){
            mv.addObject("status",0);
        }else{
            mv.addObject("status",1);
        }
        mv.addObject("treatise",treatise);
        int page = 1;
        int totalPage = 0;
        int publicPage = 0;
        for(int seq = 1 ; seq <= chapters.size();seq++){
            Chapter cur = chapters.get(seq-1);
            if(cur.getPrivacy() == 1){
                publicPage += cur.getPage();
            }
            totalPage+=cur.getPage();
        }
        mv.addObject("curPage",page);
        mv.addObject("totalPage",totalPage);
        mv.addObject("publicPage",publicPage);
        mv.addObject("lastSeq",chapters.size());

        // 该著作阅读量加一
        treatiseService.addReadingForTreatise(treatiseId, 1);
        return mv;
    }

    @RequestMapping("/reading/{treatiseId}/{chapterSeq}")
    public ModelAndView readChapter(@PathVariable("treatiseId") Long treatiseId,
                                    @PathVariable("chapterSeq") Integer chapterSeq){
        ModelAndView mv = new ModelAndView("readChapter");
        Treatise treatise = treatiseService.findById(treatiseId);
        List<Chapter> chapters = chapterService.getByTreatiseId(treatiseId);
        Chapter chapter = chapters.get(chapterSeq-1);
        Customer customer = SessionUtil.getCustomer();
        if (0 == chapter.getPrivacy() && customer == null) {
            // 该章节加密并且 未登录
            ModelAndView loginMv = new ModelAndView("redirect:/customer/login");
            SessionUtil.add("url", "/reading/" + treatise.getId() + "/" + chapterSeq);
            SessionUtil.add("msg", "该章节需要登陆的会员才能阅读，请登录或注册");
            return loginMv;
        }
        mv.addObject("treatise",treatise);
        mv.addObject("chapter",chapter);
        int page = 0;
        int totalPage = 0;
        for(int seq = 1 ; seq <= chapters.size();seq++){
            Chapter cur = chapters.get(seq-1);
            if(chapterSeq > cur.getSeq()){
                page+=cur.getPage();
            }
            totalPage+=cur.getPage();
        }
        mv.addObject("basePage",page);
        mv.addObject("totalPage",totalPage);
        mv.addObject("lastSeq",chapters.size());

        // 该著作阅读量加一
        treatiseService.addReadingForTreatise(treatiseId, 1);
        return mv;
    }
}
