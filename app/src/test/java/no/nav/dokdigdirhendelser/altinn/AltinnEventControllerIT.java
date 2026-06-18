package no.nav.dokdigdirhendelser.altinn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
import static no.nav.dokdigdirhendelser.altinn.AltinnEventTestData.createValidAltinnEvent;
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
				.body(createValidAltinnEvent())
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnAltinnEvents() {
		AltinnEvent altinnEvent = createValidAltinnEvent(SPEC_VERSION);

		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(altinnEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		AltinnEvent altinnEventReadFromTopic = readFromAltinnEventsTopic();

		assertThat(convertToJson(altinnEvent)).isEqualTo(convertToJson(altinnEventReadFromTopic));

		assertThat(altinnEventReadFromTopic.id()).isEqualTo(EVENT_ID);
		assertThat(altinnEventReadFromTopic.type()).isEqualTo(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ);
		assertThat(altinnEventReadFromTopic.time()).isEqualTo(TIME);
		assertThat(altinnEventReadFromTopic.resource()).isEqualTo(ALTINN_EVENTS_RESOURCE);
		assertThat(altinnEventReadFromTopic.resourceinstance()).isEqualTo(RESOURCE_INSTANCE);
		assertThat(altinnEventReadFromTopic.source()).isEqualTo(EVENT_SOURCE);
		assertThat(altinnEventReadFromTopic.specversion()).isEqualTo(VERSION);
	}

	@ParameterizedTest
	@MethodSource
	void shouldReturnOKWhenAltinnEventRequestenAreInvalid(AltinnEvent invalidEvent) {
		restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(invalidEvent)
				.exchange()
				.expectStatus().isOk();

		assertTopicIsEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {"\"\"", "\" \""})
	@NullSource
	void shouldReturnOkWithBodyWhenIdIsNullOrEmpty(String id) {
		var event = """
				{
				   "id": %s,
				   "resource": "urn:altinn:resource:nav_dokumentdistribusjon_taushetsbelagtpost",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": "no.altinn.correspondence.correspondencepublished",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event.formatted(id))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("id: must not be null");
		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnOkWithBodyWhenIdIsMissing() {
		var event = """
				{
				   "resource": "urn:altinn:resource:nav_dokumentdistribusjon_taushetsbelagtpost",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": "no.altinn.correspondence.correspondencepublished",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("id: must not be null");
		assertTopicIsEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {"\"\"", "\" \""})
	@NullSource
	void shouldReturnOkWithBodyWhenResourceIsNullOrEmpty(String resource) {
		var event = """
				{
				   "id": "aa992206-a705-4c27-ba99-409f2f472c24",
				   "resource": %s,
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": "no.altinn.correspondence.correspondencepublished",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event.formatted(resource))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("resource: must not be blank");
		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnOkWithBodyWhenResourceIsMissing() {
		var event = """
				{
				   "id": "aa992206-a705-4c27-ba99-409f2f472c24",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": "no.altinn.correspondence.correspondencepublished",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("resource: must not be blank");
		assertTopicIsEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {"\"\"", "\" \""})
	@NullSource
	void shouldReturnOkWithBodyWhenTypeIsNullOrEmpty(String type) {
		var event = """
				{
				   "id": "aa992206-a705-4c27-ba99-409f2f472c24",
				   "resource": "urn:altinn:resource:nav_dokumentdistribusjon_taushetsbelagtpost",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": %s,
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event.formatted(type))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("type: must not be blank");
		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnOkWithBodyWhenTypeIsMissing() {
		var event = """
				{
				   "id": "aa992206-a705-4c27-ba99-409f2f472c24",
				   "resource": "urn:altinn:resource:nav_dokumentdistribusjon_taushetsbelagtpost",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isEqualTo("type: must not be blank");
		assertTopicIsEmpty();
	}

	@Test
	void shouldReturnOkWithBodyWhenMultipleFieldsAreBlank() {
		var event = """
				{
				   "id": null,
				   "resource": "",
				   "resourceinstance": "65d04253-6758-4573-84bb-594129dd34ee",
				   "source": "https://platform.tt02.altinn.no/correspondence/api/v1/correspondence",
				   "specversion": "1.0",
				   "type": "no.altinn.correspondence.correspondencepublished",
				   "subject": "urn:altinn:organization:identifier-no:889640782",
				   "alternativesubject": "/organisation/889640782",
				   "time": "2024-04-19T07:22:19.438039Z"
				}
				""";

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.contentType(APPLICATION_JSON)
				.body(event)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains("id: must not be null", "resource: must not be blank");
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
	void shouldThrowInternalServerErrorWhenSpecversionIsInvalid() {
		AltinnEvent altinnEvent = createValidAltinnEvent(INVALID_VERSION);

		var response = restTestClient.post()
				.uri(WEBHOOK_PATH)
				.body(altinnEvent)
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
						AltinnEvent.builder()
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
						AltinnEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.resource(ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(SPEC_VERSION)
								.build()),
				//invalid resource
				Arguments.of(
						AltinnEvent.builder()
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
						AltinnEvent.builder()
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
						AltinnEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build()),
				//invalid source
				Arguments.of(
						AltinnEvent.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.resourceinstance(RESOURCE_INSTANCE)
								.specversion(VERSION)
								.build())
		);
	}

	private String convertToJson(AltinnEvent altinnEvent) {
		return jsonMapper.writeValueAsString(altinnEvent);
	}
}
