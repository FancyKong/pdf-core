/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.ChapterDTO;
import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.service.TreatiseService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseAndChapterReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSaveReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseUpdateReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    public TreatiseController(TreatiseService treatiseService,
                              TreatiseCategoryService treatiseCategoryService, ChapterService chapterService) {
        this.treatiseService = treatiseService;
        this.treatiseCategoryService = treatiseCategoryService;
        this.chapterService = chapterService;
    }

    /**
     * 全局返回著作类别
     * @return List<TreatiseCategory>
     */
    @ModelAttribute("categories")
    public List<TreatiseCategory> categories() {
        return treatiseCategoryService.findAll();
    }

    @GetMapping
    @RequiresPermissions("treatise:show")
    public ModelAndView show() {
        ModelAndView mv = new ModelAndView("admin/treatise/list");
        return mv;
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
            //TODO 级联删除，著作章节，solr等

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
     * 更改章节
     * @param treatiseAndChapterReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/updateChapter")
    @RequiresPermissions("treatise:update")
    public ModelAndView updateChapter(@Validated TreatiseAndChapterReq treatiseAndChapterReq, BindingResult bindingResult) {
        log.info("【更改章节】 参数：{}", treatiseAndChapterReq);

        ModelAndView mv = new ModelAndView("admin/treatise/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        Long treatiseId = treatiseAndChapterReq.getTreatiseId();
        if (treatiseId == null) {
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatise", treatiseAndChapterReq);
        } else {
            try {
                List<ChapterReq> chapterReqList = treatiseAndChapterReq.getChapterReqList();

                if (chapterReqList == null || chapterReqList.isEmpty()) {
                    // 删除所有
                    chapterService.deleteByTreatiseId(treatiseId);
                }else {
                    // 根据情况实现增删改
                    chapterService.operate(chapterReqList);
                }

                mv.addObject("treatise", treatiseService.findById(treatiseId));
                mv.addObject("chapters", chapterService.findByTreatiseId(treatiseId));
                errorMap.put("msg", "修改成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("修改错误:{}", e.getMessage());
            }
        }
        return mv;
    }

    //TODO 文件存放路径
    private static final String FILE_PATH = "F:/cherish";
    /**
     * 上传章节的pdf
     * @param multipartFile 文件
     * @param request HttpServletRequest
     * @return url 文件存放路径
     */
    @PostMapping("/uploadPdf")
    @ResponseBody
    public String uploadPdf(@RequestParam("pdf") MultipartFile multipartFile, HttpServletRequest request){
        if (multipartFile.isEmpty()) {
            throw new ServiceException("403", "没有文件数据");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!".pdf".equals(extendName)) {
            throw new ServiceException("403", "非pdf文件");
        }

        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String url = "上传出错";
        try {
            String newFIleName = System.currentTimeMillis() + extendName;
            multipartFile.transferTo(new File(directory, newFIleName));
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath() + "/";
            url = basePath + "fileDownload?filename=" + newFIleName;
        } catch (Exception e) {
            log.error("上传错误 {}", e.getMessage());
        }
        return url;
    }

    /**
     * 保存
     * @param treatiseSaveReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    @RequiresPermissions("treatise:add")
    public ModelAndView save(@Validated TreatiseSaveReq treatiseSaveReq, BindingResult bindingResult) {

        ModelAndView mv = new ModelAndView("admin/treatise/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatise", treatiseSaveReq);
        } else {
            try {
                Treatise treatise = treatiseService.save(treatiseSaveReq);
                // 跳转到更新页面
                mv.setViewName("redirect:admin/treatise/" + treatise.getId() + "/update");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("添加失败:{}", e.getMessage());
            }
        }
        return mv;
    }


}
