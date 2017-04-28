package com.cafa.pdf.core.web;

import com.cafa.pdf.core.web.response.Response;
import com.cafa.pdf.core.commom.dto.CustomerDTO;
import com.cafa.pdf.core.dal.entity.Customer;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.customer.CustomerReq;
import com.cafa.pdf.core.web.request.customer.CustomerSearchReq;
import com.cafa.pdf.core.service.CustomerService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
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
 * 会员控制器
 * Created by Cherish on 2017/1/6.
 */
@Controller
@RequestMapping("customer")
@RequiresRoles("admin")
public class CustomerController extends ABaseController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequiresRoles("admin")
    @GetMapping("/list")
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView("admin/customer/list");
        return mv;
    }
    /**
     * 返回新增页面
     */
    @RequiresRoles("admin")
    @GetMapping("/add")
    public ModelAndView addForm(){
        ModelAndView mv = new ModelAndView("admin/customer/add");
        return mv;
    }
    /**
     * 返回修改信息页面
     */
    @RequiresRoles("admin")
    @GetMapping("/{customerId}/update")
    public ModelAndView updateForm(@PathVariable("customerId") Long customerId){
        ModelAndView mv = new ModelAndView("admin/customer/edit");
        Customer customer = customerService.findById(customerId);
        mv.addObject(customer);
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
    public Response toPage(BasicSearchReq basicSearchReq, CustomerSearchReq customerSearchReq){
        try {
            Page<CustomerDTO> page = customerService.findAll(basicSearchReq, customerSearchReq);
            return buildResponse(Boolean.TRUE, basicSearchReq.getDraw(), page);
        } catch (Exception e) {
            log.error("获取列表失败: {}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }

    /**
     * 删除
     * @param customerId ID
     * @return JSON
     */
    @RequiresRoles("admin")
    @DeleteMapping("/{customerId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("customerId") Long customerId){
        try {
            customerService.delete(customerId);
            return buildResponse(Boolean.TRUE, "删除成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
    }

    /**
     * 更改信息
     * @param customerReq 更新信息
     * @return ModelAndView
     */
    @RequiresRoles("admin")
    @PostMapping("/update")
    public ModelAndView update(@Validated CustomerReq customerReq, BindingResult bindingResult){

        ModelAndView mv = new ModelAndView("admin/customer/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if(customerReq == null || customerReq.getId() == null){
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("customer", customerReq);
        }else {
            try {
                customerService.update(customerReq);
                mv.addObject("customer", customerService.findById(customerReq.getId()));
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
     * @param customerReq 保存的信息
     * @return ModelAndView
     */
    @RequiresRoles("admin")
    @PostMapping("/save")
    public ModelAndView save(@Validated CustomerReq customerReq, BindingResult bindingResult){
        log.info("start to handle register param = {}",customerReq);
        ModelAndView mv = new ModelAndView("admin/customer/add");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("customer", customerReq);
        }else {
            try {
                customerService.save(customerReq);
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
    @RequiresAuthentication
    @PostMapping("/modifyPassword")
    public ModelAndView modifyPassword() {

        return null;
    }
    /**
     * 会员中心页面
     * @return ModelAndView
     */
    @GetMapping({"","/","/index"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("customer/index");
        return mv;
    }
    /**
     * 注册页面
     * @return ModelAndView
     */
    @GetMapping("/register")
    public ModelAndView register(){
        ModelAndView mv = new ModelAndView("customer/register");
        return mv;
    }

}
