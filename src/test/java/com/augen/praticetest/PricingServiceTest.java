package com.augen.praticetest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.augen.praticetest.domain.CoinbaseResponse;
import com.augen.praticetest.domain.ProfitFactor;
import com.augen.praticetest.domain.ProfitFactorTask;
import com.augen.praticetest.domain.SpotPrice;
import com.augen.praticetest.domain.SpotPriceTask;
import com.augen.praticetest.service.PricingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
		"spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
		"spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=org.springframework.kafka.support.serializer.JsonSerde",
		"spring.cloud.stream.bindings.pfo.destination=" + PricingServiceTest.PROFIT_FACTOR_TOPIC,
		"spring.cloud.stream.bindings.spo.destination=" + PricingServiceTest.SPOT_PRICE_TOPIC,
		"spring.cloud.stream.bindings.cpo.destination=" + PricingServiceTest.COMPOSITE_PRICE_TOPIC,
		"spring.cloud.stream.bindings.pfi.destination=" + PricingServiceTest.PROFIT_FACTOR_TOPIC,
		"spring.kafka.consumer.group-id=EmbeddedKafkaIntTest",
		"spring.cloud.stream.bindings.spi.destination="
				+ PricingServiceTest.SPOT_PRICE_TOPIC }, webEnvironment = SpringBootTest.WebEnvironment.MOCK)

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
public class PricingServiceTest {

	static final String PROFIT_FACTOR_TOPIC = "profit-factor-topic";
	static final String SPOT_PRICE_TOPIC = "spot-price-topic";
	static final String COMPOSITE_PRICE_TOPIC = "composite-price-topic";

	@Mock
	private RestTemplate restTemplate;

	ObjectMapper objMapper = new ObjectMapper();

	@Autowired
	@InjectMocks
	PricingService pricingService;

	@Autowired
	EmbeddedKafkaBroker embeddedKafka;

	@Test
	public void testSendReceiveSpotPrice() throws JsonMappingException, JsonProcessingException {
		String jsonValue = "{\n" + "  \"data\": {\n" + "    \"amount\": \"1015.00\",\n" + "    \"currency\": \"NZD\",\n"
				+ "    \"base\": \"BTC\"\n" + "  }\n" + "}";
		CoinbaseResponse obj = objMapper.readValue(jsonValue, CoinbaseResponse.class);
		ResponseEntity<CoinbaseResponse> result = new ResponseEntity<CoinbaseResponse>(obj, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity("https://api.coinbase.com/v2/prices/spot?currency=NZD",
				CoinbaseResponse.class)).thenReturn(result);
		pricingService.sendSpotPriceEvent("NZD");

		DefaultKafkaConsumerFactory<String, SpotPrice> consumerFactory = createDefaultKafkaConsumerFactory(
				SpotPrice.class,"spotPrice");
		Consumer<String, SpotPrice> consumer = consumerFactory.createConsumer();
		embeddedKafka.consumeFromAnEmbeddedTopic(consumer, SPOT_PRICE_TOPIC);
		ConsumerRecords<String, SpotPrice> replies = KafkaTestUtils.getRecords(consumer);
		assertNotNull(replies);
 
		assertThat(replies.count()).isEqualTo(2);
	}
	
	@Test
	public void testSendReceiveProfitFactor() throws JsonMappingException, JsonProcessingException {
		pricingService.sendProfitFactor();
		
		DefaultKafkaConsumerFactory<String, ProfitFactor> consumerFactory = createDefaultKafkaConsumerFactory(
				ProfitFactor.class,"profitFactor");
		Consumer<String, ProfitFactor> consumer = consumerFactory.createConsumer();
		embeddedKafka.consumeFromAnEmbeddedTopic(consumer, PROFIT_FACTOR_TOPIC);
		ConsumerRecords<String, ProfitFactor> replies = KafkaTestUtils.getRecords(consumer);
		assertNotNull(replies);
		assertThat(replies.count()).isEqualTo(2);
		
		
	}
	

	private DefaultKafkaConsumerFactory createDefaultKafkaConsumerFactory(Class obj, String groupName) {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(groupName, "false",
				embeddedKafka);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		DefaultKafkaConsumerFactory<String, SpotPrice> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProps, new StringDeserializer(), new JsonDeserializer<>(obj).ignoreTypeHeaders());
		return consumerFactory;
	}

}