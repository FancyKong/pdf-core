package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.AuthorDTO;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.service.AuthorService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorSaveReq;
import com.cafa.pdf.core.web.request.author.AuthorSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorUpdateReq;
import com.cafa.pdf.core.web.response.Response;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
 * 著作者控制器
 * Created by Cherish on 2017/1/6.
 */
@Controller
@RequestMapping("author")
@RequiresRoles("admin")
public class AuthorController extends ABaseController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("admin/author/list");
        return mv;
    }

    /**
     * 返回新增页面
     */
    @GetMapping("/add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/author/add");
        return mv;
    }

    /**
     * 返回修改信息页面
     */
    @GetMapping("/{authorId}/update")
    public ModelAndView updateForm(@PathVariable("authorId") Long authorId){
        ModelAndView mv = new ModelAndView("admin/author/edit");
        Author author = authorService.findById(authorId);
        mv.addObject(author);
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
    public Response toPage(BasicSearchReq basicSearchReq, AuthorSearchReq authorSearchReq){
        try {
            Page<AuthorDTO> page = authorService.findAll(basicSearchReq, authorSearchReq);
            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            log.error("获取列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 删除
     * @param authorId ID
     * @return JSON
     */
    @DeleteMapping("/{authorId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("authorId") Long authorId){
        try {
            authorService.delete(authorId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改信息
     * @param updateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    public ModelAndView update(@Validated AuthorUpdateReq updateReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/author/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if(updateReq == null || updateReq.getId() == null){
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("author", updateReq);
        }else {
            try {
                authorService.update(updateReq);
                mv.addObject("author", authorService.findById(updateReq.getId()));
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
     * @param saveReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    public ModelAndView save(@Validated AuthorSaveReq saveReq, BindingResult bindingResult){
        log.info("start to handle save param = {}",saveReq);
        ModelAndView mv = new ModelAndView("admin/author/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("author", saveReq);
        }else {
            try {
                authorService.save(saveReq);
                errorMap.put("msg", "添加成功");
            } catch (Exception e) {
                errorMap.put("msg", "系统繁忙");
                log.error("添加失败:{}", e.getMessage());
            }
        }
        return mv;
    }

    /**
     * 提交密码修改请求
     * @return ModelAndView
     */
    @PostMapping("/modifyPassword")
    public ModelAndView modifyPassword() {

        return null;
    }


}
