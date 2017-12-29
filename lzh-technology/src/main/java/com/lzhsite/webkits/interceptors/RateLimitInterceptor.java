package com.lzhsite.webkits.interceptors;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.CollectionUtils;
import com.lzhsite.core.utils.DateUtils;
import com.lzhsite.core.utils.redis.RedisUtils;
import com.lzhsite.webkits.context.XContext;

/**
 * Created by nt on 2016-07-28.
 *
 * 访问限流，之后交给apigateway处理
 */
@Deprecated
public class RateLimitInterceptor extends XInterceptor {

    private Integer requestLimit;

    private List<String> limitPathList;

    @Override
    protected boolean internalPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        XContext context = XContext.getCurrentContext();
        String path = context.getServiceUrl();
        if (isNotLimitPath(path)) {
            return true;
        }
        String appId = context.getParameterMap().get("appId");
        checkRequestLimit(appId);
        return true;
    }

    /**
     * 判断是否是限流的路径
     *
     * @param path
     */
    private boolean isNotLimitPath(String path) {
        if (CollectionUtils.isEmpty(limitPathList)) {
            return true;
        }
        for (String limitPath : limitPathList) {
            PathMatcher matcher = new AntPathMatcher();
            if (matcher.match(limitPath, path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测商户请求是否超过限额
     *
     * @param appId
     */
    private void checkRequestLimit(String appId) {
        Ensure.that(appId).isNotEmpty("F_WEBKITS_SIGN_1006");
        String key = StringUtils.join(appId, "@", DateUtils.format(new Date(), "HH:mm"));
        checkAndIncrement(key);
    }

    /**
     * 根据key获取，如果不存在，会缓存
     *
     * @param key
     * @return
     */
    private void checkAndIncrement(String key) {
        Integer count = RedisUtils.get(key, Integer.class);
        Ensure.that(count == null || count < requestLimit).isTrue("F_WEBKITS_COMMON_1009");
        if (count == null) {
            RedisUtils.put(key, 1, 60);
        } else {
            RedisUtils.incr(key);
        }
    }

    public void setRequestLimit(Integer requestLimit) {
        this.requestLimit = requestLimit;
    }

    public void setLimitPathList(List<String> limitPathList) {
        this.limitPathList = limitPathList;
    }
}
