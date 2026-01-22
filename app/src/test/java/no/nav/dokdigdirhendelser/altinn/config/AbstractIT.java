package no.nav.dokdigdirhendelser.altinn.config;

import no.nav.dokdigdirhendelser.Application;
import no.nav.dokdigdirhendelser.altinn.AltinnEvents;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EmbeddedKafka(partitions = 1, brokerProperties = {
		"listeners=PLAINTEXT://localhost:9092",
		"port=9092"
})
@ActiveProfiles("itest")

@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		classes = {Application.class}
)
@AutoConfigureWireMock(port = 0)
public class AbstractIT {

	protected static final String PRIVAT_ALTINN_MELDING_TOPIC = "altinn-melding-hendelse";


	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	protected EmbeddedKafkaBroker embeddedKafkaBroker;

	protected Consumer<String, AltinnEvents> setupKafkaConsumer() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(embeddedKafkaBroker, "itest-group", true);
		consumerProps.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		consumerProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		consumerProps.put(GROUP_INSTANCE_ID_CONFIG, "itest-group-instance");

		var consumer = new DefaultKafkaConsumerFactory<String, AltinnEvents>(consumerProps).createConsumer();
		consumer.subscribe(singletonList(PRIVAT_ALTINN_MELDING_TOPIC));
		return consumer;
	}

	protected AltinnEvents readFromAltinnEventsTopic() {
		try (var consumer = setupKafkaConsumer()) {
			ConsumerRecord<String, AltinnEvents> singleRecord = KafkaTestUtils.getSingleRecord(consumer, PRIVAT_ALTINN_MELDING_TOPIC, ofSeconds(5));
			assertThat(singleRecord).withFailMessage("Record fra topic er null").isNotNull();
			return singleRecord.value();
		} catch (IllegalStateException _) {
			return null;
		}
	}
}
