package com.lzhsite.disruptor.quickstart.order;

import com.lmax.disruptor.EventHandler;

public class OrderEventHandler implements EventHandler<OrderEvent>{

	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		Thread.sleep(Integer.MAX_VALUE);
		System.err.println("消费者: " + event.getValue());
	}

}
