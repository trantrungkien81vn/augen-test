package com.augen.praticetest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.augen.praticetest.domain.CoinbaseResponse;
import com.augen.praticetest.domain.CompositePrice;
import com.augen.praticetest.domain.ProfitFactor;
import com.augen.praticetest.domain.SpotPrice;
import com.augen.praticetest.handler.AugenMessageChannel;

@Service
public class PricingService {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@Autowired
	@Qualifier(AugenMessageChannel.SPOT_PRICE_OUT)
	private MessageChannel spotPriceChannel;

	@Autowired
	@Qualifier(AugenMessageChannel.PROFIT_FACTOR_OUT)
	private MessageChannel profitFactorChannel;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	@Qualifier("currencies")
	private CopyOnWriteArraySet<String> currencies;

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Get Get the price from KTable.
	 * 
	 * @param currency
	 * @return
	 */
	public Optional<CompositePrice> getPricing(String currency) {
		try {
			ReadOnlyKeyValueStore<String, CompositePrice> keyValueStore = interactiveQueryService
					.getQueryableStore(AugenMessageChannel.COMPOSITE_PRICE_MV1, QueryableStoreTypes.keyValueStore());

			KeyValueIterator<String, CompositePrice> all = keyValueStore.all();
			while (all.hasNext()) {
				KeyValue<String, CompositePrice> value = all.next();
				if (currency.equalsIgnoreCase(value.key)) {
					return Optional.of(value.value);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info(
				"No composite price in table " + AugenMessageChannel.COMPOSITE_PRICE_MV1 + "for currency:" + currency);
		return Optional.empty();

	}
	/**
	 * Get all pricing of all currencies in KTable
	 * 
	 * @return
	 */
	public List<CompositePrice> getPricing() {

		ReadOnlyKeyValueStore<String, CompositePrice> keyValueStore = interactiveQueryService
				.getQueryableStore(AugenMessageChannel.COMPOSITE_PRICE_MV1, QueryableStoreTypes.keyValueStore());

		List<CompositePrice> result = new ArrayList<>();
		KeyValueIterator<String, CompositePrice> all = keyValueStore.all();
		while (all.hasNext()) {
			KeyValue<String, CompositePrice> value = all.next();
			result.add(value.value);
		}
		return result;
	}

	/**
	 * Send Spot Price to Kafka topic
	 * 
	 * @param currency
	 */
	public void sendSpotPriceEvent(String currency) {

		SpotPrice spEvent = callCoinbasePrice(currency);
		logger.debug("coinbase price for currency-" + spEvent.getCurrency() + ": " + spEvent.getPrice());
		Message<SpotPrice> message = MessageBuilder.withPayload(spEvent)
				.setHeader(KafkaHeaders.MESSAGE_KEY, spEvent.getCurrency().getBytes()).build();
		try {
			this.spotPriceChannel.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Send Profit factor to Kafka topic
	 * 
	 * @param currency
	 */
	public void sendProfitFactor() {
		final double[] profitFactor = { 0.05, 0.1, 0.12 };
		double pf = profitFactor[new Random().nextInt(profitFactor.length)];
		logger.debug("Profit factor changed to " + pf);
		for (String cur : currencies) {
			ProfitFactor pEvent = new ProfitFactor(pf, cur);
			Message<ProfitFactor> message = MessageBuilder.withPayload(pEvent)
					.setHeader(KafkaHeaders.MESSAGE_KEY, pEvent.getCurrency().getBytes()).build();
			try {
				this.profitFactorChannel.send(message);
			} catch (Exception e) {
				logger.error(e);
			}

		}

	}

	/**
	 * Get the price from coinbase website and saving new curreny in currencies obj.
	 * 
	 * @param currency
	 * @return
	 */
	public Supplier<? extends CompositePrice> getPriceFromCoinbase(String currency) {
		SpotPrice spotPriceEvent = callCoinbasePrice(currency);
		Optional<CompositePrice> opt = getPricing("NZD");
		CompositePrice result = new CompositePrice();
		result.setCurrency(currency);
		result.setProfictFactor(opt.get().getProfictFactor());
		result.setSpotPrice(spotPriceEvent.getPrice());
		// update list of currencies
		currencies.add(currency);
		return () -> result;
	}

	// private function
	private SpotPrice callCoinbasePrice(String currency) {
		ResponseEntity<CoinbaseResponse> response = restTemplate
				.getForEntity("https://api.coinbase.com/v2/prices/spot?currency=" + currency, CoinbaseResponse.class);
		CoinbaseResponse price = response.getBody();
		logger.debug("Price from coinbase:" + price);
		SpotPrice spEvent = new SpotPrice(Double.valueOf(price.getData().getAmount()), price.getData().getCurrency());
		return spEvent;
	}

}
