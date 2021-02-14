package com.augen.praticetest;

import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.augen.praticetest.domain.CompositePrice;
import com.augen.praticetest.domain.PriceResponse;
import com.augen.praticetest.domain.ProfitFactorTask;
import com.augen.praticetest.domain.SpotPriceTask;
import com.augen.praticetest.service.PricingService;
@SpringBootTest(properties = {
		"spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
		"spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=org.springframework.kafka.support.serializer.JsonSerde",
		"spring.cloud.stream.bindings.pfo.destination=" + PricingServiceTest.PROFIT_FACTOR_TOPIC,
		"spring.cloud.stream.bindings.spo.destination=" + PricingServiceTest.SPOT_PRICE_TOPIC,
		"spring.cloud.stream.bindings.cpo.destination=" + PricingServiceTest.COMPOSITE_PRICE_TOPIC,
		"spring.cloud.stream.bindings.pfi.destination=" + PricingServiceTest.PROFIT_FACTOR_TOPIC,
		"spring.kafka.consumer.group-id=EmbeddedKafkaIntTest",
		"spring.cloud.stream.bindings.spi.destination="
				+ PricingServiceTest.SPOT_PRICE_TOPIC }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(controlledShutdown = true, bootstrapServersProperty = "spring.kafka.bootstrap-servers", topics = {
		PricingServiceTest.PROFIT_FACTOR_TOPIC, PricingServiceTest.SPOT_PRICE_TOPIC,
		PricingServiceTest.COMPOSITE_PRICE_TOPIC })
@ImportAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, TransactionAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@RunWith(SpringRunner.class)
//disable scheduling when running unit test
@MockBean(SpotPriceTask.class)
@MockBean(ProfitFactorTask.class)
public class PricingControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@SpyBean
	PricingService pricingService;

	static final String URL = "http://localhost";

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		CompositePrice compositePrice = new CompositePrice();
		compositePrice.setCurrency("NZD");
		compositePrice.setSpotPrice(4000.23);
		compositePrice.setProfictFactor(0.12);
		Optional<CompositePrice> opt = Optional.of(compositePrice);
		// Mock method2 for every test.
		doReturn(opt).when(pricingService).getPricing("NZD");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getPricingNoParameters() throws Exception {
		PriceResponse response = this.restTemplate.getForObject(URL + ":" + port + "/pricing", PriceResponse.class);
		Assert.assertNotNull(response);
		Assert.assertEquals(BigDecimal.valueOf(4000.23 * (1+0.12)).setScale(2, RoundingMode.HALF_DOWN).doubleValue(),
				response.getTotalPrice().doubleValue(), 0.0);
	}

	@Test
	public void getPricingForUSD() throws Exception {
		PriceResponse response = this.restTemplate.getForObject(URL + ":" + port + "/pricing?c=USD",
				PriceResponse.class);
		Assert.assertNotNull(response);
		Assert.assertNotEquals(response.getSportPrice().doubleValue(), 0.0,0.0);
		
	}

}
