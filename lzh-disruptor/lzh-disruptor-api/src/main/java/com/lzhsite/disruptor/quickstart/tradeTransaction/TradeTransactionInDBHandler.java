package com.lzhsite.disruptor.quickstart.tradeTransaction;

import java.util.UUID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class TradeTransactionInDBHandler
		implements EventHandler<TradeTransactionEvent>, WorkHandler<TradeTransactionEvent> {

	@Override
	public void onEvent(TradeTransactionEvent event, long sequence, boolean endOfBatch) throws Exception {
		this.onEvent(event);
	}

	@Override
	public void onEvent(TradeTransactionEvent event) throws Exception {
		// 这里做具体的消费逻辑
		event.setId(UUID.randomUUID().toString());// 简单生成下ID
		System.out.println(event.getId());
	}
}
