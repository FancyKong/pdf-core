/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.ChapterDTO;
import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.service.AuthorService;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.service.TreatiseService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSaveReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseUpdateReq;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author FancyKong
 * @file TreatiseController.java
 * @date 2017/4/12 22:05
 * @since 0.0.1
 */
@Controller
@RequestMapping("treatise")
@RequiresAuthentication
public class TreatiseController extends ABaseController {

    private final TreatiseService treatiseService;
    private final TreatiseCategoryService treatiseCategoryService;
    private final ChapterService chapterService;

    private final AuthorService authorService;

    @Autowired
    public TreatiseController(TreatiseService treatiseService,
                              TreatiseCategoryService treatiseCategoryService,
                              ChapterService chapterService, AuthorService authorService) {
        this.treatiseService = treatiseService;
        this.treatiseCategoryService = treatiseCategoryService;
        this.chapterService = chapterService;
        this.authorService = authorService;
    }

    /**
     * 全局返回著作类别
     * @return List<TreatiseCategory>
     */
    @ModelAttribute("categories")
    public List<TreatiseCategory> categories() {
        return treatiseCategoryService.findParent();
    }

    @ModelAttribute("childCategories")
    public List<TreatiseCategory> childCategories() {
        return treatiseCategoryService.findChildren(1L);
    }

    @ModelAttribute("language")
    public List<Language> language() {
        return Arrays.asList(Language.values());
    }

    @ModelAttribute("publicationMode")
    public List<PublicationMode> publicationMode() {
        return Arrays.asList(PublicationMode.values());
    }

    @ModelAttribute("authors")
    public List<Author> authors(){
        return authorService.findAll();
    }

    @GetMapping
    @RequiresPermissions("treatise:show")
    public ModelAndView show() {
        ModelAndView mv = new ModelAndView("admin/treatise/list");
        return mv;
    }

    @GetMapping("{pid}/children")
    @ResponseBody
    public List<TreatiseCategory> children(@PathVariable("pid") Long pid){
        return treatiseCategoryService.findChildren(pid);
    }
    /**
     * 分页查询
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/page")
    @ResponseBody
    public Response toPage(BasicSearchReq basicSearchReq, TreatiseSearchReq treatiseSearchReq) {
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
    public ModelAndView addForm() {
        ModelAndView mv = new ModelAndView("admin/treatise/add");
        return mv;
    }

    /**
     * 返回修改著作信息的页面
     */
    @GetMapping("/{treatiseId}/update")
    @RequiresPermissions("treatise:update")
    public ModelAndView updateForm(@PathVariable("treatiseId") Long treatiseId) {
        ModelAndView mv = new ModelAndView("admin/treatise/edit");
        Treatise treatise = treatiseService.findById(treatiseId);
        mv.addObject("treatise", treatise);
        TreatiseCategory category = treatiseCategoryService.findById(treatise.getCategoryId());
        mv.addObject("thisCategory",category);
        mv.addObject("categories",treatiseCategoryService.findParent());
        mv.addObject("childCategories",treatiseCategoryService.findChildren(category.getPid()));
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatiseId);
        mv.addObject("chapters", chapters);
        return mv;
    }

    /**
     * 返回修改著作信息的详情页面
     */
    @GetMapping("/{treatiseId}/info")
    @RequiresPermissions("treatise:update")
    public ModelAndView infoForm(@PathVariable("treatiseId") Long treatiseId) {
        ModelAndView mv = new ModelAndView("admin/treatise/info");
        Treatise treatise = treatiseService.findById(treatiseId);
        mv.addObject("treatise", treatise);
        TreatiseCategory category = treatiseCategoryService.findById(treatise.getCategoryId());
        mv.addObject("thisCategory",category);
        mv.addObject("categories",treatiseCategoryService.findParent());
        mv.addObject("childCategories",treatiseCategoryService.findChildren(category.getPid()));
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatiseId);
        mv.addObject("chapters", chapters);
        return mv;
    }

    /**
     * 删除
     * @param treatiseId ID
     * @return JSON
     */
    @DeleteMapping("/{treatiseId}/delete")
    @ResponseBody
    @RequiresPermissions("treatise:delete")
    public Response delete(@PathVariable("treatiseId") Long treatiseId) {
        try {
            treatiseService.delete(treatiseId);
            //TODO 级联删除，著作章节，solr等, 应该放在同一个事务下
            chapterService.deleteAllByTreatiseId(treatiseId);

            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改著作信息
     * @param treatiseUpdateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    @RequiresPermissions("treatise:update")
    public ModelAndView update(@Validated TreatiseUpdateReq treatiseUpdateReq, BindingResult bindingResult) {
        log.info("【更改】 {}", treatiseUpdateReq);

        ModelAndView mv = new ModelAndView("admin/treatise/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (treatiseUpdateReq == null || treatiseUpdateReq.getId() == null) {
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatise", treatiseUpdateReq);
        } else {
            try {
                treatiseService.update(treatiseUpdateReq);
                mv.addObject("treatise", treatiseService.findById(treatiseUpdateReq.getId()));
                mv.addObject("chapters", chapterService.findByTreatiseId(treatiseUpdateReq.getId()));
                errorMap.put("msg", "修改成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("修改错误:{}", e.getMessage());
            }
        }
        return mv;
    }

    /**
     * 保存
     * @param treatiseSaveReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    @RequiresPermissions("treatise:add")
    @ResponseBody
    public Response save(@Validated TreatiseSaveReq treatiseSaveReq, BindingResult bindingResult) {
        log.info("【保存】 {}", treatiseSaveReq);
        try {
            TreatiseDTO treatise = treatiseService.save(treatiseSaveReq);
            //TODO call this method to save in solr next
            // 现在测试，没有发到solr
            // saveTreatiseInSolr("abc");

            return buildResponse(Boolean.TRUE, "保存成功", treatise);
        } catch (Exception e) {
            log.error("添加失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }


    @GetMapping("pdf")
    public ResponseEntity<byte[]> showPDF() throws IOException {
        File file = new File("D:a.pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "D:a.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(
                FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }

    //TODO 文件存放路径
    private static final String FILE_PATH = "/pdf-core/file/";

    @GetMapping("page/{treatiseId}/{pageNumber}")
    public ResponseEntity<byte[]> page(@PathVariable("treatiseId")Long treatiseId,@PathVariable("pageNumber") int pageNumber) throws IOException {
        //TODO　select from chapter where treatiseId is order by seq
        List<Integer> pageNums = Arrays.asList(100,100,100,100,100,100,13);
        int seq = 1;
        for(Integer i : pageNums) {
            if(pageNumber>i){
                pageNumber-=i;
                seq++;
            }else break;
        }
        File file = new File(FILE_PATH+treatiseId+"/"+seq+"/"+pageNumber+".pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "x.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(
                FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }
}
