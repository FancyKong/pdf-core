package com.cafa.pdf.core.config;

import com.cafa.pdf.core.commom.interceptor.VisitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.List;
import java.util.Properties;

/**
 * web 相关配置
 * Created by Cherish on 2017/1/6.
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(){
        return new LocalValidatorFactoryBean();
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
//        registry.addViewController("/index").setViewName("/index");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        // 访问量拦截器
        registry.addInterceptor(new VisitInterceptor()).addPathPatterns("/");

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        super.extendHandlerExceptionResolvers(exceptionResolvers);

        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        //为了shiro权限认证中，用户无权限的返回页面
        //明明配置了shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        // 不起效也是奇葩，它就是要抛异常 >> org.apache.shiro.authz.UnauthorizedException
        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "redirect:/403");
        //登陆过期后，跳回登陆界面
        properties.setProperty("org.apache.shiro.authz.AuthorizationException", "redirect:/login");
        simpleMappingExceptionResolver.setExceptionMappings(properties);

        exceptionResolvers.add(simpleMappingExceptionResolver);
    }


}
