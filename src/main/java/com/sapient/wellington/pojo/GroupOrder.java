package com.sapient.wellington.pojo;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupOrder {
	private static AtomicInteger counter = new AtomicInteger();

	public GroupOrder() {
		this.id = counter.incrementAndGet();
	}

	private int quantity;
	private Integer id;
	private Double avgLimitprice;
	private String orderInstruction;
	private Date insertTime;
	private int securityId;
	private TransactionType transactionType;
	private String symbol;

	public AtomicInteger getCounter() {
		return counter;
	}

	public void setCounter(AtomicInteger counter) {
		this.counter = counter;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getAvgLimitprice() {
		return avgLimitprice;
	}

	public void setAvgLimitprice(Double avgLimitprice) {
		this.avgLimitprice = avgLimitprice;
	}

	public String getOrderInstruction() {
		return orderInstruction;
	}

	public void setOrderInstruction(String orderInstruction) {
		this.orderInstruction = orderInstruction;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public int getSecurityId() {
		return securityId;
	}

	public void setSecurityId(int securityId) {
		this.securityId = securityId;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Integer getId() {
		return this.id;
	}

}
