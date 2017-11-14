package com.lzhsite.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.lzhsite.util.SpringContextHolder;

public class RedisUtils {
    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static RedisTemplate template = (RedisTemplate)SpringContextHolder.getApplicationContext().getBean(RedisTemplate.class);

    public RedisUtils() {
    }

    /*  public static void put(String key, Object value) {
        put(key, value, Integer.valueOf(3600));
    }

    public static void put(String key, Object value, Integer seconds) {
        template.execute((connection) -> {
            String json = JSON.toJSONString(value);
            byte[] keyBytes = SafeEncoder.encode(key);
            connection.set(keyBytes, SafeEncoder.encode(json));
            connection.expire(keyBytes, (long)seconds.intValue());
            logger.debug("setObject key={},value={}", key, json);
            return null;
        });
    }

    public static <T> T get(String key, Class<T> clazz) {
        return template.execute((connection) -> {
            byte[] keyBytes = connection.get(SafeEncoder.encode(key));
            if(keyBytes != null && keyBytes.length != 0) {
                String value = SafeEncoder.encode(keyBytes);
                return JSONObject.parseObject(value, clazz);
            } else {
                return null;
            }
        });
    }

    public static Long remove(String key) {
        logger.debug("removeObject:{}", key);
        return (Long)template.execute((connection) -> {
            return connection.del(new byte[][]{SafeEncoder.encode(key)});
        });
    }

    public static Long ttl(String key) {
        return (Long)template.execute((connection) -> {
            return connection.ttl(SafeEncoder.encode(key));
        });
    }

    public static void setExpire(String key, int seconds) {
        logger.debug("set key={} expire time={}s", key, Integer.valueOf(seconds));
        template.execute((connection) -> {
            return connection.expire(SafeEncoder.encode(key), (long)seconds);
        });
    }

    public static void setExpireAt(String key, Date date) {
        int expireTime = DateUtils.getSecondsBetween(new Date(), date);
        setExpire(key, expireTime);
        logger.debug("set key={} expire at datetime={}", key, date.toString());
    }

    public static void hput(String key, String field, Object value) {
        logger.debug("set value to field={},key={}", field, key);
        template.execute((connection) -> {
            return connection.hSet(SafeEncoder.encode(key), SafeEncoder.encode(field), SerializeUtils.serialize(value));
        });
    }

    public static <T> T hget(String key, String field, Class<T> clazz) {
        return template.execute((connection) -> {
            byte[] value = connection.hGet(SafeEncoder.encode(key), SafeEncoder.encode(field));
            logger.debug("hget value from key={},fiedl={}", key, field);
            return value != null && value.length != 0?SerializeUtils.unserialize(value):null;
        });
    }

    public static void hremove(String key, String field) {
        logger.debug("gremove value from key={},field={}", key, field);
        template.execute((connection) -> {
            return connection.hDel(SafeEncoder.encode(key), new byte[][]{SafeEncoder.encode(field)});
        });
    }

    public static <T> Collection<T> hgetAll(String key, Class<T> clazz) {
        return (Collection)template.execute((connection) -> {
            ArrayList values = new ArrayList();
            Map map = connection.hGetAll(SafeEncoder.encode(key));
            Iterator var4 = map.entrySet().iterator();

            while(var4.hasNext()) {
                Entry entry = (Entry)var4.next();
                if(entry.getKey() != null && ((byte[])entry.getKey()).length > 0 && entry.getValue() != null && ((byte[])entry.getValue()).length > 0) {
                    values.add(SerializeUtils.unserialize((byte[])entry.getValue()));
                }
            }

            logger.debug("hget all value from key={}", key);
            return values;
        });
    }

    public static Long rpush(String key, String... value) {
        logger.debug("rpush value with key={},field={}", key, value);
        return (Long)template.execute((connection) -> {
            return connection.rPush(SafeEncoder.encode(key), SafeEncoder.encodeMany(value));
        });
    }

    public static List<String> lrange(String key, int start, int end) {
        return (List)template.execute((connection) -> {
            List list = connection.lRange(SafeEncoder.encode(key), (long)start, (long)end);
            return BuilderFactory.STRING_LIST.build(list);
        });
    }

    public static Long lrem(String key, String value, int count) {
        return (Long)template.execute((connection) -> {
            return connection.lRem(SafeEncoder.encode(key), (long)count, SafeEncoder.encode(value));
        });
    }

    public static Boolean exists(String key) {
        logger.debug("query exist key={}", key);
        return (Boolean)template.execute((connection) -> {
            return connection.exists(SafeEncoder.encode(key));
        });
    }

    public static Long incr(String key) {
        logger.debug("incrBy key={}", key);
        return (Long)template.execute((connection) -> {
            return connection.incr(SafeEncoder.encode(key));
        });
    }

    public static Long incrBy(String key, long num) {
        logger.debug("incrBy key={},num={}", key, Long.valueOf(num));
        return (Long)template.execute((connection) -> {
            return connection.incrBy(SafeEncoder.encode(key), num);
        });
    }

    public static Double incrByFloat(String key, double num) {
        logger.debug("incrByFloat key={},num={}", key, Double.valueOf(num));
        return (Double)template.execute((connection) -> {
            return connection.incrBy(SafeEncoder.encode(key), num);
        });
    }

    public static Long setnx(String key, Object value) {
        return setnx(key, value, Integer.valueOf(3600));
    }

    public static Long setnx(String key, Object value, Integer seconds) {
        return setnx(key, value, seconds, Boolean.valueOf(true));
    }

    public static Long setnx(String key, Object value, Integer seconds, Boolean refreshExpireTime) {
        return (Long)template.execute((connection) -> {
            String json = JSON.toJSONString(value);
            byte[] keyBytes = SafeEncoder.encode(key);
            Boolean ret = connection.setNX(keyBytes, SafeEncoder.encode(json));
            if(refreshExpireTime.booleanValue() || ret.booleanValue()) {
                connection.expire(keyBytes, (long)seconds.intValue());
            }

            logger.debug("setnx key={},value={}", key, json);
            return Long.valueOf(ret.booleanValue()?1L:0L);
        });
    }

    public static Long decr(String key) {
        logger.debug("decr key={}", key);
        return (Long)template.execute((connection) -> {
            return connection.decr(SafeEncoder.encode(key));
        });
    }

    public static Long decrBy(String key, long num) {
        logger.debug("decrBy key={},num={}", key, Long.valueOf(num));
        return (Long)template.execute((connection) -> {
            return connection.decrBy(SafeEncoder.encode(key), num);
        });
    }

    public static byte[] get(byte[] key) {
        return (byte[])((byte[])template.execute((connection) -> {
            return connection.get(key);
        }));
    }

    public static byte[] set(byte[] key, byte[] value, Integer seconds) {
        template.execute((connection) -> {
            connection.set(key, value);
            connection.expire(key, (long)seconds.intValue());
            return value;
        });
        return value;
    }

    public static Long del(byte[] key) {
        return (Long)template.execute((connection) -> {
            return connection.del(new byte[][]{key});
        });
    }

    public static Set<byte[]> keys(String pattern) {
        return (Set)template.execute((connection) -> {
            return connection.keys(SafeEncoder.encode(pattern));
        });
    }

    public static void flushDB() {
        logger.debug("flushDB");
        template.execute((connection) -> {
            connection.flushDb();
            return null;
        });
    }

    public static Long dbSize() {
        return (Long)template.execute(RedisServerCommands::dbSize);
    }

    public static <T> T execute(RedisCallback<T> action) {
        return template.execute(action);
    }*/
}
