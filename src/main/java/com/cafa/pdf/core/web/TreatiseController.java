/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.*;
import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.service.*;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.statistics.HitsSearchReq;
import com.cafa.pdf.core.web.request.statistics.ReadingSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSaveCoreReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseUpdateReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
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
public class TreatiseController extends ABaseController {

    private final TreatiseService treatiseService;
    private final TreatiseCategoryService treatiseCategoryService;
    private final ChapterService chapterService;
    private final AuthorService authorService;
    private final SysConfigService sysConfigService;

    @Autowired
    public TreatiseController(TreatiseService treatiseService,
                              TreatiseCategoryService treatiseCategoryService,
                              ChapterService chapterService, AuthorService authorService, SysConfigService sysConfigService) {
        this.treatiseService = treatiseService;
        this.treatiseCategoryService = treatiseCategoryService;
        this.chapterService = chapterService;
        this.authorService = authorService;
        this.sysConfigService = sysConfigService;
    }

    /**
     * 全局返回著作类别
     *
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
    public List<Author> authors() {
        return authorService.findAll();
    }

    @GetMapping({"", "/list"})
    @RequiresPermissions("treatise:show")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("admin/treatise/list");
        return mv;
    }

    @GetMapping("{pid}/children")
    @ResponseBody
    public List<TreatiseCategory> children(@PathVariable("pid") Long pid) {
        return treatiseCategoryService.findChildren(pid);
    }

    /**
     * 分页查询
     *
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/page")
    @ResponseBody
    public Response toPage(BasicSearchReq basicSearchReq, TreatiseSearchReq treatiseSearchReq) {
        log.info("【分页查询】 {}", treatiseSearchReq);
        Page<TreatiseDTO> page = treatiseService.findAll(treatiseSearchReq, basicSearchReq);
        return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
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
        mv.addObject("thisCategory", category);
        mv.addObject("categories", treatiseCategoryService.findParent());
        mv.addObject("childCategories", treatiseCategoryService.findChildren(category.getPid()));
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatiseId);
        mv.addObject("chapters", chapters);
        mv.addObject("lastSeq", chapters.get(chapters.size() - 1).getSeq());
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
        mv.addObject("thisCategory", category);
        mv.addObject("categories", treatiseCategoryService.findParent());
        mv.addObject("childCategories", treatiseCategoryService.findChildren(category.getPid()));
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatiseId);
        mv.addObject("chapters", chapters);
        return mv;
    }

    @ResponseBody
    @RequestMapping("/{treatiseId}/jsonInfo")
    public Treatise getTreatiseById(@PathVariable("treatiseId") Long treatiseId) {
        return treatiseService.findById(treatiseId);
    }

    /**
     * 删除
     * @param treatiseId ID
     * @return JSON
     * @see com.cafa.pdf.core.web.aop.ControllerAspect
     */
    @DeleteMapping("/{treatiseId}/delete")
    @ResponseBody
    @RequiresPermissions("treatise:delete")
    public Response delete(@PathVariable("treatiseId") Long treatiseId) {
        treatiseService.delete(treatiseId);
        return buildResponse(Boolean.TRUE, "删除成功", null);
    }

    /**
     * 更改著作信息
     * @param treatiseUpdateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    @RequiresPermissions("treatise:update")
    @ResponseBody
    public Response update(TreatiseUpdateReq treatiseUpdateReq) {
        log.info("【更改信息】 {}", treatiseUpdateReq);
        TreatiseDTO treatise = treatiseService.update(treatiseUpdateReq);
        return buildResponse(Boolean.TRUE, "保存成功", treatise);
    }

    /**
     * 更改著作核心信息
     * @param treatiseUpdateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/updateCore")
    @RequiresPermissions("treatise:update")
    @ResponseBody
    public Response updateCore(TreatiseUpdateReq treatiseUpdateReq) {
        log.info("【更改信息】 {}", treatiseUpdateReq);
        TreatiseDTO treatise = treatiseService.updateCore(treatiseUpdateReq);
        return buildResponse(Boolean.TRUE, "保存成功", treatise);
    }

    /**
     * 保存核心信息
     * @param treatiseSaveReq 保存的信息
     * @return ModelAndView
     * @see com.cafa.pdf.core.web.aop.ControllerAspect
     */
    @PostMapping("/saveCore")
    @RequiresPermissions("treatise:add")
    @ResponseBody
    public Response saveCore(TreatiseSaveCoreReq treatiseSaveReq) {
        log.info("【保存核心信息】 {}", treatiseSaveReq);
        TreatiseDTO treatise = treatiseService.saveCore(treatiseSaveReq);
        // 专著收录量加一
        sysConfigService.addTreatiseAmount();
        return buildResponse(Boolean.TRUE, "保存成功", treatise);
    }

    /**
     * 保存章节信息
     * @param treatiseId 保存的著作id
     * @return ModelAndView
     * @see com.cafa.pdf.core.web.aop.ControllerAspect
     */
    @PostMapping("/{id}/saveChapter")
    @RequiresPermissions("treatise:add")
    @ResponseBody
    public Response saveChapter(@PathVariable("id") Long treatiseId) {
        log.info("【保存章节信息的著作】 {}", treatiseId);
        Treatise treatise = treatiseService.saveChapter(treatiseId);
        return buildResponse(Boolean.TRUE, "保存成功", treatise);
    }

    @Value("${file.path}")
    private String FILE_PATH = "/usr/local/tomcat8kjgl/kjgl/pdf_file/";

    @GetMapping("page/{treatiseId}/{pageNumber}")
    public ResponseEntity<byte[]> page(@PathVariable("treatiseId") Long treatiseId, @PathVariable("pageNumber") int pageNumber) throws IOException {
        List<Chapter> chapters = chapterService.getByTreatiseId(treatiseId);
        int seq = 1;
        for (Chapter i : chapters) {
            if (pageNumber > i.getPage()) {
                pageNumber -= i.getPage();
                seq++;
            } else break;
        }
        File file = new File(FILE_PATH + treatiseId + "/" + seq + "/" + pageNumber + ".pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "x.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(
                FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }

    /**
     * 返回修改著作信息
     */
    @GetMapping("/{treatiseId}")
    @ResponseBody
    public Response info(@PathVariable("treatiseId") Long treatiseId) {
        TreatiseDTO treatise = treatiseService.findOne(treatiseId);
        if (treatise == null) {// 404
            return buildResponse(Boolean.FALSE, "此资源找不到", null);
        }
        List<ChapterDTO> chapters = chapterService.findByTreatiseId(treatise.getId());

        TreatiseAndChaptersDTO treatiseAndChaptersDTO = new TreatiseAndChaptersDTO();
        treatiseAndChaptersDTO.setTreatise(treatise);
        treatiseAndChaptersDTO.setChapters(chapters);

        return buildResponse(Boolean.TRUE, "", treatiseAndChaptersDTO);
    }


    /*
            统计量信息，点击量、阅读量
     */

    /**
     * 返回点击量的页面
     */
    @GetMapping("/hits/list")
    @RequiresPermissions("treatise:show")
    public ModelAndView hitsList() {
        ModelAndView mv = new ModelAndView("admin/statistics/hits");
        return mv;
    }

    /**
     * 返回阅读量的页面
     */
    @GetMapping("/reading/list")
    @RequiresPermissions("treatise:show")
    public ModelAndView readingList() {
        ModelAndView mv = new ModelAndView("admin/statistics/reading");
        return mv;
    }

    /**
     * 点击量分页查询
     *
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/hits/page")
    @ResponseBody
    public Response hitsPage(BasicSearchReq basicSearchReq, HitsSearchReq hitsSearchReq) {
        log.info("【点击量分页查询】 {}", hitsSearchReq);
        Page<HitsDTO> page = treatiseService.findHitsPage(hitsSearchReq, basicSearchReq);
        return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
    }

    /**
     * 阅读量分页查询
     *
     * @param basicSearchReq 基本搜索条件
     * @return JSON
     * @date 2016年8月30日 下午5:30:18
     */
    @GetMapping("/reading/page")
    @ResponseBody
    public Response readingPage(BasicSearchReq basicSearchReq, ReadingSearchReq readingSearchReq) {
        log.info("【阅读量分页查询】 {}", readingSearchReq);
        Page<ReadingDTO> page = treatiseService.findReadingPage(readingSearchReq, basicSearchReq);
        return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
    }


}
