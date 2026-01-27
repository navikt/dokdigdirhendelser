package no.nav.dokdigdirhendelser.altinn;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import no.nav.dokdigdirhendelser.exception.KafkaTechnicalException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@EnableTransactionManagement
public class AltinnMeldingHendelse {

	private static final String KAFKA_NOT_AUTHENTICATED = "Not authenticated to publish to topic: ";
	private static final String KAFKA_FAILED_TO_SEND = "Failed to send message to kafka. Topic: ";

	private final KafkaTemplate<String, AltinnEvents> kafkaTemplate;
	private final DokDigdirHendelserProperties.TopicsProperties topicsProperties;

	public AltinnMeldingHendelse(KafkaTemplate<String, AltinnEvents> kafkaTemplate,
								 DokDigdirHendelserProperties altinnProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicsProperties = altinnProperties.topics();
	}

	public void publish(AltinnEvents altinnEvents) {
		String topic = topicsProperties.altinnMeldingHendelse();
		ProducerRecord<String, AltinnEvents> altinnEventsProducerRecord = new ProducerRecord<>(
				topicsProperties.altinnMeldingHendelse(),
				null,
				System.currentTimeMillis(),
				altinnEvents.id(),
				altinnEvents
		);

		CompletableFuture<SendResult<String, AltinnEvents>> future = kafkaTemplate.send(altinnEventsProducerRecord);
		future.whenComplete((result, ex) -> {
			if (ex != null) {
				handleKafkaError(topic, ex);
			} else {
				log.info("altinnEvent med (id={}, resourceinstance={}) skrevet til topic: {}. metadata={}",
						altinnEvents.id(), altinnEvents.resourceinstance(), topic, result.getRecordMetadata());
			}
		});
	}

	private void handleKafkaError(String topic, Throwable ex) {
		if (ex.getCause() instanceof KafkaProducerException kpe
				&& kpe.getCause() instanceof TopicAuthorizationException) {
			throw new KafkaTechnicalException(KAFKA_NOT_AUTHENTICATED + topic, kpe.getCause());
		}
		throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topic, ex);
	}
}
