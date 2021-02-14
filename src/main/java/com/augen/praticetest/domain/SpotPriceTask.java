package com.augen.praticetest.domain;

import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.augen.praticetest.service.PricingService;

@Component
public class SpotPriceTask  {

	private final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	@Qualifier("currencies")
	private CopyOnWriteArraySet<String> currencies;
	
	@Autowired
	private PricingService srv;

	@Scheduled(fixedDelay = 1000, initialDelay = 1000)
	public void run() {
		logger.debug("SpotPriceTask runnig");
		for (String cur : currencies) {
			srv.sendSpotPriceEvent(cur);
		}
	}

}
