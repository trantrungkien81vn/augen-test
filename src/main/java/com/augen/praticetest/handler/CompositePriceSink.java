package com.augen.praticetest.handler;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.augen.praticetest.domain.CompositePrice;
import com.augen.praticetest.domain.ProfitFactor;
import com.augen.praticetest.domain.SpotPrice;

@Component
public class CompositePriceSink {

	private final Log logger = LogFactory.getLog(getClass());

	@StreamListener
	@SendTo({ AugenMessageChannel.COMPOSITE_PRICE_OUT })
	public KStream<String, CompositePrice> process(
			@Input(AugenMessageChannel.PROFIT_FACTOR_IN) KStream<String, ProfitFactor> profitFactorStream,
			@Input(AugenMessageChannel.SPOT_PRICE_IN) KStream<String, SpotPrice> spotPriceStream) {
		
		KTable<String, ProfitFactor> profitFactorTable = profitFactorStream.toTable();
		KTable<String, SpotPrice> spotPriceTable = spotPriceStream.toTable();
		ValueJoiner<SpotPrice, ProfitFactor, CompositePrice> valueJoiner = new ValueJoiner<SpotPrice, ProfitFactor, CompositePrice>() {

			@Override
			public CompositePrice apply(SpotPrice value1, ProfitFactor value2) {
				try {
					return new CompositePrice(value2.getProfitFactor(), value1.getPrice(), value1.getCurrency());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		};

		KTable<String, CompositePrice> compositePriceTable = spotPriceTable.join(profitFactorTable, valueJoiner,
				Materialized
						.<String, CompositePrice, KeyValueStore<Bytes, byte[]>>as(
								AugenMessageChannel.COMPOSITE_PRICE_MV1)
						.withValueSerde(new JsonSerde<CompositePrice>(CompositePrice.class)));
		
		
		return compositePriceTable.toStream();
	}
}
