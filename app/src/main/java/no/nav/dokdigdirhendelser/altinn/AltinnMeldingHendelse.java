package no.nav.dokdigdirhendelser.altinn;

import lombok.extern.slf4j.Slf4j;
import no.altinn.event.domain.CloudEvent;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import no.nav.dokdigdirhendelser.exception.KafkaTechnicalException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutionException;

import static no.nav.dokdigdirhendelser.altinn.CloudEventExtensions.getExtension;

@Slf4j
@Component
@EnableTransactionManagement
public class AltinnMeldingHendelse {

	private static final String KAFKA_NOT_AUTHENTICATED = "Not authenticated to publish to topic: ";
	private static final String KAFKA_FAILED_TO_SEND = "Failed to send message to kafka. Topic: ";

	private final KafkaTemplate<String, CloudEvent> kafkaTemplate;
	private final DokDigdirHendelserProperties.TopicsProperties topicsProperties;

	public AltinnMeldingHendelse(KafkaTemplate<String, CloudEvent> kafkaTemplate,
								 DokDigdirHendelserProperties altinnProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicsProperties = altinnProperties.topics();
	}

	public void publish(CloudEvent cloudEvent) {
		final String topic = topicsProperties.altinnMeldingHendelse();
		ProducerRecord<String, CloudEvent> altinnEventsProducerRecord = new ProducerRecord<>(
				topicsProperties.altinnMeldingHendelse(),
				cloudEvent.getId(),
				cloudEvent
		);

		try {
			kafkaTemplate.send(altinnEventsProducerRecord)
					.whenComplete((_, ex) -> {
						if (ex != null) {
							handleKafkaError(topic, ex);
						} else {
							log.info("altinnEvent med (id={}, resourceinstance={}) skrevet til topic: {}.",
									cloudEvent.getId(), getExtension(cloudEvent, "resourceinstance"), topic);
						}
					}).get();
		} catch (ExecutionException | InterruptedException e) {
			handleKafkaError(topic, e);
		}
	}

	private void handleKafkaError(String topic, Throwable ex) {
		if (ex.getCause() instanceof KafkaProducerException kpe
				&& kpe.getCause() instanceof TopicAuthorizationException) {
			throw new KafkaTechnicalException(KAFKA_NOT_AUTHENTICATED + topic, kpe.getCause());
		}
		throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topic, ex);
	}
}
