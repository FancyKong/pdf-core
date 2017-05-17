package com.cafa.pdf.core.commom.interceptor;

import com.cafa.pdf.core.util.IPv4Util;
import com.cafa.pdf.core.util.MStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页访问量拦截器
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 12:37
 */
@Slf4j
public class VisitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        // TODO
        log.info("【访问量拦截器】 requestUri: {}", requestUri);

        String ipStr = MStringUtils.getIpAddress(request);
        int ipInt = IPv4Util.ipToInt(ipStr);
        log.info("【访问量拦截器】 ipStr: {}", ipStr);
        log.info("【访问量拦截器】 ipInt: {}", ipInt);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // TODO
        log.info("【访问量拦截器】postHandle ");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO
        log.info("【访问量拦截器】afterCompletion ");
    }


}