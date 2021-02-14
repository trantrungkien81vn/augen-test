package com.augen.praticetest.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CompositePrice implements Serializable {

	public CompositePrice() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CompositePrice(double profictFactor, double spotPrice, String currency) {
		this.profictFactor = profictFactor;
		this.spotPrice = spotPrice;
		this.currency = currency;
	}

	private double profictFactor;
	private double spotPrice;
	private String currency;

	public double getProfictFactor() {

		return this.profictFactor;
	}

	public void setProfictFactor(double profictFactor) {
		this.profictFactor = BigDecimal.valueOf(profictFactor).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
	}

	public double getSpotPrice() {
		return spotPrice;
	}

	public void setSpotPrice(double spotPrice) {
		this.spotPrice = BigDecimal.valueOf(spotPrice).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "CompositePrice [profictFactor=" + profictFactor + ", spotPrice=" + spotPrice + ", currency=" + currency
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		long temp;
		temp = Double.doubleToLongBits(profictFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(spotPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CompositePrice other = (CompositePrice) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (Double.doubleToLongBits(profictFactor) != Double.doubleToLongBits(other.profictFactor))
			return false;
		if (Double.doubleToLongBits(spotPrice) != Double.doubleToLongBits(other.spotPrice))
			return false;
		return true;
	}

}
