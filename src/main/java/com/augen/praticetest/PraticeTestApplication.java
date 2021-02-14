package com.augen.praticetest;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.augen.praticetest.handler.AugenMessageChannel;
import com.augen.praticetest.service.PricingService;

@SpringBootApplication
@EnableBinding(AugenMessageChannel.class)
@EnableWebMvc
@EnableScheduling
public class PraticeTestApplication implements ApplicationRunner{

	public static void main(String[] args) {
		SpringApplication.run(PraticeTestApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "currencies")
	public CopyOnWriteArraySet<String> currencies() {
		return new CopyOnWriteArraySet<String>(Arrays.asList("NZD"));
	}
	
	@Autowired
	PricingService srv;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		srv.sendSpotPriceEvent("NZD");
		srv.sendProfitFactor();
	}

}
