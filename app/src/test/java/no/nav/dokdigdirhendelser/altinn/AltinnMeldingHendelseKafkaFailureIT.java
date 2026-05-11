package no.nav.dokdigdirhendelser.altinn;

import no.altinn.event.domain.CloudEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;

import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.createValidAltinnEvent;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;

class AltinnMeldingHendelseKafkaFailureIT extends AbstractIT {

	@MockitoBean
	private KafkaTemplate<String, CloudEvent> kafkaTemplate;

	@Test
	void shouldReturnInternalServerErrorWhenKafkaPublishFails() {
		CompletableFuture<SendResult<String, CloudEvent>> failedFuture = new CompletableFuture<>();
		failedFuture.completeExceptionally(new RuntimeException("Kafka broker unavailable"));

		when(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, CloudEvent>>any())).thenReturn(failedFuture);

		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(createValidAltinnEvent())
				.exchange()
				.expectStatus().is5xxServerError();
	}
}
