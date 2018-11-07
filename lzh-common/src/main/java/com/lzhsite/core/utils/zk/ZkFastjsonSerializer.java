package com.lzhsite.core.utils.zk;

import java.io.UnsupportedEncodingException;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lzhsite.core.constant.Constants;
 
public class ZkFastjsonSerializer implements ZkSerializer {
    private static final Logger logger = LoggerFactory.getLogger(ZkSerializer.class);

    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        if(null == data){
            return null;
        }
        try {
            String jsonStr = JSONObject.toJSONString(data);
            if (StringUtils.isBlank(jsonStr)) {
                return null;
            }
            return jsonStr.getBytes(Constants.CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            logger.error("序列化对象失败!data=" + data, e);
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        if (null == bytes || bytes.length==0) {
            return null;
        }
        String jsonStr = null;
        try {
             jsonStr = new String(bytes, "UTF-8");
            if (StringUtils.isBlank(jsonStr)) {
                return null;
            }
            return JSONObject.parse(jsonStr);
        } catch (UnsupportedEncodingException e) {
            logger.error("序列化对象失败!jsonStr=" + jsonStr, e);
        }
        return null;
    }
}
