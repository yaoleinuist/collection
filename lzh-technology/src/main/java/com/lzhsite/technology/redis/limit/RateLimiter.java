package com.lzhsite.technology.redis.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Created by hao.g on 18/5/16.
 */
@Component
public class RateLimiter {
	
    @Autowired
    private RedisClient redisClient;

    @Value("${redis.limit.expire}")
    private int expire;

    @Value("${redis.limit.request.count}")
    private int reqCount;

    @Value("${redis.limit.script.name}")
    private String scriptName;

    public Long limit(String key) {
        return redisClient.eval(key, scriptName, 1, expire, reqCount);
    }
}