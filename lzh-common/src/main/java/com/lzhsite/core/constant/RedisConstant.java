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
     * 电子券缓存
     */
    public static final String PREFIX = "wemall:";

    /**
     * WEMALL用户token
     */
    public static final String WEMALL_USER_TOKEN = PREFIX + "userToken:%s";

    /**
     * WEMALL券信息
     */
    public static final String COUPON_INFO_WEMALL_PREFIX = PREFIX + "couponInfoWemall:%s";

    /**
     * 渠道信息缓存
     */
    public static final String COUPON_CHANNEL_INFO_PREFIX = PREFIX + "couponChannelId:%d";

    /**
     * 电子券缓存
     */
    public static final String COUPON_INFO_PREFIX = PREFIX + "couponChannelDetailId:%d:applyScene:%d";

    /**
     * 电子券缓存
     */
    public static final String WEMALL_COUPON_INFO_PREFIX = PREFIX + "wemallCouponId:%d";

    /**
     * 电子券详情缓存
     */
    public static final String COUPON_DETAIL_DTO_PREFIX = PREFIX + "couponChannelDetailDTOId:%d";

  
 
    /**
     * 限流规则
     */
    public static final String WEMALL_DISTRUBUTED_LIMITER_RULE = PREFIX + "distributedLimit";

    /**
     * 限流key
     */
    public static final String WEMALL_LIMITER_KEY = PREFIX + "redisLimit:%s:%s";

    /**
     * 限流key次数
     */
    public static final String WEMALL_LIMITER_KEY_COUNT = PREFIX + "redisLimitCounts";

    /**
     * 限流黑名单
     */
    public static final String WEMALL_LIMITER_BLACKLIST_KEY = PREFIX + "redisBlackListLimit";
    
    
}