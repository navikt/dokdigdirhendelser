package no.nav.dokdigdirhendelser.altinn.config;

import no.nav.dokdigdirhendelser.altinn.AltinnEvents;
import no.nav.dokdigdirhendelser.altinn.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;

import static no.nav.dokdigdirhendelser.altinn.EventType.CORRESPONDENCE_RECEIVER_READ;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_ALTERNATIVE_SUBJECT;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

class AltinnEventControllerIT extends AbstractIT {

	private static final String EVENT_ID = "af0e7e0c-579c-4563-9398-10cdf031b80d";
	private static final String EVENT_TYPE = "no.altinn.correspondence.correspondencereceiverread";
	private static final String RESOURCE_INSTANCE = "af0e7e0c-579c-4563-9398-10cdf031b80A";
	private static final String EVENT_SOURCE = "https://ttd.apps.altinn.no/ttd/apps-test/instances/50015641/a72223a3-926b-4095-a2a6-bacc10815f2d";
	private static final String VERSION = "1.0";
	private static final String TIME = Instant.now().toString();

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
		AltinnEvents validEvent = createValidAltinnEvent();

		var response = restTestClient.post()
				.uri("/rest/webhook/path")
				.body(validEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(OK);
		AltinnEvents readFromAltinnEventsTopic = readFromAltinnEventsTopic();

		assertThat(readFromAltinnEventsTopic.id()).isEqualTo(EVENT_ID);
		assertThat(readFromAltinnEventsTopic.type()).isEqualTo(EVENT_TYPE);
		assertThat(readFromAltinnEventsTopic.time()).isEqualTo(TIME);
		assertThat(readFromAltinnEventsTopic.resource()).isEqualTo(ALTINN_EVENTS_RESOURCE);
		assertThat(readFromAltinnEventsTopic.alternativesubject()).isEqualTo(ALTINN_ALTERNATIVE_SUBJECT);
		assertThat(readFromAltinnEventsTopic.resourceinstance()).isEqualTo(RESOURCE_INSTANCE);
		assertThat(readFromAltinnEventsTopic.source()).isEqualTo(EVENT_SOURCE);
		assertThat(readFromAltinnEventsTopic.specversion()).isEqualTo(VERSION);
	}

	@Test
	void shouldReturnOKWhenAltinnEventRequestenAreInvalid() {
		AltinnEvents invalidEvent = AltinnEvents.builder()
				.id(EVENT_ID)
				.type(EventType.CORRESPONDENCE_RECEIVER_CONFIRMED.name())
				.time(TIME)
				.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
				.resourceinstance(RESOURCE_INSTANCE)
				.specversion(VERSION)
				.build();

		var response = restTestClient.post()
				.uri("/rest/webhook/path")
				.body(invalidEvent)
				.exchange()
				.expectStatus().isOk()
				.returnResult();

		assertThat(response.getStatus()).isEqualTo(OK);
	}

	private AltinnEvents createValidAltinnEvent() {
		return AltinnEvents.builder()
				.id(EVENT_ID)
				.type(CORRESPONDENCE_RECEIVER_READ.getValue())
				.time(TIME)
				.resource(ALTINN_EVENTS_RESOURCE)
				.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
				.resourceinstance(RESOURCE_INSTANCE)
				.source(EVENT_SOURCE)
				.specversion(VERSION)
				.build();
	}
}
