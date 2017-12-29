package com.lzhsite.webkits.interceptors.form;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.utils.RandomStringUtils;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.core.utils.redis.RedisUtils;
import com.lzhsite.webkits.context.XContext;

/**
 * 令牌生成及验证器
 * Created by ylc on 2015/7/8.
 */
public class TokenGenerator {

    /**
     * 保存token值的默认命名空间
     */
    public static final String TOKEN_NAMESPACE = "xkeshi.tokens";

    /**
     * 持有token名称的字段名
     */
    public static final String TOKEN_KEY_FIELD = "tokenKey";
    public static final String TOKEN_VALUE_FIELD = "token";

    /**
     * 日志输出
     */
    private static final Logger logger = LoggerFactory.getLogger(TokenGenerator.class);

    /**
     * 生成重复提交token
     *
     * @param request
     * @return
     */
    public static DuplicateSubmissionToken generate(HttpServletRequest request) {
        return generate(request, generateGUID());
    }

    private static DuplicateSubmissionToken generate(HttpServletRequest request, String key) {
        DuplicateSubmissionToken token = new DuplicateSubmissionToken();
        token.setKey(key);
        token.setValue(generateGUID());
        setRedisCacheToken(request, token);

        return token;
    }

    /**
     * token写入request,redis中
     *
     * @param request
     * @param token
     */
    private static void setRedisCacheToken(HttpServletRequest request, DuplicateSubmissionToken token) {
        String fullTokenName = buildTokenAttributeName(token.getKey());
        /** token 信息放到redis*/
        RedisUtils.put(fullTokenName, token.getValue(), 60 * 30);

        /** 请求信息写入到request中*/
        request.setAttribute(TOKEN_KEY_FIELD, token.getKey());
        request.setAttribute(TOKEN_VALUE_FIELD, token.getValue());
    }

    /**
     * 构建一个基于token名字的带有命名空间为前缀的token名字
     *
     * @param key
     * @return 带命名空间的token名字
     */
    private static String buildTokenAttributeName(String key) {
        return TOKEN_NAMESPACE + "." + key;
    }

    /**
     * 验证当前请求参数中的token是否合法，如果合法的token出现就会删除它，它不会再次成功合法的token
     *
     * @param request
     * @return
     */
    protected static boolean validToken(HttpServletRequest request) {
        XContext xContext = XContext.getCurrentContext();
        String tokenName = xContext.getParameter(TOKEN_KEY_FIELD);
        String token = xContext.getParameter(TOKEN_VALUE_FIELD);
        if (StringUtils.isBlank(tokenName)) {
            logger.debug("no token name found -> Invalid token name:{}", tokenName);
            return false;
        }
        if (StringUtils.isBlank(token)) {
            logger.debug("no token found for token name {} -> Invalid token ", tokenName);
            return false;
        }
        String tokenCacheName = buildTokenAttributeName(tokenName);
        String cacheToken = RedisUtils.get(tokenCacheName, String.class);

        if (!token.equals(cacheToken)) {
            logger.error("xkeshi.internal.invalid.token Form token {} does not match the session token {}.", token, cacheToken);
            return false;
        }

        /**删除redis中token */
        RedisUtils.remove(tokenCacheName);
        request.removeAttribute(TOKEN_KEY_FIELD);
        request.removeAttribute(TOKEN_VALUE_FIELD);
        return true;
    }


    /**
     * 生成token随机名称
     */

    private static String generateGUID() {
        return RandomStringUtils.getUUID();
    }

}