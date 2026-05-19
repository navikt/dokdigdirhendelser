package no.nav.dokdigdirhendelser.altinn;

import no.altinn.event.domain.CloudEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.EVENT_ID;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.EVENT_SOURCE;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.INVALID_ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.INVALID_EVENT_TYPE;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.INVALID_VERSION;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.RESOURCE_INSTANCE;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.TIME;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.VERSION;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.buildCloudEventJson;
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.createValidCloudEventJson;
import static no.nav.dokdigdirhendelser.altinn.CloudEventExtensions.getExtension;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class AltinnEventControllerIT extends AbstractIT {

	@Test
	void shouldReturnOkWhenValidateEvent() {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body("""
						{
							 "id": "694caa35-8b25-4cd7-b800-f6eeb93c56ed",
							 "source": "https://platform.altinn.no/events/api/v1/subscriptions/1234",
							 "type": "platform.events.validatesubscription",
							 "specversion": "1.0"
						 }
						""")
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnOkWhenCodeDoesNotMatch() {
		restTestClient.post()
				.uri("/rest/webhook/path?code=hei")
				.contentType(APPLICATION_JSON)
				.body(createValidCloudEventJson())
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnAltinnEvents() {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(createValidCloudEventJson(SPEC_VERSION))
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		CloudEvent cloudEventReadFromTopic = readFromAltinnEventsTopic();

		assertThat(cloudEventReadFromTopic.getId()).isEqualTo(EVENT_ID.toString());
		assertThat(cloudEventReadFromTopic.getType()).isEqualTo(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ);
		assertThat(cloudEventReadFromTopic.getTime()).isEqualTo(TIME);
		assertThat(getExtension(cloudEventReadFromTopic, "resource")).isEqualTo(ALTINN_EVENTS_RESOURCE);
		assertThat(getExtension(cloudEventReadFromTopic, "resourceinstance")).isEqualTo(RESOURCE_INSTANCE.toString().toLowerCase());
		assertThat(cloudEventReadFromTopic.getSource()).isEqualTo(EVENT_SOURCE);
		assertThat(cloudEventReadFromTopic.getSpecVersion().getVersionId()).isEqualTo(VERSION);
	}

	@ParameterizedTest
	@MethodSource
	void shouldReturnOKWhenAltinnEventRequestenAreInvalid(String invalidEventJson) {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(invalidEventJson)
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnBadRequestWhenRequestContainsUnknownFields() {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body("""
						{"ukjent_felt": "en_eller_annen_verdi"}
						""")
				.exchange()
				.expectStatus()
				.isBadRequest();

		assertTopicIsEmpty();
	}

	@Test
	void shoudThrowInternalServerErrorWhenSpecversionAreInvalid() {
		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(createValidCloudEventJson(INVALID_VERSION))
				.exchange()
				.expectStatus().is5xxServerError()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);

		assertTopicIsEmpty();
	}

	private static Stream<Arguments> shouldReturnOKWhenAltinnEventRequestenAreInvalid() {
		return Stream.of(
				//invalid type
				Arguments.of(buildCloudEventJson(INVALID_EVENT_TYPE, ALTINN_EVENTS_RESOURCE, RESOURCE_INSTANCE.toString(), VERSION)),
				//invalid resource
				Arguments.of(buildCloudEventJson(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ, INVALID_ALTINN_EVENTS_RESOURCE, RESOURCE_INSTANCE.toString(), SPEC_VERSION)),
				//invalid resourceinstance (null)
				Arguments.of(buildCloudEventJson(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ, ALTINN_EVENTS_RESOURCE, null, VERSION))
		);
	}
}
