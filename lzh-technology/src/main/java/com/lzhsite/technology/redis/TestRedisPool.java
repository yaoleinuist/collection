package com.lzhsite.technology.redis;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;
/**
 * https://www.cnblogs.com/lsx1993/p/4632998.html
 * 
 * 总结:连接池加管道和事务的效率最高
 * @author lzhcode
 *
 */
public class TestRedisPool {

	private static Jedis jedis;
	private static ShardedJedis sharding;//分布式
	private static ShardedJedisPool pool;//链接池

	@BeforeClass
	public static void beforeClass() {
		
		JedisShardInfo jedisShardInfo=new JedisShardInfo("127.0.0.1",6379);
		jedisShardInfo.setPassword("123456..");
		List<JedisShardInfo> shards = Arrays.asList(jedisShardInfo);//仅做演示
		
		jedis = new Jedis(jedisShardInfo);
		sharding = new ShardedJedis(shards);
		
		pool = new ShardedJedisPool(new JedisPoolConfig(), shards);//连接池
	}

	@AfterClass
	public static void afterClass() {
		jedis.disconnect();
		sharding.disconnect();
		
		pool.destroy();
	}

	@Test
	public void normal() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			jedis.set("n" + i, "n" + i);
		}
		long end = System.currentTimeMillis();
		//19秒
		System.out.println("普通操作时间-----：" + (end - start) / 1000 + "秒");
	}

	/**
	 *  事务方式
     *  保障一个client发起的事务中的命令可以连续的执行，而中间不会插入其他client的命令
     *  调用jedis.watch(…)方法来监控key，如果调用后key的值发生变化，则整个事务会执行失败。
     *  另外，事务中某个操作失败，并不会回滚其他操作。这一点需要注意。
     *  还有，我们可以使用discard()方法来取消事务
	 */
	@Test
	public void trans() {
		long start = System.currentTimeMillis();
		// 实现redis乐观锁
		Transaction tx = jedis.multi();
//		String key = "key";
//		tx.watch(key);// 监控，只生效一次
//		tx.incr(key);// 原子性操作
//		List<Object> list = tx.exec();
//		if (list != null) {// 提交事务成功，说明没人更改过
//			
//		}
		
		jedis.multi();
		for (int i = 0; i < 100000; i++) {
			tx.set("t" + i, "t" + i);
		}
		tx.exec();

		long end = System.currentTimeMillis();
		//1.265秒
		System.out.println("事务操作时间-----：" + (end - start) / 1000.0 + "秒");

	}
	
	
	
	
	
	@Test
	public void pipelined () {
		long start = System.currentTimeMillis();
		Pipeline pipeline = jedis.pipelined();
		for (int i = 0; i < 100000; i++) {
			pipeline.set("p" + i, "p" + i);
		}
		
		List<Object> results = pipeline.syncAndReturnAll();
		
		long end = System.currentTimeMillis();
		//3.45秒
		System.out.println("事务操作时间-----：" + (end - start) / 1000.0 + "秒");
	}
	
	@Test
	public void shardNormal(){
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			sharding.set("sn" + i, "sn" + i);
		}
		long end = System.currentTimeMillis();
    	//51.263秒
		System.out.println("分布式同步调用操作时间-----：" + (end - start) / 1000.0 + "秒");
	}
	
	@Test
	public void shardPipelined(){
		long start = System.currentTimeMillis();
		ShardedJedisPipeline pipeline = sharding.pipelined();
		for (int i = 0; i < 100000; i++) {
			pipeline.set("sp" + i, "sp" + i);
		}
		List<Object> results = pipeline.syncAndReturnAll();
		long end = System.currentTimeMillis();
		//1.5秒
		System.out.println("分布式异步调用操作时间-----：" + (end - start) / 1000.0 + "秒");
	}
	
	@Test
	public void shardPipelinedPool () {
		ShardedJedis one = pool.getResource();
		long start = System.currentTimeMillis();
		ShardedJedisPipeline pipeline = one.pipelined();
		for (int i = 0; i < 100000; i++) {
			pipeline.set("sp" + i, "sp" + i);
		}
		List<Object> results = pipeline.syncAndReturnAll();
		
//		try {
//			pool.returnResource(one);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		one.close();
		long end = System.currentTimeMillis();
		//1.298秒
		System.out.println("分布式异步调用操作时间-----：" + (end - start) / 1000.0 + "秒");
		
	}
	
}
