package com.lzhsite.technology.redis.redislockframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisFactory {

	public static JedisPoolConfig getPoolConfig() throws IOException {
		Properties properties = new Properties();

		InputStream in = RedisFactory.class.getClassLoader().getResourceAsStream("prop/redis.properties");

		try {
			properties.load(in);
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(Integer.parseInt(properties.getProperty("jedis.pool.maxIdle", "3000")));
			config.setMinIdle(Integer.parseInt(properties.getProperty("jedis.pool.minIdle", "1")));
			config.setMaxTotal(Integer.parseInt(properties.getProperty("jedis.pool.maxTotal", "3000")));
			return config;
		} finally {
			in.close();
		}

	}

	public static RedisClient getDefaultClient() {
		JedisPool pool = null;
		try {
			pool = new JedisPool(getPoolConfig(),"127.0.0.1",Protocol.DEFAULT_PORT,100000000,"123456..");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RedisClient client = new RedisClient(pool);
		
		return client;
	}
}
