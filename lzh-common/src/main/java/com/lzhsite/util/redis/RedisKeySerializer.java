package com.lzhsite.util.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class RedisKeySerializer implements RedisSerializer<Object> {
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private final Charset charset;
    private static SerializerFeature[] features;

    public RedisKeySerializer() {
        this.charset = StandardCharsets.UTF_8;
    }

    public Object deserialize(byte[] bytes) {
        throw new SerializationException("Cannot deserialize");
    }

    public byte[] serialize(Object object) {
        if(object == null) {
            return EMPTY_ARRAY;
        } else if(!(object instanceof String) && !(object instanceof Character) && !(object instanceof Number) && !(object instanceof Boolean)) {
            String str = JSON.toJSONString(object, features);
            str = str.replaceAll(":", "-");
            return str.getBytes(this.charset);
        } else {
            return object.toString().getBytes(this.charset);
        }
    }

    static {
        features = new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect};
    }
}
