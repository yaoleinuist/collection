package com.lzhsite.core.limiter;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.lzhsite.core.constant.RedisConstant;
import com.lzhsite.core.utils.KeyUtil;
import com.lzhsite.util.redis.RedisUtils;

/**
 * 限流接口 - 分布式实现
 */
public class RedisLimiter{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RedisLimiter.class);
	private static Map<String, LimiterBean> limiterBeanMap = new HashMap<String, LimiterBean>();
	//本地缓存刷新时间
	private static LocalDateTime reflushDateTime = null;

	/**
	 * 初始化限流配置
	 */
	public void init() {
		if(null != limiterBeanMap && limiterBeanMap.size() >0){
			limiterBeanMap.clear();
		}
		Collection<LimiterBean> limiterList = RedisUtils.hgetAll(
				RedisConstant.WEMALL_DISTRUBUTED_LIMITER_RULE,LimiterBean.class);
		reflushDateTime = LocalDateTime.now();
		if(null == limiterList || limiterList.size() == 0){
			return;
		}
		for (LimiterBean limiterBean : limiterList) {
			if(null == limiterBean){
				continue;
			}
			limiterBeanMap.put(limiterBean.getRouter(), limiterBean);
			LOGGER.info("分布式限流-加载限流配置>>>router = [{}],time = [{}],count = [{}]",
					limiterBean.getRouter(), limiterBean.getTime(), limiterBean.getCount());
		}

	}

	/**
	 * 执行限流控制，如果通过则返回true，如果不通过则返回false。
	 * @param routerName
	 * @param uniqueKey
	 * @return
	 */
	public boolean execute(String routerName,String uniqueKey) {
		String limitKey = KeyUtil.generteKeyWithPlaceholder(RedisConstant.WEMALL_LIMITER_KEY,
				routerName,String.valueOf(uniqueKey));
		/**
		 * 黑名单校验
		 */
		String value = RedisUtils.hget(RedisConstant.WEMALL_LIMITER_BLACKLIST_KEY,limitKey,String.class);
		if(!StringUtils.isEmpty(value) && "ok".equalsIgnoreCase(value)){
			LOGGER.error("黑名单校验不通过,uniqueKey:{},limitKey:{}",uniqueKey,limitKey);
			return false;
		}

		//本地更新缓存
		LocalDateTime nowDateTime = LocalDateTime.now();
		Duration duration = java.time.Duration.between(reflushDateTime,nowDateTime);
		if(duration.toHours() >= 1){
			//大于一个小时的时候，去更新本地缓存
			this.init();
			LOGGER.info("更新本地缓存限流规则缓存");
		}

		/**
		 * 限流校验
		 */
		LimiterBean limiterBean = limiterBeanMap.get(routerName);
		if (null == limiterBean){
			// 表示没有相关限流配置，直接返回成功
			return true;
		}
		int limiterCount = limiterBean.getCount();

		/**
		 * 每次根据路由地址强制设置缓存和过期时间，防止缓存过期后导致限流失效<br>
		 * 倘若已经存在该路由的缓存KEY，不会设置新值
		 */
		RedisUtils.setnx(limitKey,0,limiterBean.getTime(),true);
		long currentCount = RedisUtils.incr(limitKey);
		if (currentCount > limiterCount) {
			// 如果超过限流值，则直接返回false
			Integer count = RedisUtils.hget(RedisConstant.WEMALL_LIMITER_KEY_COUNT,limitKey,Integer.class);
			if (null == count){
				count = 0;
			}
			if(count >= 50){
				//大于50次的时候，才会触发加入黑名单
				RedisUtils.hput(RedisConstant.WEMALL_LIMITER_BLACKLIST_KEY,limitKey,"ok");
				RedisUtils.setExpire(RedisConstant.WEMALL_LIMITER_BLACKLIST_KEY,RedisConstant.REDIS_SET_TIME_OUT);
				LOGGER.error("触发加入黑名单,uniqueKey:{},limitKey:{}",uniqueKey,limitKey);
				return false;
			}
			count = count + 1;
			RedisUtils.hput(RedisConstant.WEMALL_LIMITER_KEY_COUNT,limitKey,count);
			LOGGER.error("请求限流,uniqueKey:{},limitKey:{},count:{}",uniqueKey,limitKey,count);
			return false;
		}
		return true;
	}
	
}
