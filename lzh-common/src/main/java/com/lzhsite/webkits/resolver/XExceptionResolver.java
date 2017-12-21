package com.lzhsite.webkits.resolver;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lzhsite.core.context.XContext;
import com.lzhsite.core.exception.XBusinessException;
import com.lzhsite.core.exception.XEmptyRequestBodyException;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.exception.XRuntimeException;
import com.lzhsite.core.output.Result;
import com.lzhsite.core.utils.RegexUtils;
import com.lzhsite.core.utils.ResponseUtils;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.ensure.Ensure;
import com.lzhsite.enums.SignType;
import com.lzhsite.webkits.security.signature.SignatureConfig;
import com.lzhsite.webkits.security.signature.SignatureService;
import com.lzhsite.webkits.shiro.exception.XShiroException;

/**
 * Created by nt on 2015-07-11.
 */
public class XExceptionResolver extends SimpleMappingExceptionResolverWrapper {

    private final String WEB_ERROR_LOG_PATTERN = ">>>web请求[%s]发生异常:%s";

    private final String JSON_ERROR_LOG_PATTERN = ">>>[HashCode=%s]请求[%s]发生异常:%s";

    private Logger logger = LoggerFactory.getLogger(XExceptionResolver.class);

    private final SerializerFeature[] serializerFeatures = new SerializerFeature[]{
            SerializerFeature.QuoteFieldNames,
    };

    private String defaultPath;
    private Properties exceptionMappings;
    private Charset charset = Charset.forName("UTF-8");

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public void setExceptionMappings(Properties exceptionMappings) {
        this.exceptionMappings = exceptionMappings;
    }

    @Override
    protected ModelAndView handleResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        boolean isJsonResponse = isJsonRequest(request,handler);
        ModelAndView view;
        if (isJsonResponse) {
            view = jsonExceptionHandler(response, ex);
        } else {
            view = generalExceptionHandler(request, response, ex);
        }
        return view;
    }

    /**
     * 处理非ajax异常
     */
    private ModelAndView generalExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String errorLog = String.format(WEB_ERROR_LOG_PATTERN, request.getServletPath(), ex.getMessage());
        logger.error(errorLog, ex);
        Result result = getExceptionResult(response, ex);
        ModelAndView mav = new ModelAndView();
        mav.addObject("result", result);
        String name = ex.getClass().getName();
        String path = exceptionMappings.getProperty(name);
        String wrapperPath = StringUtils.isEmpty(path) ? defaultPath : path;
        mav.setViewName(wrapperPath);
        return mav;
    }

    /**
     * 处理ajax异常
     */
    public ModelAndView jsonExceptionHandler(HttpServletResponse response, Exception e) {
        ServletOutputStream stream = null;
        try {
            XContext context = XContext.getCurrentContext();
            if (!context.isInited()) {
                logger.error("XContext uninitialized ", e);
            } else {
                String errorLog = String.format(JSON_ERROR_LOG_PATTERN, context.getRequestHashCode(), context.getServiceUrl(), e.getMessage());
                logger.error(errorLog, e);
            }

            Result result = getExceptionResult(response, e);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            stream = response.getOutputStream();

            String jsonStr = convertToJsonStr(result);
            context.setResultJsonStr(jsonStr);

            if (needSignWrite(context)) {
                context.getSignatureConfig().getSignatureService().doWriteSign(response);
            }else if(needSignWriteForOldVersion(context)){
                setSignToHeader(response, jsonStr, context);
            }

            stream.write(jsonStr.getBytes(charset));
        } catch (Exception e1) {
            //do nothing
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e1) {
                    logger.error("IO Exception", e);
                }
            }
        }
        return new ModelAndView();
    }

    private boolean needSignWrite(XContext context){
        if(!context.isInited()){
            return false;
        }
        SignatureConfig signatureConfig = context.getSignatureConfig();
        if(signatureConfig == null){
            return false;
        }
        if(signatureConfig.getSkipWriteSignature()){
            return false;
        }
        SignatureService signatureService = signatureConfig.getSignatureService();
        Ensure.that(signatureService).isNotNull("F_WEBKITS_SECURITY_1010");
        return true;
    }

    private boolean needSignWriteForOldVersion(XContext context){
        return context.isWriteSignToHeader();
    }

    private String convertToJsonStr(Object obj) {
        if (null == obj) {
            return "\"\"";
        } else if (obj instanceof String) {
            String value = (String) obj;
            if (StringUtils.EMPTY.equals(value)) {
                return "\"\"";
            } else {
                return value;
            }
        } else {
            if (serializerFeatures != null) {
                return JSON.toJSONString(obj, serializerFeatures);
            } else {
                return JSON.toJSONString(obj);
            }
        }
    }

    /**
     * 将部分异常处理成BusinessException
     */
    private Result getExceptionResult(HttpServletResponse response, Exception e) {
        Exception ex = e;
        if (e instanceof MethodArgumentNotValidException) {
            ObjectError objectError = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0);
            String validationMsg = objectError.getDefaultMessage();
            ex = handleErrorMessage(validationMsg);
        } else if (e instanceof BindException) {
            ObjectError objectError = ((BindException) e).getBindingResult().getAllErrors().get(0);
            String validationMsg = objectError.getDefaultMessage();
            ex = handleErrorMessage(validationMsg);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) e;
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            ex = XExceptionFactory.create("F_WEBKITS_COMMON_1004", exception.getMethod());
        } else if (e instanceof XEmptyRequestBodyException) {
            ex = XExceptionFactory.create("F_WEBKITS_COMMON_1001");
        } else if (e instanceof RpcException && !(e instanceof XRuntimeException)) {
            Throwable causeEx = e.getCause();
            if (causeEx instanceof ConstraintViolationException) {
                ConstraintViolationException ve = (ConstraintViolationException) e.getCause();
                Set<ConstraintViolation<?>> violations = ve.getConstraintViolations();
                String validationMsg = "";
                for (ConstraintViolation c : violations) {
                    validationMsg = c.getMessage();
                }
                ex = handleErrorMessage(validationMsg);
            }
        } else if (e instanceof JSONException) {
            ex = XExceptionFactory.create("F_WEBKITS_COMMON_1002", e.getMessage().replace("\"", ""));
        } else if (e instanceof MissingServletRequestParameterException) {
            ex = XExceptionFactory.create("F_WEBKITS_COMMON_1003", e.getMessage().replace("\"", ""));
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            ex = XExceptionFactory.create("F_WEBKITS_COMMON_1002", e.getMessage().replace("\"", ""));
        } else if (e instanceof XShiroException) {
            ex = handleErrorMessage(e.getMessage());
        }
        return getFaultResponse(ex);
    }

    /**
     * 判断是否标有responseBody注解请求
     */
    private boolean isJsonRequest(HttpServletRequest request,Object handler) {
        if(String.valueOf(request.getHeader("Accept")).indexOf("application/json") > -1 || String.valueOf(request.getHeader("Content-Type")).indexOf("application/json") > -1 ){
            return true;
        }
        if (handler == null) {
            return false;
        }
        HandlerMethod method = (HandlerMethod) handler;
        ResponseBody body = method.getMethodAnnotation(ResponseBody.class);
        return body != null;
    }

    private XBusinessException handleErrorMessage(String validationMsg) {
        if (RegexUtils.isErrorCode(validationMsg)) {
            return XExceptionFactory.create(validationMsg);
        }

        String code = "F_WEBKITS_COMMON_1003";
        String errMsg = validationMsg;
        return XExceptionFactory.create(code, errMsg);
    }

    private Result getFaultResponse(Exception e) {
        if (e instanceof XBusinessException) {
            return ResponseUtils.getXBusinessResult((XBusinessException) e);
        } else if (e instanceof ValidationException) {
            if (e.getCause() instanceof XBusinessException) {
                return ResponseUtils.getXBusinessResult(((XBusinessException) e.getCause()));
            } else {
                return ResponseUtils.getFaultResult();
            }
        } else if (e instanceof Exception) {
            return ResponseUtils.getFaultResult();
        }
        return ResponseUtils.getUnknownResult();
    }

    /**
     * 设置签名相关的http头
     */
    private void setSignToHeader(HttpServletResponse response, String jsonStr, XContext context) {
        if (!context.isSkipCheckSignature()) {
            try {
                String sign = getResponseSign(jsonStr, context);
                response.setHeader("sign", sign);
                response.setHeader("signType", context.getSignType().getValue());
            } catch (Exception e) {
                logger.error("sign exception");
            }
        }
    }

    /**
     * response返回的签名
     */
    private String getResponseSign(String jsonStr, XContext context) {
        if (jsonStr == null) {
            throw XExceptionFactory.create("F_WEBKITS_RETURN_1001");
        }

        SignType type = context.getSignType();
        String appId = context.getParameterMap().get("appId");
/*        SecretService secretService = ApplicationContextHelper.getContext()
                .getBean(SecretService.class);
        Secret secret = secretService.getSecret(appId, type);
        jsonStr = "data=" + jsonStr;

        return SignUtils.sign(jsonStr, type, secret.getXkeshiPrivateKey());*/
        
        return null;
    }

}
