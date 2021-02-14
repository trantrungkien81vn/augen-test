package com.augen.praticetest.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.augen.praticetest.service.PricingService;

@Component
public class ProfitFactorTask {

	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	PricingService pricingService;

	@Scheduled(fixedDelay = 1000)
	public void run() {
		logger.debug("ProfitFactorTask runnig");
		pricingService.sendProfitFactor();
	}

}
