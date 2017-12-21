package com.lzhsite.webkits.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.lzhsite.core.context.XContext;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.limiter.RedisLimiter;
import com.lzhsite.core.utils.IPUtils;

/**
 * 接口限流
 */
public class ApiGatewayInterceptor extends XInterceptor {

    private final static String ACCESS_TOKEN_HEADER_KEY = "x-access-token";

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiGatewayInterceptor.class);

    @Autowired
    private RedisLimiter redisLimiter;

    @Override
    protected boolean internalPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uniqueKey = null;//唯一key
        XContext xContext = XContext.getCurrentContext();
        String token = xContext.getHeader(ACCESS_TOKEN_HEADER_KEY);
        String ip = IPUtils.getRequestRealIp(request);
        if(StringUtils.isEmpty(token)){
            if(null == ip){
                return true;
            }
            uniqueKey = String.valueOf(ip);
        }else{
            uniqueKey = token;
        }
        boolean bol = true;
        try {
            bol = redisLimiter.execute(request.getRequestURI(),uniqueKey);
        }catch (Exception ex){
            LOGGER.error("限流报错,直接跳过,error:{}",ex.getMessage(),ex);
        }
        if(!bol){
            LOGGER.error("限流 URI:{} 超出最大限制,token:{} ip:{}",request.getRequestURI(),token,ip);
            throw XExceptionFactory.create("WEMAILL_0013");
        }
        return bol;
    }


}
