package com.lzhsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.lzhsite.disruptor.MessageConsumer;
import com.lzhsite.disruptor.RingBufferWorkerPoolFactory;
import com.lzhsite.server.MessageConsumerImpl4Server;
import com.lzhsite.server.NettyServer;

@SpringBootApplication
public class NettyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyServerApplication.class, args);
		
		MessageConsumer[] conusmers = new MessageConsumer[4];
		for(int i =0; i < conusmers.length; i++) {
			MessageConsumer messageConsumer = new MessageConsumerImpl4Server("code:serverId:" + i);
			conusmers[i] = messageConsumer;
		}
		RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
				1024*1024,
				//new YieldingWaitStrategy(),
				new BlockingWaitStrategy(),
				conusmers);
		
		new NettyServer();
	}
}
