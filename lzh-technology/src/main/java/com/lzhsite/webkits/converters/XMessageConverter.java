package com.lzhsite.webkits.converters;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.lzhsite.core.context.ApplicationContextHelper;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.SignUtils;
import com.lzhsite.core.utils.enums.SignType;
import com.lzhsite.webkits.context.XContext;

/**
 * 待重写
 * 
 * @author lzhcode
 *
 */
public class XMessageConverter extends BaseHttpMessageConverter {
	public XMessageConverter(String charset) {
		super(new MediaType[] { new MediaType("application", "json", Charset.forName(charset)),
				new MediaType("application", "*+json", Charset.forName(charset)) });
		this.setCleanXss(Boolean.valueOf(true));
	}

	public XMessageConverter() {
		super(new MediaType[] { new MediaType("application", "json", StandardCharsets.UTF_8),
				new MediaType("application", "*+json", StandardCharsets.UTF_8) });
		this.setCleanXss(Boolean.valueOf(true));
	}

	protected void setSignToHeader(HttpOutputMessage outputMessage, String jsonStr, XContext context) {
		try {
			if (!context.isSkipCheckSignature()) {
				String e = this.getResponseSign(jsonStr, context);
				outputMessage.getHeaders().add("sign", e);
				outputMessage.getHeaders().add("signType", context.getSignType().getValue());
			}
		} catch (Exception var5) {
			;
		}

	}

	/**
	 * response返回的签名
	 *
	 * @return
	 */
	private String getResponseSign(String jsonStr, XContext context) {
		if (jsonStr == null) {
			throw XExceptionFactory.create("F_WEBKITS_RETURN_1001");
		}

		SignType type = context.getSignType();
		String appId = context.getParameterMap().get("appId");

		jsonStr = "data=" + jsonStr;

		return null;
	}

	@Override
	protected Object doWrite(Object var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}

}
