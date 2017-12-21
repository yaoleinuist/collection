package com.lzhsite.core.utils;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * Created by xiehejun on 2016/11/16.
 */
public class RequestUtils {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * 应用来源
     */
    public enum ChannelVersion{
        WECHAT("wechat" , "微信应用"),
        ALIPAY("alipay" , "支付宝应用"),
        PC("pc" , "电脑浏览器");

        private String code;
        private String desc;

        ChannelVersion(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 封装请求参数成对象
     * @param request
     * @param needDecode
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request, boolean needDecode) {
        Map<String, String> res = new HashMap<>();
        try {
            Enumeration<?> temp = request.getParameterNames();
            if (null != temp) {
                if (needDecode) {
                    while (temp.hasMoreElements()) {
                        String en = (String) temp.nextElement();
                        String value = request.getParameter(en);
                        if (StringUtils.isNotBlank(en) && StringUtils.isNotBlank(value)) {
                            value = URLDecoder.decode(value, "UTF-8");
                            res.put(en, value);
                        }
                    }
                } else {
                    while (temp.hasMoreElements()) {
                        String en = (String) temp.nextElement();
                        String value = request.getParameter(en);
                        if (StringUtils.isNotBlank(en) && StringUtils.isNotBlank(value)) {
                            res.put(en, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("request:请求参数解析失败", e);
        }
        return res;
    }

    /**
     * 获取应用来源
     * @param request
     * @return
     */
    public  static  ChannelVersion getChannelVersion(HttpServletRequest request){

        String userAgent  =  request.getHeader("user-agent");
        logger.info("用户访问来源，userAgent:{}",userAgent);
        if(StringUtils.isBlank(userAgent) ){
            return ChannelVersion.PC;//默认电脑浏览器
        }
        if(userAgent.contains("MicroMessenger")){
            logger.info("用户访问来源为微信应用");
            return ChannelVersion.WECHAT;
        }
        if(userAgent.contains("AlipayClient")){
            logger.info("用户访问来源为支付宝应用");
            return ChannelVersion.ALIPAY;
        }
       return ChannelVersion.PC;//默认电脑浏览器
    }


    /**
     * 获取用户真实IP地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PROXY_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if(ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ip!=null && ip.length()>15){ //"***.***.***.***".length() = 15
            if(ip.indexOf(",")>0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }

}
