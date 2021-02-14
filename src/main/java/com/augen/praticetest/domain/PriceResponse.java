package com.augen.praticetest.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceResponse {
	private String id;
	private BigDecimal sportPrice;
	private double profitFactor;
	private double amount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProfitFactor(double profitFactor) {
		BigDecimal bd = new BigDecimal(profitFactor).setScale(2, RoundingMode.HALF_DOWN);
		this.profitFactor = bd.doubleValue();
	}

	public void setSportPrice(double sportPrice) {
		BigDecimal bd = new BigDecimal(sportPrice).setScale(2, RoundingMode.HALF_DOWN);
		this.sportPrice = bd;
	}

	public void setAmount(double amount) {
		BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_DOWN);
		this.amount = bd.doubleValue();
	}

	public double getAmount() {

		return amount;
	}

	public BigDecimal getTotalPrice() {
		double total = this.sportPrice.doubleValue() * (1+this.profitFactor) * this.amount;
		BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_DOWN);
		return bd;
	}

	public BigDecimal getSportPrice() {
		return sportPrice;
	}

	public double getProfitFactor() {
		return profitFactor;
	}

}
