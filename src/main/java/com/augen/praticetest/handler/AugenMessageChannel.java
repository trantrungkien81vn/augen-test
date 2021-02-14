package com.augen.praticetest.handler;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.augen.praticetest.domain.ProfitFactor;
import com.augen.praticetest.domain.SpotPrice;

@Component
public interface AugenMessageChannel {

	public static String PROFIT_FACTOR_OUT = "pfo";
	public static String SPOT_PRICE_OUT = "spo";
	public static String SPOT_PRICE_IN = "spi";
	public static String PROFIT_FACTOR_IN = "pfi";
	public static String COMPOSITE_PRICE_MV1 = "composite-price-store1";
	public static String COMPOSITE_PRICE_OUT = "cpo";
	
	// out bound channle
	@Output(PROFIT_FACTOR_OUT)
	MessageChannel profitFactorOut();

	@Output(SPOT_PRICE_OUT)
	MessageChannel spotPriceOut();
	
	@Output(COMPOSITE_PRICE_OUT)
	KStream<?,?> compositePriceOut();

	@Input(SPOT_PRICE_IN)
	KStream<String, SpotPrice> spotPriceIn();

	@Input(PROFIT_FACTOR_IN)
	KStream<String, ProfitFactor> profitFactorIn();
	

}