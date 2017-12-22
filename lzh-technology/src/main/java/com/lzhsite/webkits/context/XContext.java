//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lzhsite.webkits.context;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.CollectionUtils;
import com.lzhsite.core.utils.MapUtils;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.core.utils.enums.SignType;
import com.lzhsite.webkits.annotations.ResponseWithoutResult;
import com.lzhsite.webkits.security.signature.SignatureConfig;
import com.lzhsite.webkits.session.XSession;

public class XContext {
    private Logger logger = LoggerFactory.getLogger(XContext.class);
    protected static final ThreadLocal<XContext> THREAD_LOCAL = new ThreadLocal() {
        protected XContext initialValue() {
            return new XContext();
        }
    };
    protected HttpServletRequest request;
    private JSONObject postJsonBody;
    private String postJsonBodyStr;
    private Map<String, String> requestParamMap;
    private SignatureConfig signatureConfig;
    private boolean skipCheckSignature = false;
    private long requestStartTime;
    private boolean isInited = false;
    private String source;
    private String realIp;
    private Object handler;
    private String resultJsonStr;
    private XSession xSession = new XSession();

    protected XContext() {
    }

    public static XContext getCurrentContext() {
        return (XContext)THREAD_LOCAL.get();
    }

    public XSession getSession() {
        return this.xSession;
    }

    public void setSession(XSession xSession) {
        if(xSession != null) {
            this.xSession = xSession;
        }

    }

    public String getServletPath() {
        return this.request.getServletPath();
    }

    public void init(HttpServletRequest request, Object handler) {
        this.request = request;
        this.setRequestStartTime(System.currentTimeMillis());
        this.isInited = true;
        this.handler = handler;
        this.realIp = request.getHeader("x-real-ip");
        if(this.isPostRequest()) {
            this.postJsonBody = this.parse(request);
        }

    }

    public String getServiceUrl() {
        String queryString = this.request.getQueryString();
        return StringUtils.isNotEmpty(queryString)?this.request.getRequestURI() + "?" + queryString:this.request.getRequestURI();
    }

    public int getRequestHashCode() {
        return this.request.hashCode();
    }

    public String getParamsString() {
        Map paramMap = this.getParameterMap();
        if(CollectionUtils.isEmpty(paramMap)) {
            return "{}";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("hashcode=").append(this.getRequestHashCode());
            Iterator var3 = paramMap.entrySet().iterator();

            while(var3.hasNext()) {
                Entry entry = (Entry)var3.next();
                String key = (String)entry.getKey();
                if(!key.startsWith("upYun")) {
                    if(key.contains("password")) {
                        stringBuilder.append(',').append(key).append('=').append("******");
                    } else {
                        stringBuilder.append(',').append(key).append('=').append((String)entry.getValue());
                    }
                }
            }

            return stringBuilder.toString();
        }
    }

    public Map<String, String> getParameterMap() {
        if(this.requestParamMap == null) {
            if(this.isPostRequest()) {
                if(this.postJsonBody == null) {
                    return Collections.emptyMap();
                }

                return MapUtils.convertValueToString(this.postJsonBody);
            }

            this.requestParamMap = MapUtils.convertValuesToString(this.request.getParameterMap());
        }

        return this.requestParamMap;
    }

    public long getRequestStartTime() {
        return this.requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRealIp() {
        return this.realIp;
    }

    public void setRealIp(String realIp) {
        this.realIp = realIp;
    }

    public String getParameter(String key) {
        return (String)this.getParameterMap().get(key);
    }

    public String getPostJsonBodyStr() {
        if(StringUtils.isEmpty(this.postJsonBodyStr)) {
            this.postJsonBodyStr = "{}";
        }

        return this.postJsonBodyStr;
    }

    public boolean isPostRequest() {
        if(this.request == null) {
            throw XExceptionFactory.create("F_WEBKITS_COMMON_1006", new String[0]);
        } else {
            boolean isPost = this.request.getMethod().equals(HttpMethod.POST.name()) || this.request.getMethod().equals(HttpMethod.PUT.name());
            return isPost && this.isJsonRequest();
        }
    }

    public boolean isJsonRequest() {
        if(this.request == null) {
            throw XExceptionFactory.create("F_WEBKITS_COMMON_1006", new String[0]);
        } else {
            String contentType = this.request.getContentType();
            return contentType.contains("application/json") || contentType.contains("text/json");
        }
    }

    private JSONObject parse(HttpServletRequest request) {
        try {
            this.postJsonBodyStr = IOUtils.toString(request.getInputStream(), "UTF-8");
            if(this.postJsonBodyStr.startsWith("{\"data\":")) {
                this.postJsonBodyStr = (String)JSON.parseObject(this.postJsonBodyStr).get("data");
                return JSON.parseObject(this.postJsonBodyStr, new Feature[]{Feature.SupportArrayToBean});
            } else {
                return JSON.parseObject(this.postJsonBodyStr, new Feature[]{Feature.SupportArrayToBean});
            }
        } catch (Exception var3) {
            throw XExceptionFactory.create("F_WEBKITS_COMMON_1007", new String[]{request.getRequestURI(), this.postJsonBodyStr, var3.getMessage()});
        }
    }

    public SignType getSignType() {
        String signTypeStr = this.request.getHeader("signType");
        Ensure.that(SignType.isSignType(signTypeStr)).isTrue("F_WEBKITS_SIGN_1007");
        return SignType.valueOf(signTypeStr);
    }

    public String getHeader(String name) {
        return this.request.getHeader(name);
    }

    public boolean isInited() {
        return this.isInited;
    }

    public boolean isSkipCheckSignature() {
        return this.skipCheckSignature;
    }

    public void setSkipCheckSignature(boolean skipCheckSignature) {
        this.skipCheckSignature = skipCheckSignature;
    }

    public boolean isResponseWithoutResult() {
        if(this.handler == null) {
            return false;
        } else {
            ResponseWithoutResult responseWithoutResult = (ResponseWithoutResult)((HandlerMethod)this.handler).getMethodAnnotation(ResponseWithoutResult.class);
            return responseWithoutResult != null;
        }
    }

    public Boolean isWriteSignToHeader() {
        return Boolean.valueOf(this.isInited() && this.isApiSource());
    }

    private boolean isApiSource() {
        return StringUtils.isNotEmpty(this.getSource()) && "openApi".endsWith(this.getSource());
    }

    public <T extends Annotation> T getMethodAnnotation(Class<T> clz) {
        return !(this.handler instanceof HandlerMethod)?null:((HandlerMethod)this.handler).getMethodAnnotation(clz);
    }

    public String getResultJsonStr() {
        return this.resultJsonStr;
    }

    public void setResultJsonStr(String resultJsonStr) {
        this.resultJsonStr = resultJsonStr;
    }

    public String getResultJsonStrWithLimitSize(Integer size) {
        if(size.intValue() >= 0 && size.intValue() <= 10000) {
            String jsonStr = this.getResultJsonStr();
            return StringUtils.isEmpty(jsonStr)?"":(this.resultJsonStr.length() <= size.intValue()?jsonStr:jsonStr.substring(0, size.intValue()));
        } else {
            return "";
        }
    }

    public SignatureConfig getSignatureConfig() {
        return this.signatureConfig;
    }

    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
