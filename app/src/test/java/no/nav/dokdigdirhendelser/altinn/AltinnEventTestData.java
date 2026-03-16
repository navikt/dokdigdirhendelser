package no.nav.dokdigdirhendelser.altinn;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_ALTERNATIVE_SUBJECT;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;

public final class AltinnEventTestData {

	public static final String INVALID_EVENT_TYPE = "invalid.event.type";
	public static final String INVALID_VERSION = "2.0";
	public static final String INVALID_ALTINN_EVENTS_RESOURCE = "urn:altinn:resource:";
	public static final String INVALID_ALTINN_ALTERNATIVE_SUBJECT = "/organisation/889640798";
	public static final UUID EVENT_ID = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80d");
	public static final String EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ = "no.altinn.correspondence.correspondencereceiverread";
	public static final UUID RESOURCE_INSTANCE = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80A");
	public static final URI EVENT_SOURCE = URI.create("https://ttd.apps.altinn.no/ttd/apps-test/instances/50015641/a72223a3-926b-4095-a2a6-bacc10815f2d");
	public static final String VERSION = "1.0";
	public static final OffsetDateTime TIME = OffsetDateTime.now();

	private AltinnEventTestData() {
	}

	public static AltinnEvent createValidAltinnEvent(String specVersion) {
		return AltinnEvent.builder()
				.id(EVENT_ID)
				.type(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ)
				.time(TIME)
				.resource(ALTINN_EVENTS_RESOURCE)
				.alternativesubject(ALTINN_ALTERNATIVE_SUBJECT)
				.resourceinstance(RESOURCE_INSTANCE)
				.source(EVENT_SOURCE)
				.specversion(specVersion)
				.build();
	}

	public static AltinnEvent createValidAltinnEvent() {
		return createValidAltinnEvent(VERSION);
	}
}
