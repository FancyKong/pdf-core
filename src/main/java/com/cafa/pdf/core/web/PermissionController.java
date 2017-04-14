package com.cafa.pdf.core.web;

import com.cafa.pdf.core.web.response.Response;
import com.cafa.pdf.core.commom.dto.PermissionDTO;
import com.cafa.pdf.core.dal.entity.Permission;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.permission.PermissionSaveReq;
import com.cafa.pdf.core.web.request.permission.PermissionUpdateReq;
import com.cafa.pdf.core.service.PermissionService;
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
 * 权限控制器
 * Created by Cherish on 2017/1/6.
 */
@Controller
@RequestMapping("permission")
@RequiresRoles("super")
public class PermissionController extends ABaseController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("admin/permission/list");
        return mv;
    }

    /**
     * 返回新增页面
     */
    @GetMapping("/add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/permission/add");
        return mv;
    }

    /**
     * 返回修改信息页面
     */
    @GetMapping("/{permissionId}/update")
    public ModelAndView updateForm(@PathVariable("permissionId") Long permissionId){
        ModelAndView mv = new ModelAndView("admin/permission/edit");
        Permission permission = permissionService.findById(permissionId);
        mv.addObject(permission);
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
    public Response toPage(BasicSearchReq basicSearchReq){
        try {
            Page<PermissionDTO> page = permissionService.findAll(basicSearchReq);
            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 删除
     * @param permissionId ID
     * @return JSON
     */
    @DeleteMapping("/{permissionId}/delete")
    @ResponseBody
    public Response del(@PathVariable("permissionId") Long permissionId){
        try {
            permissionService.delete(permissionId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改信息
     * @param permissionUpdateReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    public ModelAndView update(@Validated PermissionUpdateReq permissionUpdateReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/permission/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if(permissionUpdateReq == null || permissionUpdateReq.getId() == null){
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("permission", permissionUpdateReq);
        }else {
            try {
                permissionService.update(permissionUpdateReq);
                mv.addObject("permission", permissionService.findById(permissionUpdateReq.getId()));
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
     * @param permissionSaveReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    public ModelAndView save(@Validated PermissionSaveReq permissionSaveReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/permission/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("permission", permissionSaveReq);
        }else {
            try {
                if (permissionService.exist(permissionSaveReq.getPermit())){
                    errorMap.put("msg", "该角色名已存在，请更换再试");
                    mv.addObject("permission", permissionSaveReq);
                }else {
                    permissionService.save(permissionSaveReq);
                    errorMap.put("msg", "添加成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMap.put("msg", "系统繁忙");
                log.error("添加失败:{}", e.getMessage());
            }
        }
        return mv;
    }


}
