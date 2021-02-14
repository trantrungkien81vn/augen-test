package com.augen.praticetest.domain;

public class CoinbasePrice {

	@Override
	public String toString() {
		return "CoinbasePrice [currency=" + currency + ", base=" + base + ", amount=" + amount + "]";
	}

	public CoinbasePrice() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CoinbasePrice(String currency, String base, Double amount) {
		super();
		this.currency = currency;
		this.base = base;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	private String currency;
	private String base;
	private Double amount;

}
