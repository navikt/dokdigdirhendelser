package no.nav.dokdigdirhendelser.altinn;

import no.altinn.event.domain.CloudEvent;
import no.altinn.event.domain.CloudEventAttribute;
import no.altinn.event.domain.CloudEventAttributeType;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;

public final class AltinnEventTestData {

	public static final String INVALID_EVENT_TYPE = "invalid.event.type";
	public static final String INVALID_VERSION = "2.0";
	public static final String INVALID_ALTINN_EVENTS_RESOURCE = "urn:altinn:resource:";
	public static final UUID EVENT_ID = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80d");
	public static final String EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ = "no.altinn.correspondence.correspondencereceiverread";
	public static final UUID RESOURCE_INSTANCE = UUID.fromString("af0e7e0c-579c-4563-9398-10cdf031b80A");
	public static final URI EVENT_SOURCE = URI.create("https://ttd.apps.altinn.no/ttd/apps-test/instances/50015641/a72223a3-926b-4095-a2a6-bacc10815f2d");
	public static final String VERSION = "1.0";
	public static final OffsetDateTime TIME = OffsetDateTime.now();

	private AltinnEventTestData() {
	}

	/**
	 * Creates a valid CloudEvent JSON string in the flat CloudEvents wire format (extensions as top-level properties).
	 * This matches what Altinn actually sends.
	 */
	public static String createValidCloudEventJson(String specVersion) {
		return """
				{
					"id": "%s",
					"source": "%s",
					"type": "%s",
					"time": "%s",
					"resource": "%s",
					"resourceinstance": "%s",
					"specversion": "%s"
				}
				""".formatted(
				EVENT_ID,
				EVENT_SOURCE,
				EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ,
				TIME,
				ALTINN_EVENTS_RESOURCE,
				RESOURCE_INSTANCE.toString().toLowerCase(),
				specVersion
		);
	}

	public static String createValidCloudEventJson() {
		return createValidCloudEventJson(VERSION);
	}

	/**
	 * Builds a CloudEvent JSON string with custom values for parameterized tests.
	 */
	public static String buildCloudEventJson(String type, String resource, String resourceinstance, String specversion) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"id\": \"%s\",".formatted(EVENT_ID));
		sb.append("\"source\": \"%s\",".formatted(EVENT_SOURCE));
		sb.append("\"type\": \"%s\",".formatted(type));
		sb.append("\"time\": \"%s\",".formatted(TIME));
		sb.append("\"resource\": \"%s\",".formatted(resource));
		if (resourceinstance != null) {
			sb.append("\"resourceinstance\": \"%s\",".formatted(resourceinstance));
		}
		sb.append("\"specversion\": \"%s\"".formatted(specversion));
		sb.append("}");
		return sb.toString();
	}

	public static CloudEvent createValidCloudEvent(String specVersion) {
		CloudEvent cloudEvent = new CloudEvent();
		cloudEvent.setId(EVENT_ID.toString());
		cloudEvent.setType(EVENT_TYPE_CORRESPONDENCE_RECEIVER_READ);
		cloudEvent.setTime(TIME);
		cloudEvent.setSource(EVENT_SOURCE);

		List<CloudEventAttribute> extensions = new ArrayList<>();
		extensions.add(createExtension("resource", ALTINN_EVENTS_RESOURCE));
		extensions.add(createExtension("resourceinstance", RESOURCE_INSTANCE.toString()));
		extensions.add(createExtension("specversion", specVersion));
		cloudEvent.setExtensionAttributes(extensions);

		return cloudEvent;
	}

	public static CloudEvent createValidCloudEvent() {
		return createValidCloudEvent(VERSION);
	}

	public static CloudEventAttribute createExtension(String name, String value) {
		CloudEventAttributeType type = new CloudEventAttributeType();
		type.setName(value);

		CloudEventAttribute attr = new CloudEventAttribute();
		attr.setName(name);
		attr.setType(type);
		attr.setIsExtension(true);
		attr.setIsRequired(false);
		return attr;
	}
}
