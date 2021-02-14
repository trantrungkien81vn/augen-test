package com.augen.praticetest.domain;

public class CoinbaseResponse {

	@Override
	public String toString() {
		return "CoinbaseResponse [data=" + data + "]";
	}

	private CoinbasePrice data;

	public CoinbasePrice getData() {
		return data;
	}

	public void setData(CoinbasePrice data) {
		this.data = data;
	}

	public CoinbaseResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

}
