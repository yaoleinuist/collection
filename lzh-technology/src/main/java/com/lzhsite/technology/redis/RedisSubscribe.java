package com.lzhsite.technology.redis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscribe {
	
	@Test
	public void subscribe_test() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(),"192.168.90.137",6379,5000);
		
		Jedis jedis = pool.getResource();
		
		JedisPubSub jedisPubSub = new JedisPubSub() {

			@Override
			public void onMessage(String channel, String message) {
				System.out.println("收到消息：【"+message+"】+来自频道【"+channel+"】");
			}
			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				System.out.println("channel---:"+channel);
				System.out.println("消息信道-----"+subscribedChannels);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				System.out.println("channel---:"+channel);
				System.out.println("消息信道-----"+subscribedChannels);
			}
			
		};
		
		jedis.subscribe(jedisPubSub, new String[]{"dongnao1","dongnao2"});
		jedis.close();
	}
	
	@Test
	public void publish_test(){
		JedisPool pool = new JedisPool(new JedisPoolConfig(),"192.168.90.137",6379,5000);
		
		Jedis jedis = pool.getResource();
		
		long i = jedis.publish("dongnao1", "动脑学院的同学们，你们好呀!!!");
		System.out.println(i+"个订阅者接收到dongnao1的消息");
		i = jedis.publish("dongnao2", "你好呀!!!");
		System.out.println(i+"个订阅者接收到dongnao2的消息");
		
		pool.close();
	}

}
