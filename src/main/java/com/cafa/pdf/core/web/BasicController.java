package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.commom.shiro.ShiroUserUtil;
import com.cafa.pdf.core.service.SysConfigService;
import com.cafa.pdf.core.util.MStringUtils;
import com.cafa.pdf.core.util.SessionUtil;
import com.cafa.pdf.core.util.ValidateCode;
import com.cafa.pdf.core.web.request.LoginReq;
import com.google.common.base.Throwables;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BasicController extends ABaseController {

    private final SysConfigService sysConfigService;

    @Autowired
    public BasicController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    /**
     * 死机路径
     */
    @GetMapping("/exitIt")
    public String exitIt(@RequestParam(required = false) String code) {
        if ("cafa".equals(code)) {
            System.exit(0);
        }
        return "redirect:/";
    }

    /**
     * 首页
     */
    @GetMapping(value = {"/","/index"})
    public String index(){
        return "redirect:/web/index.html";
    }

    /**
     * 管理页面
     */
    @RequiresRoles("admin")
    @GetMapping(value = "admin")
    public ModelAndView admin(){
        ModelAndView mv = new ModelAndView("admin/introduce");
        mv.addObject("customerAmount", sysConfigService.findCustomerAmount());
        mv.addObject("treatiseAmount", sysConfigService.findTreatiseAmount());
        return mv;
    }

	/**
	 * 登陆页面
	 */
	@GetMapping(value = "/login")
	public String login(){
		return "admin/login";
	}
	
	/**
	 * 执行登陆
	 */
	@PostMapping(value = "/login")
	public ModelAndView login(@Validated LoginReq loginReq, BindingResult bindingResult, HttpServletRequest request){
        log.info("【执行登陆】 {}", loginReq);
        ModelAndView mv = new ModelAndView();
		mv.setViewName("admin/login");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

		String code = (String) request.getSession().getAttribute("validateCode");
		String submitCode = WebUtils.getCleanParam(request, "validateCode");
		//判断验证码
		if (StringUtils.isBlank(submitCode) || !StringUtils.equalsIgnoreCase(code,submitCode.toLowerCase())) {
			log.debug("验证码不正确");
            errorMap.put("validateCodeError", "验证码不正确");
            //添加上表单输入数据返回给页面
            mv.addObject("loginReq", loginReq);
			return mv;
		}
		//表单验证是否通过
		if (bindingResult.hasErrors()) {
			errorMap.putAll(getErrors(bindingResult));
            //添加上表单输入数据返回给页面
			mv.addObject("loginReq", loginReq);
			return mv;
		}
		// 保存到session中，按照类别做登录
        SessionUtil.add("loginType", loginReq.getLoginType());
        // 实现登陆
		UsernamePasswordToken token = new UsernamePasswordToken(
				loginReq.getUsername(), CryptographyUtil.cherishSha1(loginReq.getPassword()));
		// token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();

		try {
			// subject.login(token);就会调用 ShiroRealm的 doGetAuthenticationInfo方法
			subject.login(token);

			Session session = subject.getSession();
			session.setAttribute("msg", "登陆成功");
			session.setAttribute("username", ShiroUserUtil.getUsername());
			session.setAttribute("nickname", ShiroUserUtil.getNickname());

		} catch (UnknownAccountException uae) {
			log.debug("【执行登陆】 账户不存在!");
			errorMap.put("username","账户或密码错误，请重新输入");
		} catch (IncorrectCredentialsException ice) {
			log.debug("【执行登陆】 密码不正确!");
			errorMap.put("username","账户或密码错误，请重新输入");
		} catch (LockedAccountException lae) {
			log.debug("【执行登陆】 账户被冻结!");
			errorMap.put("username","该账户被冻结");
		}catch(ExcessiveAttemptsException eae){
			log.debug("【执行登陆】 错误次数过多");
			errorMap.put("username","密码错误次数过多，请稍后再试");
		} catch (AuthenticationException ae) {
			log.debug("【执行登陆】 认证错误!");
			errorMap.put("username","系统认证错误");
			token.clear();
		}

		if (subject.isAuthenticated()){
			log.debug("【执行登陆】 登录认证通过(这里可以进行一些系统参数初始化操作)");

            String loginType = (String) SessionUtil.get("loginType");
            if ("AUTHOR".equals(loginType)){
                mv.setViewName("redirect:/author");
            }else if ("ADMIN".equals(loginType)){
                mv.setViewName("redirect:/admin");
            }else {
                mv.setViewName("redirect:/login");
            }

		}

		return mv;
	}

	@GetMapping("/403")
	public String unauthorizedRole(){
		log.debug("------没有权限-------");
		return "error/403";
	}
	
	/**
	 * 生成验证码
	 * @param request 请求
	 * @param response 响应
	 * @throws IOException 图片生成错误
	 */
	@GetMapping(value = "/validateCode")
	public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Cache-Control", "no-cache");
		String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 4, null);
		request.getSession().setAttribute("validateCode", verifyCode);
		response.setContentType("image/jpeg");
		BufferedImage bim = ValidateCode.generateImageCode(verifyCode, 90, 30, 3, true, Color.WHITE, Color.BLACK, null);
		ImageIO.write(bim, "JPEG", response.getOutputStream());
	}

	//文件存放路径
	private static final String FILE_PATH = "F:/cherish";

    @PostMapping("/imageUpload")
    @ResponseBody
    public String upload(@RequestParam("pdf") MultipartFile multipartFile, String other,
                         HttpServletRequest request) {
        log.info("【文件上传】 other:{}", other);

        String url = "";
        if (!multipartFile.isEmpty()) {
            File directory = new File(FILE_PATH);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH) ;

            if (!directory.exists()) {
                directory.mkdirs();
            }

            try {
                String originalFilename = multipartFile.getOriginalFilename();

                String newFIleName =
                        originalFilename.substring(0, originalFilename.lastIndexOf("."))
                        + year + month + day
                        + originalFilename.substring(originalFilename.lastIndexOf("."));

                multipartFile.transferTo(new File(directory, newFIleName));
//				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),
//				new File(directory,newFIleName));
                String basePath = MStringUtils.getBasePath(request);
                url = basePath + "fileDownload?filename=" + newFIleName;
            } catch (IOException e) {
                log.error("上传错误 {}", Throwables.getStackTraceAsString(e));
            }

        } // end if
        return url;
    }

	@GetMapping("/fileDownload")
	public ResponseEntity<byte[]> downloadImage(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
		File file = new File(FILE_PATH, filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", filename);
		headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<>(
				FileUtils.readFileToByteArray(file),
				headers, HttpStatus.OK);
	}

    /**
     * TODO 暂时这样写，这个更改密码的链接会发送到邮箱，点击链接验证跳转来实现
     * 找回密码
     */
    @GetMapping(value = "/overridePwd")
    public String forgotPwd(){
        return "overridePwd";
    }

	
}
