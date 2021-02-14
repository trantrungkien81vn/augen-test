package com.augen.praticetest.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BitcoinResponse {
	private String id;
	private double amount;
	private double bitcoinAmount;
	private BigDecimal spotPrice;
	
	public BigDecimal getSpotPrice() {
		return spotPrice;
	}

	public void setSpotPrice(double spotPrice) {
		BigDecimal bd = new BigDecimal(spotPrice).setScale(2, RoundingMode.HALF_DOWN);
		this.spotPrice = bd;
	}

	public double getProfitFactor() {
		return profitFactor;
	}

	public void setProfitFactor(double profitFactor) {
		this.profitFactor = profitFactor;
	}
	private double profitFactor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBitcoinAmount() {

		double btcAmunt = this.amount / (this.spotPrice.doubleValue() * (this.profitFactor+1));
		BigDecimal bd = new BigDecimal(btcAmunt).setScale(2, RoundingMode.HALF_DOWN);
		bitcoinAmount = bd.doubleValue();
		return this.bitcoinAmount;
	}

}
