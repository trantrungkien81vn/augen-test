package com.augen.praticetest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.augen.praticetest.domain.BitcoinResponse;
import com.augen.praticetest.domain.CompositePrice;
import com.augen.praticetest.domain.PriceResponse;
import com.augen.praticetest.service.PricingService;

@RestController
@RequestMapping("/pricing")
public class PriceController {

	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private PricingService pricingService;

	/**
	 * return total price for amount of BTC
	 * 
	 * @param amount
	 * @param currency
	 * @return
	 */
	@GetMapping
	public PriceResponse calc(@RequestParam(name = "a", defaultValue = "1.0") double amount,
			@RequestParam(name = "c", defaultValue = "NZD") String currency) {

		PriceResponse response = new PriceResponse();
		Optional<CompositePrice> compositePriceOpt = pricingService.getPricing(currency);
		CompositePrice compositePrice = compositePriceOpt.orElseGet(pricingService.getPriceFromCoinbase(currency));
		response.setId(compositePrice.getCurrency());
		response.setAmount(amount);
		response.setSportPrice(compositePrice.getSpotPrice());
		response.setProfitFactor(compositePrice.getProfictFactor());
		return response;
	}

	/**
	 * return amount of bitcoin for a specific amount of money
	 * 
	 * @param amount
	 * @param currency
	 * @return
	 */
	@GetMapping("/btc")
	public BitcoinResponse bitcoinCal(@RequestParam(name = "a", required = true) double amount,
			@RequestParam(name = "c", defaultValue = "NZD") String currency) {

		BitcoinResponse response = new BitcoinResponse();
		Optional<CompositePrice> compositePriceOpt = pricingService.getPricing(currency);
		CompositePrice compositePrice = compositePriceOpt.orElseGet(pricingService.getPriceFromCoinbase(currency));
		response.setId(compositePrice.getCurrency());
		response.setAmount(amount);
		response.setProfitFactor(compositePrice.getProfictFactor());
		response.setSpotPrice(compositePrice.getSpotPrice());
		return response;
	}

	/**
	 * it is used for debug data currently in KTable spot price and profit factor
	 * for each currency at the moment
	 * 
	 * @return
	 */
	@GetMapping("/data")
	public List<PriceResponse> queryKTableData() {

		List<PriceResponse> result = new ArrayList<PriceResponse>();
		for (CompositePrice e : pricingService.getPricing()) {
			PriceResponse response = new PriceResponse();
			response.setId(e.getCurrency());
			response.setAmount(1);
			response.setSportPrice(e.getSpotPrice());
			response.setProfitFactor(e.getProfictFactor());
			result.add(response);
		}
		return result;
	}

}
