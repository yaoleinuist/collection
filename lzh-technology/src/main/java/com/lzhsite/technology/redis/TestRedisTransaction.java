package com.lzhsite.technology.redis;

import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Transaction;
/**
 * redis事务的原子性来防止超卖
 * 1. redis中的每个单独的命令都是原子性的，在期每个命令单独执行的过程中不用考录并发的问题。
 * 2. 对于redis，处于同一事物中的一组命令的执行也是原子性的，同样是这组命令执行过程中不用考虑并发的问题。
 */


public class TestRedisTransaction {


	public static void main(String[] args) {

		JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1", 6379);
		jedisShardInfo.setPassword("123456..");
		Jedis jedis = new Jedis(jedisShardInfo);
		jedis.set("mykey", "1");
		for (int i = 0; i < 1000; i++) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						jedisShardInfo.setPassword("123456..");
						Jedis jedis = new Jedis(jedisShardInfo);
						jedis.watch("mykey");
						// 监视mykey,主要是解决value<10这个地方的并发问题

						String valueStr = jedis.get("mykey");

						Integer value = Integer.parseInt(valueStr);

						if (value < 10) {
							// 有可能几个线程获得的值都是9，进入if判断后，value变为10，如果不监视的话就会超卖。
							Transaction tx = jedis.multi();
							tx.incr("mykey");

							List<Object> result = tx.exec();

							// 事物执行后，不管是执行成功还是失败，watch都会放弃对mykey的监控。如果
							// mykey的值被修改，事物将不会执行，也就是从开启事物到执行事物
							// 中间的代码都不会执行；如果mykey的执行没有被修改，那么事物中所有的代码
							// 会原子性执行。

							System.out.println(result);
							
							if (result != null) {
								System.out.println("商品抢购成功！");

							} else {

								System.out.println("商品抢购失败！");

							}

						} else {

							System.out.println("很抱歉商品已被抢完！");

						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					// TODO Auto-generated method stub

				}
			}).start();

			
		}

	}

}
