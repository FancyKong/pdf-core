package com.cafa.pdf.core.commom.interceptor;

import com.cafa.pdf.core.service.SysConfigService;
import com.cafa.pdf.core.util.IPv4Util;
import com.cafa.pdf.core.util.MStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 首页访问量拦截器
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 12:37
 */
@Slf4j
public class VisitInterceptor implements HandlerInterceptor {

    @Autowired
    private SysConfigService sysConfigService;
    private static Object NULL = new Object();
    public static ConcurrentHashMap<Integer, Object> ipMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        // TODO 测试
        log.info("【访问量拦截器】 requestUri: {}", requestUri);
        String ipStr = MStringUtils.getIpAddress(request);
        int ipInt = IPv4Util.ipToInt(ipStr);
        log.info("【访问量拦截器】 ipStr: {}", ipStr);
        log.info("【访问量拦截器】 ipInt: {}", ipInt);

        Long visit;
        Object o = ipMap.putIfAbsent(ipInt, NULL);
        if (o == null) {
            visit = sysConfigService.addVisit();
        }else {
            log.info("【访问量拦截器】 该ip已经被计算在内，不再加一");
            visit = sysConfigService.findVisit();
        }
        log.info("【访问量拦截器】 访问量: {}", visit);
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