package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.TreatiseCategoryDTO;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.service.TreatiseCategoryService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategorySearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategorySaveReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategoryUpdateReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 著作类别控制器
 * Created by Cherish on 2017/1/6.
 */
@Controller
@RequestMapping("treatise_category")
@RequiresAuthentication
public class TreatiseCategoryController extends ABaseController {

    private final TreatiseCategoryService treatiseCategoryService;

    @Autowired
    public TreatiseCategoryController(TreatiseCategoryService treatiseCategoryService) {
        this.treatiseCategoryService = treatiseCategoryService;
    }

    @GetMapping({"","/list"})
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView("admin/treatiseCategory/list");
        return mv;
    }

    /**
     * 返回新增页面
     */
    @GetMapping("/add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/treatiseCategory/add");
        return mv;
    }

    /**
     * 返回修改信息页面
     */
    @GetMapping("/{treatiseCategoryId}/update")
    public ModelAndView updateForm(@PathVariable("treatiseCategoryId") Long treatiseCategoryId){
        ModelAndView mv = new ModelAndView("admin/treatiseCategory/edit");
        TreatiseCategory treatiseCategory = treatiseCategoryService.findById(treatiseCategoryId);
        mv.addObject(treatiseCategory);
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
    public Response toPage(BasicSearchReq basicSearchReq, TreatiseCategorySearchReq treatiseCategorySearchReq){
        try {
            Page<TreatiseCategoryDTO> page = treatiseCategoryService.findAll(basicSearchReq, treatiseCategorySearchReq);
            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            log.error("获取列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 删除
     * @param treatiseCategoryId ID
     * @return JSON
     */
    @DeleteMapping("/{treatiseCategoryId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("treatiseCategoryId") Long treatiseCategoryId){
        try {
            treatiseCategoryService.delete(treatiseCategoryId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改信息
     * @param treatiseCategoryUpdateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    public ModelAndView update(@Validated TreatiseCategoryUpdateReq treatiseCategoryUpdateReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/treatiseCategory/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if(treatiseCategoryUpdateReq == null || treatiseCategoryUpdateReq.getId() == null){
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatiseCategory", treatiseCategoryUpdateReq);
        }else {
            try {
                treatiseCategoryService.update(treatiseCategoryUpdateReq);
                mv.addObject("treatiseCategory", treatiseCategoryService.findById(treatiseCategoryUpdateReq.getId()));
                errorMap.put("msg", "修改成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("修改错误:{}", e.getMessage());
            }
        }
        return mv;
    }

    /**
     * 保存新用户
     * @param treatiseCategorySaveReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    public ModelAndView save(@Validated TreatiseCategorySaveReq treatiseCategorySaveReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/treatiseCategory/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("treatiseCategory", treatiseCategorySaveReq);
        }else {
            try {
                if (treatiseCategoryService.exist(treatiseCategorySaveReq.getName())){
                    errorMap.put("msg", "该类别已存在，请更换再试");
                    mv.addObject("treatiseCategory", treatiseCategorySaveReq);
                }else {
                    treatiseCategoryService.save(treatiseCategorySaveReq);
                    errorMap.put("msg", "添加成功");
                }
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("添加失败:{}", e.getMessage());
            }
        }
        return mv;
    }


}
