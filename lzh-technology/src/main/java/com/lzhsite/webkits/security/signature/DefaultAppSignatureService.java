package com.lzhsite.webkits.security.signature;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.lzhsite.core.context.ApplicationContextHelper;
import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.DateUtils;
import com.lzhsite.core.utils.SignUtils;
import com.lzhsite.core.utils.enums.SignType;
import com.lzhsite.dto.SecretDto;
import com.lzhsite.entity.Secret;
import com.lzhsite.service.SecretService;
import com.lzhsite.webkits.context.XContext;

/**
 * Created by ruancl@xkeshi.com on 2017/6/15.
 */
public class DefaultAppSignatureService extends AbstractSignatureService {

    private boolean skipCheckTime = false;

    private static final Long EXPIRED_MILLSECONDS = 300L * 1000L;

    private Charset charset = StandardCharsets.UTF_8;

    public DefaultAppSignatureService(boolean skipCheckTime) {
        this.skipCheckTime = skipCheckTime;
    }

    public DefaultAppSignatureService() {
    }

    public void setSkipCheckTime(boolean skipCheckTime) {
        this.skipCheckTime = skipCheckTime;
    }

    private void checkUpTimestamp(String requestTimestamp) {
        Ensure.that(requestTimestamp).isNotEmpty("F_WEBKITS_COMMON_1008");

        DateTime requestTimestampDate = new DateTime(Long.valueOf(requestTimestamp));

        Long betweenSeconds = DateUtils.getMillSecondsBetween(new DateTime(), requestTimestampDate);
        if (Math.abs(betweenSeconds) > EXPIRED_MILLSECONDS) {
            throw XExceptionFactory.create("F_WEBKITS_SIGN_1005");
        }
    }

    @Override
    public boolean verifySign(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (!skipCheckTime) {
            checkUpTimestamp(
                    XContext.getCurrentContext().getParameterMap().get("timestamp"));  //判断请求是否超过5分钟
        }
        XContext context = XContext.getCurrentContext();

        String appId = context.getParameterMap().get("appId");
        Ensure.that(appId).isNotEmpty("F_WEBKITS_SIGN_1006");

        SignType type = context.getSignType();
        SecretService secretService = ApplicationContextHelper.getContext()
                .getBean(SecretService.class);
        SecretDto secret = secretService.getSecret(appId, type);
        String path = context.getServiceUrl();

        //String clientSignature = httpServletRequest.getParameter("sign");
        String clientSignature = context.getHeader("sign");

        Map<String, String> signParamMap = new HashMap<>();
        if (context.isPostRequest()) {
            // this url is servlet path
            signParamMap.put("data", context.getPostJsonBodyStr());
            signParamMap.put("path", path);
            if (!SignUtils
                    .validateSignature(clientSignature, signParamMap, type, secret.getClientPublicKey())) {
                throw XExceptionFactory
                        .create("F_WEBKITS_SIGN_1002", clientSignature, signParamMap.toString(),
                                type.getValue());
            }
        } else {
            if (!SignUtils.validateSignature(clientSignature, path, type, secret.getClientPublicKey())) {
                throw XExceptionFactory
                        .create("F_WEBKITS_SIGN_1002", clientSignature, path, type.getValue());
            }
        }

        return true;
    }

    @Override
    public void writeSign(HttpServletResponse httpServletResponse) {
        XContext context = XContext.getCurrentContext();
        httpServletResponse.setHeader("sign", getResponseSign(context));
        httpServletResponse.setHeader("signType", context.getSignType().getValue());
    }

    /**
     * response返回的签名
     *
     * @return
     */
    private String getResponseSign(XContext context) {
        SignType type = context.getSignType();
        String appId = context.getParameterMap().get("appId");
        Ensure.that(type).isNotNull("F_WEBKITS_SIGN_1007");
        Ensure.that(appId).isNotBlank("F_WEBKITS_SIGN_1006");
        SecretService secretService = ApplicationContextHelper.getContext().getBean(SecretService.class);
        SecretDto secretDto = secretService.getSecret(appId, type);
        String jsonStr = "data=" + context.getResultJsonStr();
        return SignUtils.sign(jsonStr, type, secretDto.getXkeshiPrivateKey());
    }

}
