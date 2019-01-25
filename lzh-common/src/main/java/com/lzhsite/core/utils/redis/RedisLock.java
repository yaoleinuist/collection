package com.lzhsite.core.utils.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.StringUtils;

/**
 * 分布式锁 性能待测试
 * 
 * @author lzhcode
 *
 */
public class RedisLock {

	private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);

	/**
	 * 设置锁 默认5s
	 * 
	 * @param lockKey
	 */
	public void lock(String lockKey) {
		boolean result = lock(lockKey, 5, TimeUnit.SECONDS);
		if (!result) {
			throw XExceptionFactory.create("WEMAILL_0011");
		}
	}

	/**
	 * 设置锁
	 * 
	 * @param lockKey
	 * @param time
	 * @param unit
	 * @return
	 */
	public boolean lock(String lockKey, int time, TimeUnit unit) {
		try {
			synchronized (lockKey) {
				long orginalTimeout, timeout;
				orginalTimeout = timeout = caculateTime(time, unit);// 毫秒
				while (timeout >= 0) {
					long expires = System.currentTimeMillis() + timeout + 1;
					if (RedisUtils.setnx(lockKey, expires) == 1l) {
						RedisUtils.setExpire(lockKey, Integer.valueOf("" + timeout / 1000));// 秒
						LOGGER.info("[RedisLock]lockKey:{}设置key", lockKey);
						return true;
					}
					String currentValueStr = RedisUtils.get(lockKey, String.class);
					if (StringUtils.isEmpty(currentValueStr)) {
						continue;
					}
					if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
						LOGGER.info("[RedisLock]key：{} 已过锁过期时间将强制获取锁，并重置锁时间。", lockKey);
						byte[] oldValueStr = RedisUtils.get(lockKey.getBytes());
						if (new String(currentValueStr).equals(new String(oldValueStr))) {
							expires = System.currentTimeMillis() + orginalTimeout + 1;
							RedisUtils.setnx(lockKey, expires);
							if (LOGGER.isInfoEnabled()) {
								LOGGER.info("[RedisLock]lockKey:{}设置key", lockKey);
							}
							return true;
						} else {
							RedisUtils.setnx(lockKey, oldValueStr);
							timeout = orginalTimeout;
							if (LOGGER.isInfoEnabled()) {
								LOGGER.info("[RedisLock]lockKey:{}设置key", lockKey);
							}
						}
					}
					timeout -= 100;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				return true;
			}
		} catch (Exception ex) {
			LOGGER.error("全局锁失败", ex);
		}
		return false;
	}

	/**
	 * 解锁
	 * 
	 * @param lockKey
	 * @return
	 */
	public boolean unlock(String lockKey) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[RedisLock]key：{}已清除锁成功", lockKey);
		}
		RedisUtils.del(lockKey.getBytes());
		return true;
	}

	/**
	 * 获取毫秒
	 * 
	 * @param time
	 * @param unit
	 * @return
	 */
	private int caculateTime(int time, TimeUnit unit) {
		switch (unit) {
		case DAYS:
			return time * 24 * 60 * 60 * 1000;
		case HOURS:
			return time * 60 * 60 * 1000;
		case MINUTES:
			return time * 60 * 1000;
		case SECONDS:
			return time * 1000;
		default:
			return 1;
		}
	}

}
