package com.lzhsite.webkits.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.StringUtils;

/**
 * Created by nt on 2016-10-10.
 * <p/>
 * CSRF全局过滤拦截器
 */
public class CSRFDefenseInterceptor extends XInterceptor {

    private String allowedDomains;

    private Boolean closeCSRF = Boolean.FALSE;

    private List<String> allowedDomainList = new ArrayList<>();

    private List<String> allowedPath = new ArrayList<>();

    @Override
    protected boolean internalPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (closeCSRF) {
            return true;
        }
        String path = request.getServletPath();
        if (allowedPath.contains(path)) {
            return true;
        }
        String referer = request.getHeader(CSRFConstants.REFERER);
        Ensure.that(StringUtils.isEmpty(referer) || isAllowDomain(referer)).isTrue("F_WEBKITS_COMMON_1009");
        String origin = request.getHeader(CSRFConstants.ORIGIN);
        Ensure.that(StringUtils.isEmpty(origin) || isAllowDomain(origin)).isTrue("F_WEBKITS_COMMON_1009");
        return true;
    }

    /**
     * 判断是否是允许的域名
     *
     * @param path
     * @return
     */
    private boolean isAllowDomain(String path) {
        List<String> list = getAllowedDomainList();
        for (String domain : list) {
            if (extractDomian(path).endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从request中获取到的链接提取host
     *
     * @param referer
     * @return
     */
    private String extractDomian(String referer) {
        String host;
        if (StringUtils.isEmpty(referer)) {
            return null;
        }
        if (referer.toLowerCase().startsWith(CSRFConstants.HTTP)) {
            host = referer.substring(7, referer.length());
        } else if (referer.toLowerCase().startsWith(CSRFConstants.HTTPS)) {
            host = referer.substring(8, referer.length());
        } else {
            host = referer;
        }
        if (host.contains("/")) {
            return host.substring(0, host.indexOf('/'));
        } else {
            return host;
        }
    }

    public void setAllowedDomains(String allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    public void setAllowedPath(List<String> allowedPath) {
        this.allowedPath = allowedPath;
    }

    public void setCloseCSRF(Boolean closeCSRF) {
        this.closeCSRF = closeCSRF;
    }

    private List<String> getAllowedDomainList() {
        if (allowedDomainList.size() > 0) {
            return allowedDomainList;
        }
        Ensure.that(StringUtils.isNotEmpty(allowedDomains)).isTrue("F_WEBKITS_COMMON_1010");
        String[] allowDomain = allowedDomains.split(",");
        for (int i = 0; i < allowDomain.length; i++) {
            allowedDomainList.add(allowDomain[i]);
        }
        return allowedDomainList;
    }

    static class CSRFConstants {

        public static final String REFERER = "referer";

        public static final String ORIGIN = "origin";

        public static final String HTTP = "http://";

        public static final String HTTPS = "https://";

    }

}
