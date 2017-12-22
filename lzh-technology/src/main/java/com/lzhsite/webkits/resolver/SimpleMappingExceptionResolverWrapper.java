package com.lzhsite.webkits.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.lzhsite.webkits.context.XContext;
import com.lzhsite.webkits.converters.ConverterConstant;

/**
 * Created by nt on 2017-06-19.
 */
public abstract class SimpleMappingExceptionResolverWrapper extends SimpleMappingExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(SimpleMappingExceptionResolverWrapper.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            ModelAndView modelAndView = handleResolveException(request, response, handler, ex);
            logError();
            return modelAndView;
        } catch (Exception e) {
            logger.error("doResolveException error", e);
        } finally {
            XContext.clear();
        }
        return new ModelAndView();
    }

    protected abstract ModelAndView handleResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);

    protected void logError() {
        XContext context = XContext.getCurrentContext();
        if (context.isInited()) {
            //记录初始化成功时候的异常
            long spentTime = System.currentTimeMillis() - context.getRequestStartTime();
            logger.error(String.format(ConverterConstant.LOG_PATTERN, spentTime, context.getRealIp(), context.getRequestHashCode(), context.getSource(), context.getServiceUrl(),
                    context.getParamsString(), context.getResultJsonStrWithLimitSize(2000)));
        }

    }

}
