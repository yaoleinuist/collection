package com.lzhsite.core.constant;

/**
 * redis缓存
 * Created by pangpeijie on 16/11/3.
 */
public class RedisConstant {

    /**
     * redis 超时时间 2小时
     */
    public static final int REDIS_SET_TIME_OUT = 7200;

    /**
     * redis 超时时间延长 24小时
     */
    public static final int REDIS_SET_TIME_OUT_LONG = 86400;


    /**
     * redis 超时时间延长 24小时 * 7
     */
    public static final int REDIS_SET_TIME_OUT_WEEK = 604800;

    /**
     * redis 超时时间延长 24小时 * 30
     */
    public static final int REDIS_SET_TIME_OUT_MOUTH = 2592000;

    /**
     * redis 超时时间延长 90天
     */
    public static final int REDIS_SET_TIME_OUT_THREE_MONTHS = 7776000;
 
  
    /**
     * 限流规则
     */
    public static final String DISTRUBUTED_LIMITER_RULE ="distributedLimit";

    /**
     * 限流key
     */
    public static final String LIMITER_KEY ="redisLimit:%s:%s";

    /**
     * 限流key次数
     */
    public static final String LIMITER_KEY_COUNT ="redisLimitCounts";

    /**
     * 限流黑名单
     */
    public static final String LIMITER_BLACKLIST_KEY ="redisBlackListLimit";
    
    
}