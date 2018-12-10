package com.lzhsite.disruptor.quickstart.tradeTransaction;

public class TradeTransactionEvent {
	private String id;// 交易ID
	private double price;// 交易金额

	public TradeTransactionEvent() {
	}

	public TradeTransactionEvent(String id, double price) {
		super();
		this.id = id;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
