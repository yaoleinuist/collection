package com.lzhsite.webkits.converters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.webkits.context.XContext;
import com.lzhsite.webkits.security.signature.SignatureConfig;
import com.lzhsite.webkits.security.signature.SignatureService;

public abstract class BaseHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private MessageConvertReader messageConvertReader;
    private MessageConvertWriter messageConvertWriter;
    private List<String> skipXssDefensePath;
    private Map<String, String> richTextField;
    private Boolean cleanXss = Boolean.valueOf(false);
    private SerializerFeature[] features;
    protected Charset charset;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    protected BaseHttpMessageConverter(MediaType... supportedMediaTypes) {
        this.charset = StandardCharsets.UTF_8;
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    protected boolean supports(Class<?> clazz) {
        return true;
    }

    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String jsonBody = XContext.getCurrentContext().getPostJsonBodyStr();
        if(this.cleanXss.booleanValue() && this.isDefensePath(XContext.getCurrentContext().getServiceUrl())) {
            jsonBody = this.getCleanXssJsonBody(jsonBody);
        }

        Object object = JSON.parseObject(jsonBody, clazz);
        if(this.messageConvertReader != null) {
            object = this.messageConvertReader.afterRead(object, inputMessage);
        }

        return object;
    }

    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(this.messageConvertWriter != null) {
            this.messageConvertWriter.beforeWrite(obj, outputMessage);
        }

        XContext context = XContext.getCurrentContext();
        Object result = this.doWrite(obj);
        String jsonStr = this.convertToJsonStr(result);
        context.setResultJsonStr(jsonStr);
        if(this.needSignWrite(context)) {
            if(outputMessage instanceof ServletServerHttpResponse) {
                HttpServletResponse out = ((ServletServerHttpResponse)outputMessage).getServletResponse();
                context.getSignatureConfig().getSignatureService().doWriteSign(out);
            }
        } else if(this.needSignWriteForOldVersion(context)) {
            this.setSignToHeader(outputMessage, jsonStr, context);
        }

        OutputStream out1 = outputMessage.getBody();
        out1.write(jsonStr.getBytes(this.charset));
    }

    protected void setSignToHeader(HttpOutputMessage outputMessage, String jsonStr, XContext context) {
    }

    protected abstract Object doWrite(Object var1);

    private boolean needSignWrite(XContext context) {
        if(!context.isInited()) {
            return false;
        } else {
            SignatureConfig signatureConfig = context.getSignatureConfig();
            if(signatureConfig == null) {
                return false;
            } else if(signatureConfig.getSkipWriteSignature().booleanValue()) {
                return false;
            } else {
                SignatureService signatureService = signatureConfig.getSignatureService();
                Ensure.that(signatureService).isNotNull("F_WEBKITS_SECURITY_1010");
                return true;
            }
        }
    }

    private boolean needSignWriteForOldVersion(XContext context) {
        return context.isWriteSignToHeader().booleanValue();
    }

    private String convertToJsonStr(Object obj) {
        if(null == obj) {
            return "\"\"";
        } else if(obj instanceof String) {
            String value = (String)obj;
            return "".equals(value)?"\"\"":value;
        } else {
            return this.features != null?JSON.toJSONString(obj, this.features):JSON.toJSONString(obj);
        }
    }

    private boolean isDefensePath(String path) {
        if(this.getSkipXssDefensePath() == null) {
            return true;
        } else {
            Iterator var2 = this.getSkipXssDefensePath().iterator();

            String skipDefensePath;
            do {
                if(!var2.hasNext()) {
                    return true;
                }

                skipDefensePath = (String)var2.next();
            } while(!MATCHER.match(skipDefensePath, path));

            return false;
        }
    }

    private String getCleanXssJsonBody(String jsonBody) {
        return StringUtils.isEmpty(jsonBody)?jsonBody:(this.richTextField == null?jsonBody:(this.richTextField.containsKey(XContext.getCurrentContext().getServiceUrl())?XssHelper.cleanXssForRichTextField(jsonBody, (String)this.richTextField.get(XContext.getCurrentContext().getServiceUrl())):jsonBody));
    }

    public MessageConvertReader getMessageConvertReader() {
        return this.messageConvertReader;
    }

    public void setMessageConvertReader(MessageConvertReader messageConvertReader) {
        this.messageConvertReader = messageConvertReader;
    }

    public MessageConvertWriter getMessageConvertWriter() {
        return this.messageConvertWriter;
    }

    public void setMessageConvertWriter(MessageConvertWriter messageConvertWriter) {
        this.messageConvertWriter = messageConvertWriter;
    }

    public List<String> getSkipXssDefensePath() {
        return this.skipXssDefensePath;
    }

    public void setSkipXssDefensePath(List<String> skipXssDefensePath) {
        this.skipXssDefensePath = skipXssDefensePath;
    }

    public Map<String, String> getRichTextField() {
        return this.richTextField;
    }

    public void setRichTextField(Map<String, String> richTextField) {
        this.richTextField = richTextField;
    }

    public Boolean getCleanXss() {
        return this.cleanXss;
    }

    public void setCleanXss(Boolean cleanXss) {
        this.cleanXss = cleanXss;
    }

    public SerializerFeature[] getFeatures() {
        return this.features;
    }

    public void setFeatures(SerializerFeature[] features) {
        this.features = features;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
