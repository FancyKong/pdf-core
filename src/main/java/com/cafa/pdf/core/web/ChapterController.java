package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.ChapterSolrRepository;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import com.cafa.pdf.core.web.response.Response;
import com.google.common.base.Throwables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/20 0:20
 */
@Controller
@RequestMapping("chapter")
@RequiresAuthentication
public class ChapterController extends ABaseController {
    
    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * 删除
     * @param chapterId ID
     * @return JSON
     */
    @DeleteMapping("/{chapterId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("chapterId") Long chapterId) {
        //TODO 同时要删除solr中的数据
        chapterService.delete(chapterId);
        return buildResponse(Boolean.TRUE, "删除 成功", null);
    }

    /**
     * 更改信息
     * @param chapterReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    public ModelAndView update(@Validated ChapterReq chapterReq, BindingResult bindingResult) {
        log.info("【更改信息】 {}", chapterReq);

        ModelAndView mv = new ModelAndView("admin/chapter/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (chapterReq == null || chapterReq.getId() == null) {
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("chapter", chapterReq);
            return mv;
        }

        try {
            //TODO 同时要更新solr中的数据
            chapterService.update(chapterReq);
            mv.addObject("chapter", chapterService.findById(chapterReq.getId()));
            errorMap.put("msg", "修改成功");
        } catch (Exception e) {
            errorMap.put("msg", "系统繁忙");
            log.error("修改错误:{}", Throwables.getStackTraceAsString(e));
        }
        return mv;
    }

    /**
     * 保存
     * @param chapterReq 保存的信息
     * @return Response
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ChapterReq chapterReq, @RequestParam("pdf") MultipartFile multipartFile) {
        log.info("【保存】 {}", chapterReq);
        if (multipartFile.isEmpty()) {
            return buildResponse(Boolean.FALSE, "请上传文件", null);
        }
        Chapter chapter = chapterService.saveChapter(chapterReq,multipartFile);
        return buildResponse(Boolean.TRUE, "保存成功", chapter);
    }

    /**
     * 保存
     * @param chapter 保存的信息
     * @return Response
     */
    @PostMapping("/updateNoFile")
    @ResponseBody
    public Response saveNoFile(@RequestBody Chapter chapter) {
        log.info("【保存】 {}", chapter);
        Chapter updated = chapterService.update(chapter);
        return buildResponse(Boolean.TRUE, "保存成功", updated);
    }

}
