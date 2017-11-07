package com.sapient.wellington.pojo;

public class OrderKey {
	private int securityId;
	private String symbol;
	private TransactionType transactionType;

	public OrderKey(int securityId, String symbol, TransactionType transactionType) {
		this.securityId = securityId;
		this.symbol = symbol;
		this.transactionType = transactionType;
	}

	public int getSecurityId() {
		return securityId;
	}

	public void setSecurityId(int securityId) {
		this.securityId = securityId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + securityId;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderKey other = (OrderKey) obj;
		if (securityId != other.securityId)
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (transactionType != other.transactionType)
			return false;
		return true;
	}

	
}
