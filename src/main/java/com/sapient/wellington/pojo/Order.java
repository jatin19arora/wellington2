package com.sapient.wellington.pojo;

public class Order {
	private int orderId;
	private int securityId;
	TransactionType transactionType;
	String symbol;
	Integer quantity;
	Double limitPrice;
	String orderInstruction;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(Double double1) {
		this.limitPrice = double1;
	}

	public String getOrderInstruction() {
		return orderInstruction;
	}

	public void setOrderInstruction(String orderInstruction) {
		this.orderInstruction = orderInstruction;
	}

}
