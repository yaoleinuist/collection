package com.lzhsite.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;

/**
 * 对象操作工具类
 *
 * @author Guoqw
 * @since 2015-07-09 18:34
 */
public class SerializeUtils extends org.apache.commons.lang3.ObjectUtils {

    private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    /**
     * 序列化对象
     */
    public static byte[] serialize(Object object) {
        Assert.notNull(object,"object can not be null!");
        if(!(object instanceof Serializable)) {
            throw new IllegalArgumentException("requires a Serializable payload " + "but received an object of type [" + object.getClass().getName() + "]");
        }
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("serialize fail",e);
        }
        return null;
    }

    /**
     * 反序列化对象
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            if (bytes != null && bytes.length > 0){
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            logger.error("unserialize fail",e);
        }
        return null;
    }
}
