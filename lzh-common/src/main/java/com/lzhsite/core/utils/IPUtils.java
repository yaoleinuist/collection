package com.lzhsite.core.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zhenwei on 6/23/16.
 */
public class IPUtils {

    /**
     * 未知ip
     */
    private static final String UNKNOWN = "unknown";
    /**
     * 通过代理转发时（通常为Nginx），设置的真实客户端ip的信息头字段
     */
    private static final String FORWARD = "x-forwarded-for";
    /**
     * 通过代理转发时（通常为Apache），设置的真实客户端ip的信息头字段
     */
    private static final String PROXY = "Proxy-Client-IP";
    /**
     * 通过代理转发时（通常为Apache），设置的真实客户端ip的信息头字段
     */
    private static final String WL_PROXY = "WL-Proxy-Client-IP";
    /**
     * IP分隔符
     */
    private static final String IP_SEPARATOR = ",";

    private static final String rexp = "^[\\d.;]+$";

    private static final Pattern pattern = Pattern.compile(rexp);

    /**
     * 获取请求的真实客户端ip：被多级反向代理或者跳转转发的请求
     *
     * @param servletRequest
     * @return
     */
    public static String getRequestRealIp(ServletRequest servletRequest) {
        String ip = null;
        HttpServletRequest request = null;
        if (servletRequest instanceof HttpServletRequest) {
            request = (HttpServletRequest) servletRequest;
        }
        if (request != null) {
            ip = request.getHeader(FORWARD);
            if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
                ip = request.getHeader(PROXY);
            }
            if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
                ip = request.getHeader(WL_PROXY);
            }
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = servletRequest.getRemoteAddr();
        }
        //代理ip可能有多级，ip也会有多个，第一个不是“unknown”的ip为真实客户端ip
        if (StringUtils.isNotBlank(ip) && StringUtils.split(ip, IP_SEPARATOR).length > 0) {
            for (String s : StringUtils.split(ip, IP_SEPARATOR)) {
                if (!StringUtils.equalsIgnoreCase(UNKNOWN, s)) {
                    ip = s;
                    break;
                }
            }
        }
        return isLegalIp(ip) ? ip : null;
    }

    private static boolean isLegalIp(String ip) {
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
