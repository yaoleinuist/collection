package com.lzhsite.webkits.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lzhsite.core.utils.CollectionUtils;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.webkits.context.XContext;
import com.lzhsite.webkits.converters.ConverterConstant;

/**
 * Created by Zhenwei on 2015/5/20.
 */
public class XInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(XInterceptor.class);

    private List<XInterceptor> xInterceptorList;

    private List<String> ignoreList;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 来源，例如openApi，ecoupon等
     */
    private String source;

    public XInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        if (isIgnored(request.getRequestURI())) {
            return true;
        }
        if (isAccessDenied(request, handler)) {
            return false;
        }

        XContext xContext = XContext.getCurrentContext();

        if (xContext.isInited()) {
            XContext.clear();
            xContext = XContext.getCurrentContext();
        }
        xContext.init(request, handler);

        if (StringUtils.isEmpty(source)) {
            logger.warn("source is empty. hashCode is {}", xContext.getRequestHashCode());
        } else {
            xContext.setSource(source);
        }

        if (CollectionUtils.isEmpty(xInterceptorList)) {
            return true;
        }

        for (XInterceptor interceptor : xInterceptorList) {
            if (!interceptor.internalPreHandle(request, response, handler)) {
                return false;
            }
        }
        return true;
    }

    protected boolean internalPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (CollectionUtils.isNotEmpty(xInterceptorList)) {
            for (XInterceptor interceptor : xInterceptorList) {
                interceptor.internalPostHandler(request, response, handler, modelAndView);
            }
        }
    }

    protected void internalPostHandler(HttpServletRequest request, HttpServletResponse response, Object handler,
                                       ModelAndView modelAndView) throws Exception {

    }

    protected void internalAfterCompletion(HttpServletResponse response, Exception e) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
            throws Exception {
        traceRequestLog();
        XContext.clear();
        // xInterceptor的清理工作
        for (XInterceptor xInterceptor : xInterceptorList) {
            // 避免多个自定义的xInterceptor相互影响
            try {
                xInterceptor.internalAfterCompletion(response, e);
            } catch (Exception e1) {
                logger.error("xInterceptor afterCompletion error", e1);
            }
        }
    }

    /**
     * 记录请求日志
     */
    private void traceRequestLog() {
        XContext context = XContext.getCurrentContext();
        if (context.isInited()) {
            long spentTime = System.currentTimeMillis() - context.getRequestStartTime();
            logger.info(String.format(ConverterConstant.LOG_PATTERN, spentTime, context.getRealIp(),
                    context.getRequestHashCode(), context.getSource(), context.getServiceUrl(),
                    context.getParamsString(), context.getResultJsonStrWithLimitSize(2000)));
        }
    }

    public void setXInterceptorList(List<XInterceptor> xInterceptorList) {
        this.xInterceptorList = xInterceptorList;
    }

    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    /**
     * 是否拒绝请求
     */
    private boolean isAccessDenied(HttpServletRequest request, Object handler) {
        /* 跳过默认servlet解析器 */
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        /* 禁止通过浏览器访问ajax方法 */
        ResponseBody responseBody = ((HandlerMethod) handler).getMethodAnnotation(ResponseBody.class);
        if (responseBody != null && String.valueOf(request.getHeader("Accept")).indexOf("text/html") != -1) {
            if (logger.isInfoEnabled()) {
                logger.info(" http Accept error  Access Denied. url:{}", request.getRequestURL());
            }
            return true;
        }
        return false;
    }

    /**
     * ignoreList 匹配校验
     */
    private boolean isIgnored(String uri) {
        if (ignoreList != null) {
            for (String ignoreUriPattern : ignoreList) {
                if (antPathMatcher.match(ignoreUriPattern, uri)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setSource(String source) {
        this.source = source;
    }

}