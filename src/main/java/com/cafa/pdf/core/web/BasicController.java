package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.commom.shiro.ShiroUserUtil;
import com.cafa.pdf.core.util.ValidateCode;
import com.cafa.pdf.core.web.request.user.UserLoginReq;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class BasicController extends ABaseController {

	/**
     * 首页
     */
    @GetMapping(value = {"/","/index"})
    public String index(){
        return "index";
    }

    /**
     * 管理页面
     */
    @GetMapping(value = "admin")
    public String admin(){
        return "admin/datapanel";
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
	public ModelAndView login(@Validated UserLoginReq loginReq, BindingResult bindingResult, HttpServletRequest request){

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/login");
        Map<String, Object> errorMap = new HashMap<>();
        modelAndView.addObject("errorMap", errorMap);

		String code = (String) request.getSession().getAttribute("validateCode");
		String submitCode = WebUtils.getCleanParam(request, "validateCode");
		//判断验证码
		if (StringUtils.isBlank(submitCode) || !StringUtils.equalsIgnoreCase(code,submitCode.toLowerCase())) {
			log.debug("验证码不正确");
            errorMap.put("validateCodeError", "验证码不正确");
            //添加上表单输入数据返回给页面
            modelAndView.addObject("usernameInput", loginReq.getUsername());
            modelAndView.addObject("passwordInput", loginReq.getPassword());
			return modelAndView;
		}

		//表单验证是否通过
		if (bindingResult.hasErrors()) {
			errorMap.putAll(getErrors(bindingResult));
            //添加上表单输入数据返回给页面
            modelAndView.addObject("usernameInput", loginReq.getUsername());
            modelAndView.addObject("passwordInput", loginReq.getPassword());

		}else {
			//实现登陆
			UsernamePasswordToken token = new UsernamePasswordToken(
					loginReq.getUsername(), CryptographyUtil.cherishSha1(loginReq.getPassword()));
			//token.setRememberMe(true);
			Subject subject = SecurityUtils.getSubject();

			try {
				//subject.login(token);就会调用 ShiroRealm的 doGetAuthenticationInfo方法
				subject.login(token);

				Session session = subject.getSession();
				session.setAttribute("msg", "登陆成功");
				session.setAttribute("username", ShiroUserUtil.getUsername());
				session.setAttribute("nickname", ShiroUserUtil.getNickname());

			} catch (UnknownAccountException uae) {
				log.debug("账户不存在!");
				errorMap.put("username","账户或密码错误，请重新输入");
			} catch (IncorrectCredentialsException ice) {
				errorMap.put("username","账户或密码错误，请重新输入");
				log.debug("密码不正确!");
			} catch (LockedAccountException lae) {
				log.debug("账户被冻结!");
				errorMap.put("username","该账户被冻结");
			}catch(ExcessiveAttemptsException eae){
				log.debug("错误次数过多");
				errorMap.put("username","密码错误次数过多，请稍后再试");
			} catch (AuthenticationException ae) {
				token.clear();
				errorMap.put("username","系统认证错误");
				log.debug("认证错误!");
			}

			if (subject.isAuthenticated()){
				log.debug("登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
				modelAndView.setViewName("redirect:/admin");
			}
		}

		return modelAndView;
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
	public String upload(@RequestParam("wangEditorH5File") MultipartFile multipartFile, HttpServletRequest request){
		String url = "";
		if (!multipartFile.isEmpty()) {
			File directory = new File(FILE_PATH);

			if (!directory.exists()) {
				directory.mkdirs();
			}

			try {
				String originalFilename = multipartFile.getOriginalFilename();

				String newFIleName = System.currentTimeMillis()//UUID.randomUUID().toString()
						+ originalFilename.substring(originalFilename.lastIndexOf("."));
				multipartFile.transferTo(new File(directory, newFIleName));
//				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),
//				new File(directory,newFIleName));
				String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ request.getContextPath() + "/";
				url = basePath + "imageDownload?filename=" + newFIleName;
			} catch (IOException e) {
				log.error("上传错误 {}", e.getMessage());
			}

		} // end if
		return url;
	}

	@GetMapping("/imageDownload")
	public ResponseEntity<byte[]> downloadImage(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
		File file = new File(FILE_PATH, filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", filename);
		headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(file),
				headers, HttpStatus.OK);
	}
	
}
