package com.lzhsite.webkits.interceptors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;

import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.CollectionUtils;
import com.lzhsite.webkits.context.XContext;
import com.lzhsite.webkits.security.signature.SignatureConfig;
import com.lzhsite.webkits.security.signature.SignatureService;


/**
 * Created by ruancl@xkeshi.com on 2017/6/16.
 * 新版签名校验
 */
public class SignInterceptor extends XInterceptor {

    private AntPathMatcher matcher = new AntPathMatcher();

    private Map<String, SignatureConfig> signatureConfigMap = new HashMap<>();

    public void setSignatureConfigs(List<SignatureConfig> signatureConfigs) {
        if(CollectionUtils.isEmpty(signatureConfigs)){
            return;
        }
        signatureConfigs.forEach(config -> signatureConfigMap.put(config.getType(), config));
    }

    @Override
    public boolean internalPreHandle(HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse, Object handler) throws Exception {

        String accessOrigin = httpServletRequest.getHeader("x-access-origin");
        Ensure.that(accessOrigin).isNotBlank("F_WEBKITS_SECURITY_1001");

        SignatureConfig signatureConfig = signatureConfigMap.get(accessOrigin);
        Ensure.that(signatureConfig).isNotNull("F_WEBKITS_SECURITY_1002");

        XContext.getCurrentContext().setSignatureConfig(signatureConfig);

        // 配置为跳过验签时,返回true
        if (signatureConfig.getSkipCheckSignature()) {
            XContext.getCurrentContext().setSkipCheckSignature(true);
            return true;
        }
        // 如果是可忽略的path,直接return true
        if(requestCanBeIgnored(signatureConfig.getIgnoreList())){
            return true;
        }
        SignatureService signatureService = signatureConfig.getSignatureService();
        Ensure.that(signatureService).isNotNull("F_WEBKITS_SECURITY_1010");
        return signatureService.doVerifySign(httpServletRequest, httpServletResponse);
    }

    /**
     * 允许跳过的路径，支持通配符（restful路径）
     */
    private boolean requestCanBeIgnored(List<String> ignoreList) {
        if (CollectionUtils.isEmpty(ignoreList)) {
            return false;
        }
        String servletPath = XContext.getCurrentContext().getServletPath();
        return matchUrl(servletPath, ignoreList);
    }

    private boolean matchUrl(String url, Collection<String> patterns) {
        for (String pattern : patterns) {
            if (matcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

}
