package com.lzhsite.disruptor.quickstart.inParking;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
/**
 * 测试 P1生产消息，C1，C2消费消息，C1和C2会共享所有的event元素! C3依赖C1，C2处理结果
 * @author lzhcode
 *
 */
public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		long beginTime = System.currentTimeMillis();

		//最好是2的n次方
		int bufferSize = 1024;
		// Disruptor交给线程池来处理，共计 p1,c1,c2,c3四个线程
		ExecutorService executor = Executors.newFixedThreadPool(4);
		// 构造缓冲区与事件生成
		Disruptor<InParkingDataEvent> disruptor = new Disruptor<InParkingDataEvent>(
				new EventFactory<InParkingDataEvent>() {
					@Override
					public InParkingDataEvent newInstance() {
						return new InParkingDataEvent();
					}
				}, bufferSize, executor, ProducerType.SINGLE, new YieldingWaitStrategy());

		// 使用disruptor创建消费者组C1,C2
		EventHandlerGroup<InParkingDataEvent> handlerGroup = disruptor.handleEventsWith(new ParkingDataToKafkaHandler(),
				new ParkingDataInDbHandler());

		ParkingDataSmsHandler smsHandler = new ParkingDataSmsHandler();
		// 声明在C1,C2完事之后执行JMS消息发送操作 也就是流程走到C3
		handlerGroup.then(smsHandler);

		disruptor.start();// 启动
		CountDownLatch latch = new CountDownLatch(1);
		// 生产者准备
		executor.submit(new InParkingDataEventPublisher(latch, disruptor));
		latch.await();// 等待生产者结束
		disruptor.shutdown();
		executor.shutdown();

		System.out.println("总耗时:" + (System.currentTimeMillis() - beginTime));
	}
}
