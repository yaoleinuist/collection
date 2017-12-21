package com.lzhsite.webkits.converters;

import org.springframework.http.HttpOutputMessage;

/**
 * @author guoqw
 * @since 2017-06-07 09:38
 */
public interface MessageConvertWriter {

    /**
     * XmessageConvert将信息写到response前的回调
     */
    <T> void beforeWrite(T obj, HttpOutputMessage outputMessage);
}
