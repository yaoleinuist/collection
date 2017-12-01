package com.lzhsite.util.redis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class RedisValueSerializer implements RedisSerializer<Object> {
	 private static final byte[] EMPTY_ARRAY = new byte[0];
	    private static SerializerFeature[] features;

	    public RedisValueSerializer() {
	    }

	    public Object deserialize(byte[] bytes) throws SerializationException {
	        if(isEmpty(bytes)) {
	            return null;
	        } else {
	            try {
	                return JSON.parseObject(bytes, Object.class, new Feature[0]);
	            } catch (Exception var3) {
	                throw new SerializationException("Could not read JSON: " + var3.getMessage(), var3);
	            }
	        }
	    }

	    public byte[] serialize(Object object) throws SerializationException {
	        if(object == null) {
	            return EMPTY_ARRAY;
	        } else {
	            try {
	                return JSON.toJSONBytes(object, features);
	            } catch (Exception var3) {
	                throw new SerializationException("Could not write JSON: " + var3.getMessage(), var3);
	            }
	        }
	    }

	    private static boolean isEmpty(byte[] data) {
	        return data == null || data.length == 0;
	    }

	    static {
	        features = new SerializerFeature[]{SerializerFeature.WriteClassName};
	        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	    }
}
