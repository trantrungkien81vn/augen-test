package com.augen.praticetest.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProfitFactor {

	public ProfitFactor() {
		super();
	}

	private double profitFactor;
	private double oldProfitFactor;
	public double getOldProfitFactor() {
		return oldProfitFactor;
	}

	public void setOldProfitFactor(double oldProfitFactor) {
		this.oldProfitFactor = oldProfitFactor;
	}

	private boolean isChange;
	private String currency;

	public ProfitFactor(double profitFactor, String currency) {
		super();
		this.profitFactor = profitFactor;
		this.currency = currency;
	}

	public double getProfitFactor() {
		return profitFactor;
	}

	public void setProfitFactor(double profitFactor) {
		this.profitFactor = BigDecimal.valueOf(profitFactor).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
		;
	}

	@Override
	public String toString() {
		return "ProfitFactor [profitFactor=" + profitFactor + ", isChange=" + isChange + ", currency=" + currency + "]";
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
