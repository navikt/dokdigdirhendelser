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
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_ALTERNATIVE_SUBJECT;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class AltinnEventProducerService {

	private static final String KAFKA_NOT_AUTHENTICATED = "Not authenticated to publish to topic: ";
	private static final String KAFKA_FAILED_TO_SEND = "Failed to send message to kafka. Topic: ";

	private final KafkaTemplate<String, AltinnEvents> kafkaTemplate;
	private final DokDigdirHendelserProperties.TopicsProperties topicsProperties;

	public AltinnEventProducerService(KafkaTemplate<String, AltinnEvents> kafkaTemplate,
									  DokDigdirHendelserProperties altinnProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicsProperties = altinnProperties.topics();
	}

	@Transactional
	public void publish(AltinnEvents altinnEvents) {
		validateAltinnEvent(altinnEvents);
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

	public void validateAltinnEvent(AltinnEvents altinnEvents) {
		if (!ALTINN_EVENTS_RESOURCE.equals(altinnEvents.resource())) {
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!ALTINN_ALTERNATIVE_SUBJECT.equals(altinnEvents.alternativesubject())) {
			throw new IllegalArgumentException("Ugyldig verdi: alternativeSubject er ikke lik " + ALTINN_ALTERNATIVE_SUBJECT);

		}

		if (isBlank(altinnEvents.type())) {
			throw new IllegalArgumentException("Altinn event type kan ikke være tømt");
		}
	}
}
