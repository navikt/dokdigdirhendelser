package no.nav.dokdigdirhendelser.altinn;

import no.altinn.event.domain.CloudEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
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
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.createValidCloudEvent;
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
				.body(createValidCloudEvent())
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnAltinnEvents() {
		CloudEvent cloudEvent = createValidCloudEvent(SPEC_VERSION);

		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(cloudEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		CloudEvent cloudEventReadFromTopic = readFromAltinnEventsTopic();

		assertThat(convertToJson(cloudEvent)).isEqualTo(convertToJson(cloudEventReadFromTopic));

		assertThat(cloudEventReadFromTopic.getId()).isEqualTo(EVENT_ID);
		assertThat(cloudEventReadFromTopic.getType()).isEqualTo(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ);
		assertThat(cloudEventReadFromTopic.getTime()).isEqualTo(TIME);
		assertThat(cloudEventReadFromTopic.getResource()).isEqualTo(ALTINN_EVENTS_RESOURCE);
		assertThat(cloudEventReadFromTopic.getResourceinstance()).isEqualTo(RESOURCE_INSTANCE);
		assertThat(cloudEventReadFromTopic.getSource()).isEqualTo(EVENT_SOURCE);
		assertThat(cloudEventReadFromTopic.getSpecversion()).isEqualTo(VERSION);
	}

	@ParameterizedTest
	@MethodSource
	void shouldReturnOKWhenAltinnEventRequestenAreInvalid(CloudEvent invalidEvent) {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(invalidEvent)
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnBadRequestWhenRequestContainsUnknownFields() {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(Map.of("ukjent_felt", "en_eller_annen_verdi"))
				.exchange()
				.expectStatus()
				.isBadRequest();

		assertTopicIsEmpty();
	}

	@Test
	void shoudThrowInternalServerErrorWhenSpecversionAreInvalid() {
		CloudEvent cloudEvent = createValidCloudEvent(INVALID_VERSION);

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(cloudEvent)
				.exchange()
				.expectStatus().is5xxServerError()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);

		assertTopicIsEmpty();
	}

	private static Stream<Arguments> shouldReturnOKWhenAltinnEventRequestenAreInvalid() {
		return Stream.of(
				//invalid type
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(INVALID_EVENT_TYPE)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build()),
				//invalid time
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.resource(ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(SPEC_VERSION)
								.build()),
				//invalid resource
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(INVALID_ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(SPEC_VERSION)
								.build()),
				//invalid alternativesubject
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(INVALID_ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(SPEC_VERSION)
								.build()),
				//invalid resourceinstance
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build()),
				//invalid source
				Arguments.of(
						CloudEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.specversion(VERSION)
								.build())
		);
	}

	private String convertToJson(CloudEvent cloudEvent) {
		return jsonMapper.writeValueAsString(cloudEvent);
	}
}
