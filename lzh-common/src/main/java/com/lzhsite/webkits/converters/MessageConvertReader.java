package com.lzhsite.webkits.converters;

import org.springframework.http.HttpInputMessage;

public interface MessageConvertReader {

    /**
     * XmessageConvert的readInternal方法回调
     *
     * @param obj          将原始的body内容转为的对象
     * @param inputMessage http信息体
     */
    <T> T afterRead(T obj, HttpInputMessage inputMessage);
}
