package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.dto.AuthorDTO;
import com.cafa.pdf.core.commom.shiro.ShiroUserUtil;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.service.AuthorService;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorRegisterReq;
import com.cafa.pdf.core.web.request.author.AuthorSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorUpdateReq;
import com.cafa.pdf.core.web.response.Response;
import com.google.common.base.Throwables;
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
public class AuthorController extends ABaseController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * 著作者中心页面
     * @return ModelAndView
     */
    @RequiresRoles("author")
    @GetMapping({"","/","/index"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("author/index");
        // TODO 查询信息，以及个人的著作信息
        Author author = authorService.findByUsername(ShiroUserUtil.getUsername());
        mv.addObject(author);

        return mv;
    }
    /**
     * 著作者注册页面
     * @return ModelAndView
     */
    @GetMapping("/register")
    public ModelAndView register(){
        ModelAndView mv = new ModelAndView("author/register");
        return mv;
    }
    /**
     * 管理员所看的 著作者列表页面
     * @return ModelAndView
     */
    @RequiresRoles("admin")
    @GetMapping("/list")
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView("admin/author/list");
        return mv;
    }

    /**
     * 返回新增页面
     */
    @RequiresRoles("admin")
    @GetMapping("/add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/author/add");
        return mv;
    }

    /**
     * 返回修改信息页面
     */
    @RequiresRoles("admin")
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
    @RequiresRoles("admin")
    @GetMapping("/page")
    @ResponseBody
    public Response toPage(BasicSearchReq basicSearchReq, AuthorSearchReq authorSearchReq){
        try {
            Page<AuthorDTO> page = authorService.findAll(basicSearchReq, authorSearchReq);
            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            log.error("获取列表失败: {}", Throwables.getStackTraceAsString(e));
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 删除
     * @param authorId ID
     * @return JSON
     */
    @RequiresRoles("admin")
    @DeleteMapping("/{authorId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("authorId") Long authorId){
        try {
            authorService.delete(authorId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", Throwables.getStackTraceAsString(e));
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改信息
     * @param updateReq 更新信息
     * @return ModelAndView
     */
    @RequiresRoles("admin")
    @PostMapping("/update")
    public ModelAndView update(@Validated AuthorUpdateReq updateReq, BindingResult bindingResult){
        log.info("【更改信息】 {}", updateReq);

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
            return mv;
        }

        try {
            if (authorService.existEmail(updateReq.getEmail())){
                errorMap.put("msg", "该邮箱已注册");
                mv.addObject("author", updateReq);
                return mv;
            }
            authorService.update(updateReq);
            mv.addObject("author", authorService.findById(updateReq.getId()));
            errorMap.put("msg", "修改成功");
        } catch (Exception e) {
            errorMap.put("msg", "系统繁忙");
            log.error("修改错误:{}", Throwables.getStackTraceAsString(e));
        }
        return mv;
    }

    /**
     * 注册著作者
     * @param authorRegisterReq 参数
     * @param bindingResult 验证
     * @return ModelAndView
     */
    @PostMapping("/register")
    public ModelAndView register(@Validated AuthorRegisterReq authorRegisterReq, BindingResult bindingResult) {
        log.info("【注册著作者】 {}",authorRegisterReq);
        ModelAndView mv = new ModelAndView("author/register");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("author", authorRegisterReq);
            return mv;
        }

        try {
            boolean existEmail = authorService.existEmail(authorRegisterReq.getEmail());
            if (existEmail){
                errorMap.put("msg", "该邮箱已注册");
                mv.addObject("author", authorRegisterReq);
                return mv;
            }
            boolean existUsername = authorService.existUsername(authorRegisterReq.getUsername());
            if (existUsername){
                errorMap.put("msg", "该登录账号已被注册，请更换");
                mv.addObject("author", authorRegisterReq);
                return mv;
            }

            authorService.register(authorRegisterReq);
            errorMap.put("msg", "信息提交成功，请登录您的邮箱激活账号");
        } catch (Exception e) {
            errorMap.put("msg", "系统繁忙");
            log.error("添加失败:{}", Throwables.getStackTraceAsString(e));
            
        }
        return mv;
    }

    @GetMapping("/{checkId}/active")
    public ModelAndView active(@PathVariable Long checkId, @RequestParam String key){
        ModelAndView mv = new ModelAndView("admin/login");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);
        boolean bool = authorService.active(checkId, key);
        if (bool){
            errorMap.put("msg", "激活成功，请登录");
        } else {
            errorMap.put("msg", "激活失败，请联系管理员");
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
