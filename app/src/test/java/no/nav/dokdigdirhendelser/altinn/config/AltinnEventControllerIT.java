package no.nav.dokdigdirhendelser.altinn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.dokdigdirhendelser.altinn.AltinnEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_ALTERNATIVE_SUBJECT;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

class AltinnEventControllerIT extends AbstractIT {

	private static final UUID EVENT_ID = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80d");
	private static final String EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ = "no.altinn.correspondence.correspondencereceiverread";
	private static final UUID RESOURCE_INSTANCE = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80A");
	private static final URI EVENT_SOURCE = URI.create("https://ttd.apps.altinn.no/ttd/apps-test/instances/50015641/a72223a3-926b-4095-a2a6-bacc10815f2d");
	private static final String VERSION = "1.0";
	private static final OffsetDateTime TIME = OffsetDateTime.now();

	private static final String INVALID_EVENT_TYPE = "invalid.event.type";

	@LocalServerPort
	private int port;

	private RestTestClient restTestClient;

	@BeforeEach
	void setup() {
		this.restTestClient = RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	void shouldReturnAltinnEvents() {
		AltinnEvents altinnEvent = createValidAltinnEvent();

		var response = restTestClient.post()
				.uri("/rest/webhook/path")
				.body(altinnEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(OK);

		AltinnEvents altinnEventReadFromTopic = readFromAltinnEventsTopic();

		assertThat(convertToJson(altinnEvent)).isEqualTo(convertToJson(altinnEventReadFromTopic));

		assertThat(altinnEventReadFromTopic.id()).isEqualTo(EVENT_ID);
		assertThat(altinnEventReadFromTopic.type()).isEqualTo(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ);
		assertThat(altinnEventReadFromTopic.time()).isEqualTo(TIME);
		assertThat(altinnEventReadFromTopic.resource()).isEqualTo(ALTINN_EVENTS_RESOURCE);
		assertThat(altinnEventReadFromTopic.alternativesubject()).isEqualTo(ALTINN_ALTERNATIVE_SUBJECT);
		assertThat(altinnEventReadFromTopic.resourceinstance()).isEqualTo(RESOURCE_INSTANCE);
		assertThat(altinnEventReadFromTopic.source()).isEqualTo(EVENT_SOURCE);
		assertThat(altinnEventReadFromTopic.specversion()).isEqualTo(VERSION);
	}

	@ParameterizedTest
	@MethodSource
	void shouldReturnOKWhenAltinnEventRequestenAreInvalid(AltinnEvents invalidEvent) {
		var response = restTestClient.post()
				.uri("/rest/webhook/path")
				.body(invalidEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(OK);

		AltinnEvents altinnEventReadFromTopic = readFromAltinnEventsTopic();
		assertThat(altinnEventReadFromTopic).isNull();
	}

	private static Stream<Arguments> shouldReturnOKWhenAltinnEventRequestenAreInvalid() {
		return Stream.of(
				Arguments.of(
						AltinnEvents.builder()
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build()),
				Arguments.of(
						AltinnEvents.builder()
								.id(EVENT_ID)
								.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build()),
				Arguments.of(
						AltinnEvents.builder()
								.id(EVENT_ID)
								.type(INVALID_EVENT_TYPE)
								.time(TIME)
								.resource(ALTINN_EVENTS_RESOURCE)
								.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
								.resourceinstance(RESOURCE_INSTANCE)
								.source(EVENT_SOURCE)
								.specversion(VERSION)
								.build())
		);
	}

	private AltinnEvents createValidAltinnEvent() {
		return AltinnEvents.builder()
				.id(EVENT_ID)
				.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
				.time(TIME)
				.resource(ALTINN_EVENTS_RESOURCE)
				.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
				.resourceinstance(RESOURCE_INSTANCE)
				.source(EVENT_SOURCE)
				.specversion(VERSION)
				.build();
	}

	private String convertToJson(AltinnEvents altinnEvent) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			return objectMapper.writeValueAsString(altinnEvent);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert AltinnEvents to JSON", e);
		}
	}
}
