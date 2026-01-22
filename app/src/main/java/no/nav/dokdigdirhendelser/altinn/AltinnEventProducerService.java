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

import java.util.concurrent.ExecutionException;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_ALTERNATIVE_SUBJECT;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
@EnableTransactionManagement
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

	public void publish(AltinnEvents altinnEvents) {
		ProducerRecord<String, AltinnEvents> altinnEventsProducerRecord = new ProducerRecord<>(
				topicsProperties.altinnMeldingHendelse(),
				null,
				System.currentTimeMillis(),
				altinnEvents.id(),
				altinnEvents
		);

		try {
			SendResult<String, AltinnEvents> sendResult = kafkaTemplate.send(altinnEventsProducerRecord).get();
			log.info("altinnEvent med (id={}, resourceinstance={}) skrevet til topic: {}. hendelseMetadata={}",
					altinnEvents.id(), altinnEvents.resourceinstance(), topicsProperties.altinnMeldingHendelse(),
					sendResult.getRecordMetadata()
			);
		} catch (ExecutionException executionException) {
			if (executionException.getCause() instanceof KafkaProducerException kafkaProducerException && kafkaProducerException.getCause() instanceof TopicAuthorizationException) {
				throw new KafkaTechnicalException(KAFKA_NOT_AUTHENTICATED + topicsProperties.altinnMeldingHendelse(), kafkaProducerException.getCause());
			}
			throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topicsProperties.altinnMeldingHendelse(), executionException);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topicsProperties.altinnMeldingHendelse(), e);
		}
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
