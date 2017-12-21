package com.lzhsite.core.limiter;

import java.io.Serializable;

/**
 * Limiter限流的JavaBean对象
 * 表示：在${time} 秒内允许通过${count}次访问
 */
public class LimiterBean implements Serializable {

	private static final long serialVersionUID = -6416030369903848365L;

	private String router;
	
	private Integer time;
	
	private int	count;
	
	/**
	 * 限流的路由地址
	 */
	public String getRouter() {
		return router;
	}
	
	public void setRouter(String router) {
		this.router = router;
	}
	
	/**
	 * 限流时间，单位为 秒(s)
	 */
	public Integer getTime() {
		return time;
	}
	
	public void setTime(Integer time) {
		this.time = time;
	}
	
	/**
	 * 限流数量
	 */
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "LimiterBean{" +
				"router='" + router + '\'' +
				", time=" + time +
				", count=" + count +
				'}';
	}
}
