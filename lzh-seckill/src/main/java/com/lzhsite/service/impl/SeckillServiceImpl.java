package com.lzhsite.service.impl;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.lzhsite.dao.SeckillDao;
import com.lzhsite.dao.SuccessKilledDao;
import com.lzhsite.dao.cache.RedisDao;
import com.lzhsite.dto.Exposer;
import com.lzhsite.dto.SeckillExecution;
import com.lzhsite.entity.Seckill;
import com.lzhsite.entity.SuccessKilled;
import com.lzhsite.enums.SeckillStatEnum;
import com.lzhsite.exception.RepeatKillException;
import com.lzhsite.exception.SeckillCloseException;
import com.lzhsite.exception.SeckillException;
import com.lzhsite.service.SeckillService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component、@Repository   @Service、@Controller
//看字面含义，很容易却别出其中三个：
//@Controller   控制层，就是我们的action层
//@Service        业务逻辑层，就是我们的service或者manager层
//@Repository  持久层，就是我们常说的DAO层
//而@Component  （字面意思就是组件），它在你确定不了事哪一个层的时候使用。
//其实，这四个注解的效果都是一样的，spring都会把它们当做需要注入的Bean加载在上下文中；
//但是在项目中，却建议你严格按照除Componen的其余三个注解的含义使用在项目中。这对分层结构的web架构很有好处！！

@Service
public class SeckillServiceImpl implements SeckillService {
	// 日志对象
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 加入一个混淆字符串(秒杀接口)的salt，为了避免用户猜出我们的md5值，值任意给，越复杂越好
	private final String salt = "aksehiucka24sf*&%&^^#^%$";

	// 注入Service依赖
	@Autowired
	// @Resource
	private SeckillDao seckillDao;

	@Autowired
	// @Resource
	private SuccessKilledDao successKilledDao;

	 @Autowired
	 private RedisDao redisDao;

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}
	
/**	
 * 暴露接口地址的方法，主要业务流程 
 * 1、判断该秒杀商品是否存在，如果不存在，设置Exposer类中状态(state)为活动未开启，返回暴露接口类(Exposer)，告知用户秒杀商品不存在。 
 * 2、如果秒杀商品存在，判断秒杀活动是否开启，如果活动未开启，设置Exposer类中状态(state)为活动未开启，返回暴露接口类(Exposer)，告知用户秒杀活动开启时间、结束时间。 
 * 3、如果秒杀活动开启，先md5加密后，将加密信息与seckillId封装成暴露接口类(Exposer)，并设置状态(state)为活动开启，返回暴露接口类(Exposer) 
 */
	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点:缓存优化:超时的基础上维护一致性
		// 1.访问redis

		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			// 2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {// 说明查不到这个秒杀产品的记录
				return new Exposer(false, seckillId);
			} else {
				// 3.放入redis
				redisDao.putSeckill(seckill);
			}
		}

		// 若是秒杀未开启
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		// 系统当前时间
		Date nowTime = new Date();
		if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}

		// 秒杀开启，返回秒杀商品的id、用给接口加密的md5
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

/**	
 * 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
 * 
 * 上面我们总结过，行级锁的主要发生地，主要是库存update操作这里，之前的业务逻辑如下图 
 *	由下图可知，无论这个商品是否可以被秒杀，都会执行update语句，获得行级锁，再返回是否update成功的结果，并通过这个update操作结果，来插入购买明细，这是非常错误的。 
 *	我们知道，insert操作也可以判断是否插入成功（返回值0代表失败，1代表成功），但是，insert操作只对单条记录加了行机锁，update可能产生间隙锁
 *	因此，我们可以将update语句和insert语句调换位置（先过滤不符合update操作条件的事务，即挡住一部分重复秒杀），通过insert语句的结果，
 *	来判断是否需要update操作，这样，阻塞时间将会大大减少，增加了系统的性能
 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException,
			RepeatKillException, SeckillCloseException {

		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");// 秒杀数据被重写了
		}
		// 执行秒杀逻辑:减库存+增加购买明细
		Date nowTime = new Date();

		try {

			// 否则更新了库存，秒杀成功,增加明细
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			// 看是否该明细被重复插入，即用户是否重复秒杀
			if (insertCount <= 0) {
				throw new RepeatKillException("seckill repeated");
			} else {

				// 减库存,热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// 没有更新库存记录，说明秒杀结束 rollback
					throw new SeckillCloseException("seckill is closed");
				} else {
					// 秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息 commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 将编译期异常转化为运行期异常
			throw new SeckillException("seckill inner error :" + e.getMessage());
		}

	}
	
	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStatEnum.DATE_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		// 执行储存过程,result被赋值
		seckillDao.killByProcedure(map);
		// 获取result
		int result = MapUtils.getInteger(map, "result", -2);
		if (result == 1) {
			SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
			return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
		} else {
			return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
		}
	}
}
