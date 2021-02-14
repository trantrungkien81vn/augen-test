package com.augen.praticetest.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.augen.praticetest.handler.AugenMessageChannel;

@Component
public class PricingMonitorTask {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	private final Log logger = LogFactory.getLog(getClass());

	final Map<String, CompositePrice> prices = new HashMap<>();
	double currentProfitFactor = 0.0;

	@Scheduled(fixedRate = 1000, initialDelay = 3000)
	public void checkPringChange() {

		try {
			ReadOnlyKeyValueStore<String, CompositePrice> keyValueStore = interactiveQueryService
					.getQueryableStore(AugenMessageChannel.COMPOSITE_PRICE_MV1, QueryableStoreTypes.keyValueStore());

			KeyValueIterator<String, CompositePrice> all = keyValueStore.all();
			while (all.hasNext()) {
				KeyValue<String, CompositePrice> pair = all.next();

				if (prices.containsKey(pair.key)) {
					CompositePrice newValue = pair.value;
					CompositePrice oldValue = prices.get(pair.key);
					// check profit factor change and print to console.
					if (pair.key.equalsIgnoreCase("NZD")) {
						if (currentProfitFactor != newValue.getProfictFactor()) {						
							logger.info(" Profit factor changed from " +currentProfitFactor+ " to "
									+ newValue.getProfictFactor());
							currentProfitFactor = newValue.getProfictFactor();
						}
					}
					//check price of each currency and print to console
					if (oldValue.getSpotPrice() != newValue.getSpotPrice()) {
						logger.info(pair.key + " price changed from " + BigDecimal.valueOf(oldValue.getSpotPrice()).setScale(2, RoundingMode.HALF_DOWN) + " to "
								+BigDecimal.valueOf(newValue.getSpotPrice()).setScale(2, RoundingMode.HALF_DOWN));
						prices.put(pair.key, newValue);
					}
				} else {
					logger.info(pair.key + " price updated " + pair.value.getSpotPrice());
					prices.put(pair.key, pair.value);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
