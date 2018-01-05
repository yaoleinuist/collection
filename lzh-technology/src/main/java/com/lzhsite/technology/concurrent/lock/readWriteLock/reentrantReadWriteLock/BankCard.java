package com.lzhsite.technology.concurrent.lock.readWriteLock.reentrantReadWriteLock;

public class BankCard {
	private String cardid = "XZ456789";
	private int balance = 10000;

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
}
