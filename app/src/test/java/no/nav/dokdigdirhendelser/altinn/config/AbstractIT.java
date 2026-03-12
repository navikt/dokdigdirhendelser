package no.nav.dokdigdirhendelser.altinn.config;

import no.nav.dokdigdirhendelser.Application;
import no.nav.dokdigdirhendelser.altinn.AltinnEvent;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@EnableConfigurationProperties({DokDigdirHendelserProperties.class})
@EmbeddedKafka(topics = {"test-ut-topic"},
		partitions = 1,
		controlledShutdown = true
)
@ActiveProfiles("itest")
public class AbstractIT {

	protected static final String PRIVAT_ALTINN_MELDING_TOPIC = "altinn-melding-hendelse";

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	protected EmbeddedKafkaBroker embeddedKafkaBroker;

	protected Consumer<String, AltinnEvent> setupKafkaConsumer() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(embeddedKafkaBroker, "itest-group", true);
		consumerProps.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class.getName());
		consumerProps.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, AltinnEvent.class.getName());

		consumerProps.put(GROUP_INSTANCE_ID_CONFIG, "itest-group-instance");

		var consumer = new DefaultKafkaConsumerFactory<String, AltinnEvent>(consumerProps).createConsumer();
		consumer.subscribe(singletonList(PRIVAT_ALTINN_MELDING_TOPIC));
		return consumer;
	}

	protected AltinnEvent readFromAltinnEventsTopic() {
		try (var consumer = setupKafkaConsumer()) {
			ConsumerRecord<String, AltinnEvent> singleRecord = KafkaTestUtils.getSingleRecord(consumer, PRIVAT_ALTINN_MELDING_TOPIC, ofSeconds(5));
			assertThat(singleRecord).withFailMessage("Record fra topic er null").isNotNull();
			return singleRecord.value();
		} catch (IllegalStateException _) {
			return null;
		}
	}

	protected void assertTopicIsEmpty() {
		try (var consumer = setupKafkaConsumer()) {
			while (consumer.assignment().isEmpty()) {
				consumer.poll(Duration.ofMillis(50));
			}
			var records = consumer.poll(Duration.ofMillis(200));
			assertThat(records.isEmpty())
					.withFailMessage("Forventet ingen meldinger på topic, men fant %d", records.count())
					.isTrue();
		}
	}
}
