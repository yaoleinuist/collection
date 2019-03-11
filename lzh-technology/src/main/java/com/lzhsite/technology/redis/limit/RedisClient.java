package com.lzhsite.technology.redis.limit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.lzhsite.core.utils.FileUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by hao.g on 18/5/16.
 */
@Component
public class RedisClient implements InitializingBean{
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);
    private JedisPool jedisPool;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.pool.max-wait}")
    private int maxWait;

    @Value("classpath:lua/*.lua")
    private Resource[] resources;

    private Map<String, String> scriptMap = new ConcurrentHashMap<>();

    public Long eval(String key, String scriptName, int keyCount, int expire, int reqCount){
        Jedis jedis = jedisPool.getResource();
        try {
            return (Long) jedis.eval(scriptMap.get(scriptName),
                    keyCount,
                    key,
                    String.valueOf(expire),
                    String.valueOf(reqCount));
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }finally {
            jedis.close();
        }
        return 0L;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(maxWait);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        this.jedisPool = new JedisPool(config, host, port);
        for (Resource resource : resources){
            scriptMap.put(resource.getFilename(), FileUtil.loadScript(resource.getInputStream()));
            LOGGER.info("load script file:" + resource.getFilename());
        }
    }
}
