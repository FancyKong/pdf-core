package com.cafa.pdf.core.util;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/26 14:40
 */
public class MStringUtils {

    private MStringUtils(){}

    /**
     * 随机字符串：UUID
     * @return UUID
     */
    public static String randomStr(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取网站根路径
     * @param request HttpServletRequest
     * @return String：网站根路径
     */
    public static String getBasePath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/";
    }

}
